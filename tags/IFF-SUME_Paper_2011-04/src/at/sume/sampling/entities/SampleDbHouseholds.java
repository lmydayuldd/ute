/**
 * 
 */
package at.sume.sampling.entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import net.remesch.db.Database;
import net.remesch.db.Sequence;
import at.sume.dm.Common;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.Dwellings;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.model.residential_mobility.DwellingsOnMarket;
import at.sume.dm.types.LivingSpaceGroup6;
import at.sume.sampling.SampleHouseholdCostOfResidence;
import at.sume.sampling.SampleHouseholdLivingSpace;
import at.sume.sampling.distributions.HouseholdsPerSpatialUnit;

/**
 * This class includes the complete household sampling.
 * 
 * @author Alexander Remesch
 */
public class SampleDbHouseholds {
	private SampleDbPersons sampleDbPersons;
	private Sequence householdNr = new Sequence();
	private int spatialUnitId;
	private ArrayList<DbPersonRow> members;
	private SampleHouseholdLivingSpace sampleLivingSpace;
	private Database db;
	private SpatialUnits spatialUnits;
	private Dwellings dwellings;
	private DwellingsOnMarket dwellingsOnMarket;
	private int residentialSatisfactionThresholdRange = 100;
	private byte householdSizeGroups;
	private SampleHouseholdCostOfResidence householdCostOfResidence;
	
