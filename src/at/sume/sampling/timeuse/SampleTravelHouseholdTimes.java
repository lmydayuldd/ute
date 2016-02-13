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
public class SampleTravelHouseholdTimes {
	private static final String timeUseTag = "travel hh";
	
	private Distribution<TimeUseDistributionRow> timeDistrMale, timeDistrFemale;
	
	public SampleTravelHouseholdTimes(Database db) throws InstantiationException, IllegalAccessException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'male' " +
				"ORDER BY Time;";
		List<TimeUseDistributionRow> timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		timeDistrMale = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
		sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'female' " +
				"ORDER BY Time;";
		timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		timeDistrFemale = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
	}

	public double randomSample(int gender) {
		TimeUseDistributionRow result;
		switch (gender) {
		case 1:
			result = timeDistrMale.get(timeDistrMale.randomSample());
			break;
		case 2:
			result = timeDistrFemale.get(timeDistrFemale.randomSample());
			break;
		default:
			throw new IllegalArgumentException("Invalid gender given in " + this.getClass().getName() + ".randomSample: " + gender);
		}
		return result.time;
	}
	public DbTimeUseRow randomSample(int personId, int gender) {
		return new DbTimeUseRow(personId, timeUseTag, randomSample(gender));
	}
}
