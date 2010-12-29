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
 * Sample living space for a household from the table _DM_Living space per household size based on
 * the household size
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
	public SampleHouseholdLivingSpace(Database db, byte householdSizeGroups) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		livingSpacePerHouseholdSize = (Distribution<LivingSpaceDistributionRow>[])new Distribution[householdSizeGroups];
		for (int i = 0; i != householdSizeGroups; i++) {
			String sqlStatement = "select HouseholdSize, LivingSpaceGroup, HouseholdCount " + 
				"from [_DM_Living space per household size] where HouseholdSize = " + (i + 1) +
				" order by LivingSpaceGroup";
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