	/**
	 * Constructor - loads the spatial units, the dwellings, links them and creates a list of free
	 * dwellings. Also prepares distributions for sampling the household living space from the number of
	 * household members. All that will be used later on in the household sampling process.
	 * 
	 * TODO: this class should be better separated from GeneratePopulation
	 * 
	 * @param db
	 * @param householdSizeGroups Number of household-size groups used in the database (usually a system parameter)
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public SampleDbHouseholds(Database db, byte householdSizeGroups, int dwellingsOnMarketShare) throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException  {
		this.db = db;
		this.householdSizeGroups = householdSizeGroups;
		// Load spatial units - these are not sampled
		spatialUnits = new SpatialUnits(db, Common.getSpatialUnitLevel());
        System.out.println(Common.printInfo() + ": loaded " + spatialUnits.size() + " spatial units");
		// Load dwellings - these are not sampled because we have real data from 2001
		dwellings = new Dwellings(db, Common.getSpatialUnitLevel());
        System.out.println(Common.printInfo() + ": loaded " + dwellings.size() + " dwellings");
		// Link dwellings to spatial units
		dwellings.linkSpatialUnits(spatialUnits);
        System.out.println(Common.printInfo() + ": linked dwellings + spatial units");
		// Determine all dwellings on the market
        // TODO: it would be better to use the absolute number of households in a spatial unit as a guide for how many dwellings to put on the market (instead of dwellingsOnMarketShare)
        dwellingsOnMarket = new DwellingsOnMarket(dwellings, spatialUnits, dwellingsOnMarketShare);
        System.out.println(Common.printInfo() + ": determined all available dwellings on the housing market");
		
		sampleDbPersons = new SampleDbPersons(db);

		// Preparation of sampling of cost of residence from the living space
		householdCostOfResidence = new SampleHouseholdCostOfResidence(db);
	}
	/**
	 * Set spatial unit specific parameters for household/person sampling
	 * @param spatialUnitId
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public void setSpatialUnit(int spatialUnitId) throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		this.spatialUnitId = spatialUnitId;
		sampleDbPersons.setSpatialUnit(spatialUnitId);
		sampleLivingSpace = new SampleHouseholdLivingSpace(db, spatialUnitId, householdSizeGroups);
	}
	/**
	 * Set the range for the sampling of the residential satisfaction threshold modifier that 
	 * attempts to include individual deviations and unknown variance in the residential satisfaction
	 * calculation
	 * @param residentialSatisfactionThresholdRange
	 */
	public void setResidentialSatisfactionThresholdRange(int residentialSatisfactionThresholdRange) {
		this.residentialSatisfactionThresholdRange = residentialSatisfactionThresholdRange;
	}
	/**
	 * Return a sampled household (excluding the household members)
	 * @param householdSize
	 * @return
	 * @throws SQLException
	 */
	public DbHouseholdRow randomSample(HouseholdsPerSpatialUnit householdsPerSpatialUnit, int alreadySampledHouseholdsCount) throws SQLException {
		DbHouseholdRow result = new DbHouseholdRow();
		// Household number
		result.setHouseholdId(householdNr.getNext());
		// Household spatial unit
		result.setSpatialUnitId(spatialUnitId);
		// Persons: age, sex & income
		members = new ArrayList<DbPersonRow>();
		int memberCount = 1;
		if (householdsPerSpatialUnit.householdSize < householdSizeGroups) {
			// sample given household size
			memberCount = householdsPerSpatialUnit.householdSize;
		} else if (householdsPerSpatialUnit.householdSize >= householdSizeGroups) {
			// sample a household size that might be larger or an institutional household
			int surplusPersonCount = householdsPerSpatialUnit.personCount % ((householdsPerSpatialUnit.householdCount - alreadySampledHouseholdsCount) * householdsPerSpatialUnit.householdSize);
			if (surplusPersonCount <= 0) {
				// No households larger than householdSizeGroups persons left
				memberCount = householdsPerSpatialUnit.householdSize;
			} else {
				// Larger households still available
				Random r = new Random();
				memberCount = householdsPerSpatialUnit.householdSize;
				// TODO: maximum number of household members: 10 - put into system parameters
				for (int i = householdsPerSpatialUnit.householdSize; i != 11; i++) {
					memberCount += r.nextInt(2);
				}
				if (memberCount > surplusPersonCount) {
					memberCount = surplusPersonCount;
				}
			}
			householdsPerSpatialUnit.personCount -= memberCount;
		}
		assert (memberCount > 0) && (memberCount <= 255) : "Household member count out of range (" + memberCount + ")";
		result.setHouseholdSize((byte)memberCount);
		int yearlyHouseholdIncome = 0;
		for (byte j = 0; j != memberCount; j++) {
			DbPersonRow person = sampleDbPersons.randomSample(result.getHouseholdId(), (j == 0));
			members.add(person);
			yearlyHouseholdIncome += person.getYearlyIncome();
		}
		// Living space - find a suitable dwelling
		DwellingRow dwelling = null;
		byte livingSpaceGroupCount = LivingSpaceGroup6.getLivingSpaceGroupCount();
		short livingSpace = 0;
		while (dwelling == null) {
			livingSpace = sampleLivingSpace.randomSample(householdsPerSpatialUnit.householdSize);
			byte livingSpaceGroup6Id = LivingSpaceGroup6.getLivingSpaceGroupId(livingSpace);
			while ((dwelling == null) && (livingSpaceGroup6Id <= livingSpaceGroupCount)) {
				dwelling = dwellingsOnMarket.getFirstMatchingDwelling(spatialUnitId, livingSpaceGroup6Id);
				if (dwelling == null) {
//					System.out.println(Common.printInfo() + ": no dwelling with " + livingSpace + "m� (class: " + LivingSpaceGroup6.getLivingSpaceGroupName(livingSpaceGroup6Id) + ") in spatial unit " + spatialUnitId + " anymore");
					livingSpaceGroup6Id++;
				}
//				assert livingSpaceGroup6Id <= LivingSpaceGroup6.getLivingSpaceGroupCount() : "Living space group " + livingSpaceGroup6Id + " out of range";
			}
			if (dwelling == null) {
				System.out.println(Common.printInfo() + ": no dwelling with " + livingSpace + "m� in spatial unit " + spatialUnitId + " anymore");
			}
		}
		result.setDwellingId(dwelling.getDwellingId());
		result.setLivingSpace(livingSpace);
		dwellingsOnMarket.removeDwellingFromMarket(dwelling);
		// Residential satisfaction threshold modifier
		Random r = new Random();
		result.setResidentialSatisfactionThreshMod((short) Math.round(r.nextGaussian() * residentialSatisfactionThresholdRange));
		// Cost of residence
		result.setCostOfResidence(householdCostOfResidence.randomSample(yearlyHouseholdIncome) * result.getLivingSpace() / 100);
		
		return result;
	}
	/**
	 * Return the members of the previously sampled household
	 * @return
	 */
	public ArrayList<DbPersonRow> getSampledMembers() {
		return members;
	}
}
