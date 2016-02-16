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
		df.setRoundingMode(RoundingMode.CEILING);
		return activity + " - " + avgTimeUse + " min - " + df.format(getHoursPerDay()) + " h";
	}
}
