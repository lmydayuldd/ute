/**
 * 
 */
package at.sume.sampling.timeuse;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author Alexander Remesch
 *
 */
public class TimeUseRow {
	public String activity;
	public double avgTimeUse;

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
