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
 * Sample household times depending on other (emp & edu) times:
 * other0 ... other = 0
 * other1 ... other > 0 and < 2
 * other2 ... other >= 2 and < 5
 * other3 ... other >= 5
 * 
 * @author Alexander Remesch
 *
 */
public class SampleHouseholdTimes {
	private static final String timeUseTag = "household";
	
	private Distribution<TimeUseDistributionRow> timeDistrOther0, timeDistrOther1, timeDistrOther2, timeDistrOther3;
	
	public SampleHouseholdTimes(Database db) throws InstantiationException, IllegalAccessException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'other = 0' " +
				"ORDER BY Time;";
		List<TimeUseDistributionRow> timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		timeDistrOther0 = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
		sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'other >0 & <2' " +
				"ORDER BY Time;";
		timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		timeDistrOther1 = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
		sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'other >=2 & <5' " +
				"ORDER BY Time;";
		timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		timeDistrOther2 = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
		sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'other >=5' " +
				"ORDER BY Time;";
		timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		timeDistrOther3 = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
	}

	public double randomSample(double other) {
		TimeUseDistributionRow result;
		if (other == 0)
			result = timeDistrOther0.get(timeDistrOther0.randomSample());
		else if (other > 0 && other < 2)
			result = timeDistrOther1.get(timeDistrOther1.randomSample());
		else if (other >= 2 && other < 5)
			result = timeDistrOther2.get(timeDistrOther2.randomSample());
		else if (other >= 5)
			result = timeDistrOther3.get(timeDistrOther3.randomSample());
		else
			throw new IllegalArgumentException("Invalid amount for other time given in " + this.getClass().getName() + ".randomSample: " + other);
		return result.time;
	}
	public DbTimeUseRow randomSample(int personId, double other) {
		return new DbTimeUseRow(personId, timeUseTag, randomSample(other));
	}
}
