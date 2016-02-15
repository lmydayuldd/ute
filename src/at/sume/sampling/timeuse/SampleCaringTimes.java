/**
 * 
 */
package at.sume.sampling.timeuse;

import java.sql.SQLException;
import java.util.List;

import at.sume.sampling.Distribution;
import at.sume.sampling.distributions.TimeUseDistributionRow;
import at.sume.sampling.entities.DbTimeUseRow;
import net.remesch.db.Database;

/**
 * @author Alexander Remesch
 *
 */
public class SampleCaringTimes {
	private Distribution<TimeUseDistributionRow> caringTimeDistrChildMale, caringTimeDistrChildFemale, caringTimeDistrNoChild;
	private static final String timeUseTag = "caring";
	
	public SampleCaringTimes(Database db) throws InstantiationException, IllegalAccessException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'child < 15 in hh & male' " +
				"ORDER BY Time;";
		List<TimeUseDistributionRow> timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		caringTimeDistrChildMale = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
		sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'child < 15 in hh & female' " +
				"ORDER BY Time;";
		timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		caringTimeDistrChildFemale = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
		sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'no child < 15 in hh' " +
				"ORDER BY Time;";
		timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		caringTimeDistrNoChild = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
	}

	public double randomSample(boolean householdWithChildren, int gender) {
		TimeUseDistributionRow result;
		if (householdWithChildren) {
			if (gender == 1)
				result = caringTimeDistrChildMale.get(caringTimeDistrChildMale.randomSample());
			else
				result = caringTimeDistrChildFemale.get(caringTimeDistrChildFemale.randomSample());
		} else {
			result = caringTimeDistrNoChild.get(caringTimeDistrNoChild.randomSample());
		}
		return result.time;
	}
	
	public DbTimeUseRow randomSample(int personId, boolean householdWithChildren, int gender) {
		return new DbTimeUseRow(personId, timeUseTag, randomSample(householdWithChildren, gender));
	}
}
