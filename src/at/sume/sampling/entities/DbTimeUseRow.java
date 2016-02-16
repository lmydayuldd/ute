/**
 * 
 */
package at.sume.sampling.entities;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import net.remesch.db.schema.Ignore;

/**
 * @author Alexander Remesch
 *
 */
public class DbTimeUseRow {
	private int id;
	private int personId;
	private String activity;
	private int minutesPerDay;

	@Ignore
	private static int nextid = 1;
	
	public DbTimeUseRow() {
	}
	public DbTimeUseRow(int personId, String activity, int minutesPerDay) {
		id = nextid++;
		this.personId = personId;
		this.activity = activity;
		this.minutesPerDay = minutesPerDay;
	}
	public DbTimeUseRow(int personId, String activity, double hoursPerDay) {
		id = nextid++;
		this.personId = personId;
		this.activity = activity;
		this.minutesPerDay = (int) Math.round(hoursPerDay * 60);
	}
	public int getId() {
		return id;
	}
	/**
	 * @return the personId
	 */
	public int getPersonId() {
		return personId;
	}
	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	/**
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}
	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}
	/**
	 * @return the minutesPerDay
	 */
	public int getMinutesPerDay() {
		return minutesPerDay;
	}
	public double getHoursPerDay() {
		return (double)minutesPerDay / 60;
	}
	/**
	 * @param minutesPerDay the minutesPerDay to set
	 */
	public void setMinutesPerDay(int minutesPerDay) {
		this.minutesPerDay = minutesPerDay;
	}
	
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		return "(" + id + ") " + activity + " - " + minutesPerDay + " min - " + df.format(getHoursPerDay()) + " h";
	}
}
