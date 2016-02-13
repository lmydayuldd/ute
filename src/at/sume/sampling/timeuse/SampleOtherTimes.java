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
 * Sample personal time use for 'other' time, i.e. employment, education & some small remaining activities
 * @author Alexander Remesch
 */
public class SampleOtherTimes {
	private Distribution<TimeUseDistributionRow> otherTimeDistrInEdu, otherTimeDistrEmployed, otherTimeDistrNoWorkNoEdu;
	private static final String timeUseTag = "other";
	
	public SampleOtherTimes(Database db) throws InstantiationException, IllegalAccessException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'in education' " +
				"ORDER BY Time;";
		List<TimeUseDistributionRow> timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		otherTimeDistrInEdu = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
		sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'working' " +
				"ORDER BY Time;";
		timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		otherTimeDistrEmployed = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
		sqlStatement = "SELECT Time, Share FROM _UTE_TimeUseDistributions " +
				"WHERE TimeUse = '" + timeUseTag + "' AND Condition = 'not working & not in education' " +
				"ORDER BY Time;";
		timeDistr = db.select(TimeUseDistributionRow.class, sqlStatement);
		assert timeDistr.size() > 0 : "No records found from '" + sqlStatement + "'";
		otherTimeDistrNoWorkNoEdu = new Distribution<TimeUseDistributionRow>(timeDistr, "share");
	}

	public double randomSample(boolean inEducation, boolean working) {
		TimeUseDistributionRow result;
		if (inEducation) {
			result = otherTimeDistrInEdu.get(otherTimeDistrInEdu.randomSample());
		} else if (working) {
			result = otherTimeDistrEmployed.get(otherTimeDistrEmployed.randomSample());
		} else {
			result = otherTimeDistrNoWorkNoEdu.get(otherTimeDistrEmployed.randomSample());
		}
		return result.time;
	}
	public DbTimeUseRow randomSample(int personId, boolean inEducation, boolean working) {
		return new DbTimeUseRow(personId, timeUseTag, randomSample(inEducation, working));
	}
}
