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
public class SampleTravelLeisureTimes {
	private static final String timeUseTag = "travel leisure";
	
	private Distribution<TimeUseDistributionRow> timeDistr;
	
	public SampleTravelLeisureTimes(Database db) throws InstantiationException, IllegalAccessException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' " +
				"ORDER BY Time;";
		List<TimeUseDistributionRow> t = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert t.size() > 0 : "No records found from '" + sqlStatement + "'";
		timeDistr = new Distribution<TimeUseDistributionRow>(t, "share");
	}

	public double randomSample() {
		TimeUseDistributionRow result = timeDistr.get(timeDistr.randomSample());
		return result.time;
	}
	public DbTimeUseRow randomSample(int personId) {
		return new DbTimeUseRow(personId, timeUseTag, randomSample());
	}
}
