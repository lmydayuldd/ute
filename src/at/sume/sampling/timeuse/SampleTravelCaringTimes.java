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
public class SampleTravelCaringTimes {
	private Distribution<TimeUseDistributionRow> travelCaringTimeDistrChild, travelCaringTimeDistrNoChild;
	private static final String timeUseTag = "travel caring";
	
	public SampleTravelCaringTimes(Database db) throws InstantiationException, IllegalAccessException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'Child under 15 in the household' " +
				"ORDER BY Time;";
		List<TimeUseDistributionRow> timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		travelCaringTimeDistrChild = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
		sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'No child under 15 in the household' " +
				"ORDER BY Time;";
		timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		travelCaringTimeDistrNoChild = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
	}

	public double randomSample(boolean householdWithChildren) {
		TimeUseDistributionRow result;
		if (householdWithChildren) {
			result = travelCaringTimeDistrChild.get(travelCaringTimeDistrChild.randomSample());
		} else {
			result = travelCaringTimeDistrNoChild.get(travelCaringTimeDistrNoChild.randomSample());
		}
		return result.time;
	}

	public DbTimeUseRow randomSample(int personId, boolean householdWithChildren) {
		return new DbTimeUseRow(personId, timeUseTag, randomSample(householdWithChildren));
	}
}
