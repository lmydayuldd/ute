/**
 * 
 */
package at.sume.sampling.timeuse;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import at.sume.dm.Common;
import at.sume.sampling.entities.DbTimeUseRow;
import net.remesch.db.Database;

/**
 * This class is responsible for checking the average target time use per activity during the parameterization
 * of the time use of all agents in the model.
 * 
 * @author Alexander Remesch
 */
public class TimeUseTargetCheck {
	private HashMap<String,Integer> avgTargetTimeUse;
	private HashMap<String,Long> actualTotalTimeUse;
	private HashMap<String,Integer> participantCount;
	
	private double maxDeviationPercent = 0;
	/**
	 * 
	 * @param db
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public TimeUseTargetCheck(Database db) throws InstantiationException, IllegalAccessException, SQLException {
		maxDeviationPercent = Byte.parseByte(Common.getSysParamDataPreparation("MaxTimeUseDeviationPercent"));
		String sqlStatement = "SELECT Activity, AvgTimeUse " +
				"FROM _UTE_TimeUseTargetAvg " +
				"WHERE Scale = 'Vienna' " +
				"ORDER BY Activity;";
		List<TimeUseRow> tl = db.select(TimeUseRow.class, sqlStatement);
		assert tl.size() > 0 : "No records found from '" + sqlStatement + "'";
		avgTargetTimeUse = new HashMap<String,Integer>();
		for (TimeUseRow t : tl) {
			avgTargetTimeUse.put(t.activity, (int) Math.round(t.avgTimeUse * 60));
		}
		actualTotalTimeUse = new HashMap<String, Long>();
		participantCount = new HashMap<String, Integer>();
	}
	/**
	 * 
	 * @param activity
	 * @return
	 */
	public double getTargetTimeUse(String activity) {
		return avgTargetTimeUse.get(activity);
	}
	/**
	 * 
	 * @param timeUse
	 */
	public void addActualTimeUse(List<DbTimeUseRow> timeUse) {
		for (DbTimeUseRow r : timeUse) {
			long oldTimeUse = 0;
			if (actualTotalTimeUse.containsKey(r.getActivity())) {
				oldTimeUse = actualTotalTimeUse.get(r.getActivity());
				actualTotalTimeUse.replace(r.getActivity(), oldTimeUse + r.getMinutesPerDay());
				participantCount.replace(r.getActivity(), participantCount.get(r.getActivity()) + 1);
			} else {
				actualTotalTimeUse.put(r.getActivity(), (long) r.getMinutesPerDay());
				participantCount.put(r.getActivity(), 1);
			}
		}
	}
	/**
	 * 
	 * @param activity
	 * @param newTimeUse
	 * @return
	 */
	public boolean belowTargetRange(String activity, int newTimeUse) {
		if (!actualTotalTimeUse.containsKey(activity))
			return false;
		long newTotalTimeUse = actualTotalTimeUse.get(activity) + newTimeUse;
		long lowerLimit = Math.round(avgTargetTimeUse.get(activity) * participantCount.get(activity) * (100 - maxDeviationPercent) / 100);
		if (newTotalTimeUse < lowerLimit)
			return true;
		else
			return false;
	}
	public boolean belowTargetRange(DbTimeUseRow t) {
		return belowTargetRange(t.getActivity(), t.getMinutesPerDay());
	}
	/**
	 * 
	 * @param activity
	 * @param newTimeUse
	 * @return
	 */
	public boolean aboveTargetRange(String activity, int newTimeUse) {
		if (!actualTotalTimeUse.containsKey(activity))
			return false;
		long newTotalTimeUse = actualTotalTimeUse.get(activity) + newTimeUse;
		long upperLimit = Math.round(avgTargetTimeUse.get(activity) * participantCount.get(activity) * (100 + maxDeviationPercent) / 100);
		if (newTotalTimeUse > upperLimit)
			return true;
		else
			return false;
	}
	public boolean abowTargetRange(DbTimeUseRow t) {
		return aboveTargetRange(t.getActivity(), t.getMinutesPerDay());
	}
}
