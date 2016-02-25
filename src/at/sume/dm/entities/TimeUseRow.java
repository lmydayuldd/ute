/**
 * 
 */
package at.sume.dm.entities;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import at.sume.dm.model.timeuse.SampleActivity;
import at.sume.sampling.entities.DbTimeUseRow;

/**
 * @author Alexander Remesch
 *
 */
public class TimeUseRow {
	public String activity;
	public int avgTimeUse;

	public TimeUseRow(DbTimeUseRow row) {
		activity = row.getActivity();
		avgTimeUse = row.getMinutesPerDay();
	}
	public TimeUseRow(SampleActivity sampleActivity) {
		activity = sampleActivity.getActivityName();
		avgTimeUse = sampleActivity.sampleMinutesPerDay();
	}
	public double getHoursPerDay() {
		return (double)avgTimeUse / 60;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat("#.##");
		DecimalFormat df2 = new DecimalFormat("#");
		df.setRoundingMode(RoundingMode.CEILING);
		return activity + " - " + df2.format(avgTimeUse) + " min - " + df.format(getHoursPerDay()) + " h";
	}
}
