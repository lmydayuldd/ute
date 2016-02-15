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
	private Distribution<TimeUseDistributionRow> travelCaringTimeDistr;
	private static final String timeUseTag = "travel caring";
	
	public SampleTravelCaringTimes(Database db) throws InstantiationException, IllegalAccessException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' " +
				"ORDER BY Time;";
		List<TimeUseDistributionRow> timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		travelCaringTimeDistr = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
	}

	public double randomSample() {
		TimeUseDistributionRow result = travelCaringTimeDistr.get(travelCaringTimeDistr.randomSample());
		return result.time;
	}

	public DbTimeUseRow randomSample(int personId) {
		return new DbTimeUseRow(personId, timeUseTag, randomSample());
	}
}
