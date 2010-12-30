/**
 * 
 */
package at.sume.sampling;

import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.db.Database;
import at.sume.dm.types.LivingSpaceGroup8;
import at.sume.sampling.distributions.LivingSpaceDistributionRow;

/**
 * Sample living space for a household from the table [_DM_Living space per spatial unit, household size] 
 * (created by view [Wohnungen + Haushalte nach ZB, Wohnfläche, HH-Größe]) based on the household size and the spatial unit id
 * 
 * Note: this class used the table [_DM_Living space per household size] for the distribution previously but this had
 * the major drawback that some dwelling sizes were over-/underrepresented in certain spatial units. Therefore e.g. no middle
 * sized dwellings (45-90m²) were left after creation of the synthetic population in spatial unit 90101 opposed to the majority
 * of large dwellings (> 110m²) being vacant. The new distribution is based on expected values for households per spatial unit,
 * household size class and living space class and leads to a more even distribution. 
 * 
 * @author Alexander Remesch
 */
public class SampleHouseholdLivingSpace {
	Distribution<LivingSpaceDistributionRow> livingSpacePerHouseholdSize[];
	
	/**
	 * TODO: get householdSizeGroups from data not as a parameter
	 * 
	 * @param db
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	@SuppressWarnings("unchecked")
	public SampleHouseholdLivingSpace(Database db, int spatialUnitId, byte householdSizeGroups) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		livingSpacePerHouseholdSize = (Distribution<LivingSpaceDistributionRow>[])new Distribution[householdSizeGroups];
		for (int i = 0; i != householdSizeGroups; i++) {
			String sqlStatement;
			if (i != householdSizeGroups - 1) {
				sqlStatement = "select HouseholdSize, LivingSpaceGroupId as LivingSpaceGroup, HhDwCount as HouseholdCount " + 
					"from [_DM_Living space per spatial unit, household size] " + 
					"where HouseholdSize = " + (i + 1) + " and SpatialUnitId = " + spatialUnitId +
					" order by LivingSpaceGroupId";
			} else {
				sqlStatement = "select " + (i + 1) + " as HouseholdSize, LivingSpaceGroupId as LivingSpaceGroup, sum(HhDwCount) as HouseholdCount " + 
					"from [_DM_Living space per spatial unit, household size] " + 
					"where HouseholdSize >= " + (i + 1) + " and SpatialUnitId = " + spatialUnitId +
					" group by " + (i + 1) + ", LivingSpaceGroupId " +
					"order by LivingSpaceGroupId";
			}
			ArrayList<LivingSpaceDistributionRow> livingSpaceDistribution = db.select(LivingSpaceDistributionRow.class, sqlStatement);
			assert livingSpaceDistribution.size() > 0 : "No records found from '" + sqlStatement + "'";
			livingSpacePerHouseholdSize[i] = new Distribution<LivingSpaceDistributionRow>(livingSpaceDistribution, "householdCount");
		}
	}

	public short randomSample(byte householdSize) {
//		assert (householdSize >= 1) && (householdSize <= livingSpacePerHouseholdSize.length) : "Invalid householdSize = " + householdSize;
		assert householdSize >= 1 : "Invalid householdSize = " + householdSize;
		if (householdSize > livingSpacePerHouseholdSize.length)
			householdSize = (byte) livingSpacePerHouseholdSize.length;
		LivingSpaceDistributionRow result = livingSpacePerHouseholdSize[householdSize - 1].get(livingSpacePerHouseholdSize[householdSize - 1].randomSample());
		return LivingSpaceGroup8.sampleLivingSpace(result.livingSpaceGroup);
	}
}
