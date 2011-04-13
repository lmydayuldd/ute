/**
 * 
 */
package at.sume.sampling;

import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.db.Database;
import at.sume.dm.types.CostOfResidenceGroup;
import at.sume.sampling.distributions.CostOfResidenceDistributionRow;

/**
 * Sample household cost of residence from the view [_DM_Cost of residence per income group] based on the
 * household income
 * 
 * @author Alexander Remesch
 */
public class SampleHouseholdCostOfResidence {
	ArrayList<Distribution<CostOfResidenceDistributionRow>> costOfResidencePerIncomeGroup = new ArrayList<Distribution<CostOfResidenceDistributionRow>>();
	
	/**
	 * @param db
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public SampleHouseholdCostOfResidence(Database db) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String sqlStatement = "SELECT * FROM [_DM_Cost of residence per income group] ORDER BY IncomeGroupId, CostOfResidenceGroupId";
		ArrayList<CostOfResidenceDistributionRow> costOfResidenceDistribution = db.select(CostOfResidenceDistributionRow.class, sqlStatement); 
		assert costOfResidenceDistribution.size() > 0 : "No records found from '" + sqlStatement + "'";
		int prevIncomeGroupId = 0;
		// Split into separate distributions by incomeGroupId
		ArrayList<CostOfResidenceDistributionRow> e = null;
		for (int i = 0; i != costOfResidenceDistribution.size(); i++) {
			CostOfResidenceDistributionRow row = costOfResidenceDistribution.get(i);
			if (prevIncomeGroupId != row.incomeGroupId) {
				if (i != 0) {
					costOfResidencePerIncomeGroup.add(new Distribution<CostOfResidenceDistributionRow>(e, "countWeighted"));
				}
				e = new ArrayList<CostOfResidenceDistributionRow>();
				e.add(row);
				prevIncomeGroupId = row.incomeGroupId;
			} else {
				e.add(row);
			}
		}
		if (e != null)
			costOfResidencePerIncomeGroup.add(new Distribution<CostOfResidenceDistributionRow>(e, "countWeighted"));
	}
// Removed this, because we work with a shortened income group list in this class that is incompatible with the
// standard IncomeGroupList used otherwise in this project
//	/**
//	 * Sample household cost of residence based on a given income group id
//	 * @param incomeGroupId Income group (yearly household income)
//	 * @return The sampled cost of residence in € per 100 m² and year
//	 */
//	public int randomSample(byte incomeGroupId) {
//		assert incomeGroupId >= 1 : "Invalid incomeGroupId = " + incomeGroupId;
//		if (incomeGroupId > costOfResidencePerIncomeGroup.size())
//			incomeGroupId = (byte) costOfResidencePerIncomeGroup.size();
//		CostOfResidenceDistributionRow result = costOfResidencePerIncomeGroup.get(incomeGroupId - 1).get(costOfResidencePerIncomeGroup.get(incomeGroupId - 1).randomSample());
//		return (int) Math.round(CostOfResidenceGroup.sampleCostOfResidence(result.costOfResidenceGroupId) * 1200);
//	}
	/**
	 * Sample household cost of residence based on a given household income
	 * @param yearlyHouseholdIncome Yearly household income
	 * @return The sampled cost of residence in € per 100 m² and year
	 */
	public int randomSample(int yearlyHouseholdIncome) {
		assert yearlyHouseholdIncome >= 0 : "Invalid yearlyHouseholdIncome = " + yearlyHouseholdIncome;
		for (Distribution<CostOfResidenceDistributionRow> distribution : costOfResidencePerIncomeGroup) {
			if ((distribution.get(0).minIncome <= yearlyHouseholdIncome) && (distribution.get(0).maxIncome >= yearlyHouseholdIncome)) {
				CostOfResidenceDistributionRow result = distribution.get(distribution.randomSample());
				return (int) Math.round(CostOfResidenceGroup.sampleCostOfResidence(result.costOfResidenceGroupId) * 1200);
			}
		}
		return 0;
	}
}
