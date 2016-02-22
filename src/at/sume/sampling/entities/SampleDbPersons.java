/**
 * 
 */
package at.sume.sampling.entities;

import java.sql.SQLException;

import at.sume.dm.Common;
import at.sume.dm.model.travel.SampleTravelTimesByDistance;
import at.sume.sampling.PersonDistributionAgeSex;
import at.sume.sampling.SamplePersonIncome;
import at.sume.sampling.SampleWorkplaces;
import net.remesch.db.Database;
import net.remesch.db.Sequence;

/**
 * @author Alexander Remesch
 *
 */
public class SampleDbPersons {
	private PersonDistributionAgeSex distributionPersonAgeSex;
	private Sequence personNr = new Sequence();
	private SamplePersonIncome income;
	private int spatialUnitId;
	private Database db;
	private SampleWorkplaces sampleWorkplaces;
	private SampleTravelTimesByDistance sampleTravelTimes;
	private int minIncomeForWorkplace;
	
	public SampleDbPersons(Database db) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException  {
		income = new SamplePersonIncome(db);
		this.db = db;
		// Sampling of workplaces
		sampleWorkplaces = new SampleWorkplaces(db);
		minIncomeForWorkplace = Integer.parseInt(Common.getSysParamDataPreparation("MinIncomeForWorkplace"));
		// Sampling of commuting times & mode
		sampleTravelTimes = new SampleTravelTimesByDistance(db);
	}

	public void setSpatialUnit(int spatialUnitId) throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		distributionPersonAgeSex = new PersonDistributionAgeSex(db, spatialUnitId);
		this.spatialUnitId = spatialUnitId;
		// Workplace sampling: Load commuter matrix for current residential spatial unit
		sampleWorkplaces.loadCommuterMatrix(db, spatialUnitId);
		// Commuting time & mode sampling: Load travel time information
		sampleTravelTimes.loadTravelTimes(spatialUnitId);
	}
	/**
	 * Sample a single person including their time use
	 * @param householdId
	 * @param householdRepresentative
	 * @return
	 * @throws SQLException
	 */
	public DbPersonRow randomSample(int householdId, boolean householdRepresentative) throws SQLException {
		DbPersonRow result = new DbPersonRow();
		result.setPersonId(personNr.getNext());
		result.setHouseholdId(householdId);
		distributionPersonAgeSex.randomSample(householdRepresentative);
		result.setAge(distributionPersonAgeSex.getSampledAge());
		result.setSex(distributionPersonAgeSex.getSampledSex());
		// Yearly income
		income.loadDistribution(spatialUnitId, result.getSex(), distributionPersonAgeSex.getSampledAgeGroupId());
		result.setYearlyIncome(income.determineIncome());
		// TODO: Work place (matching income & age!)
		if (result.getYearlyIncome() >= minIncomeForWorkplace) { 
			result.setInEducation(false);
			// set workplace
			result.setWorkplaceId(sampleWorkplaces.randomSample());
		} else  if (result.getAge() >= 6 && result.getAge() <= 18) {
			result.setInEducation(true);
			result.setWorkplaceId(sampleWorkplaces.randomSample());
		}	
		return result;
	}
}
