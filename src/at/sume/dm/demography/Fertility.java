/**
 * 
 */
package at.sume.dm.demography;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import net.remesch.db.Database;

/**
 * Implementation of fertility probability per age and sex
 * 
 * @author Alexander Remesch
 */
public class Fertility {
	private ArrayList<FertilityProbabilityRow> pList;
	
	/**
	 * @param db
	 * @throws SQLException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Fertility(Database db, String scenarioName) throws SQLException, InstantiationException, IllegalAccessException {
		String sqlStatement = "select ageGroupId, Fertility/1000 AS ProbabilityBirth " +
			"from _DM_FertilityAgeHouseholdSize " +
			"where fertilityScenarioName = '" + scenarioName + "' " +
			"order by ageGroupId";
		pList = db.select(FertilityProbabilityRow.class, sqlStatement);
	}

	/**
	 * Return the probability of birth for females in a given age-group and living in a household of a certain size
	 * @param ageGroupId
	 * @return
	 */
	public double probability(byte ageGroupId) {
		FertilityProbabilityRow lookup = new FertilityProbabilityRow();
		lookup.setAgeGroupId(ageGroupId);
		int index = Collections.binarySearch(pList, lookup);
		if (index >= 0) {
			return pList.get(index).getProbabilityBirth();
		} else {
			return 0;
		}
	}
}
