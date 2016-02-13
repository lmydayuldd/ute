/**
 * 
 */
package at.sume.sampling.entities;

/**
 * @author Alexander Remesch
 *
 */
public class DbTimeUseRow {
	private int personId;
	private String activity;
	private int minutesPerDay;
	
	public DbTimeUseRow() {
	}
	public DbTimeUseRow(int personId, String activity, int minutesPerDay) {
		this.personId = personId;
		this.activity = activity;
		this.minutesPerDay = minutesPerDay;
	}
	public DbTimeUseRow(int personId, String activity, double hoursPerDay) {
		this.personId = personId;
		this.activity = activity;
		this.minutesPerDay = (int) Math.round(hoursPerDay * 60);
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
	/**
	 * @param minutesPerDay the minutesPerDay to set
	 */
	public void setMinutesPerDay(int minutesPerDay) {
		this.minutesPerDay = minutesPerDay;
	}
}
