/**
 * 
 */
package at.sume.sampling.entities;

import java.util.List;

import net.remesch.db.schema.Ignore;

/**
 * @author Alexander Remesch
 *
 */
public class DbPersonRow {
	private int personId;
	private int householdId;
	private byte sex;
	private short age;
	private int yearlyIncome;
	private int workplaceId;				// spatial unit id of workplace
	private int travelModeCommuting;	// travel mode for commuting (if the person commutes)
	@Ignore
	private List<DbTimeUseRow> timeUse;		// time use of person
	@Ignore
	private boolean inEducation;
	
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
	 * @return the householdId
	 */
	public int getHouseholdId() {
		return householdId;
	}
	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
	}
	/**
	 * @return the sex
	 */
	public byte getSex() {
		return sex;
	}
	/**
	 * @param sex the sex to set
	 */
	public void setSex(byte sex) {
		this.sex = sex;
	}
	/**
	 * @return the age
	 */
	public short getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(short age) {
		this.age = age;
	}
	/**
	 * @return the yearlyIncome
	 */
	public int getYearlyIncome() {
		return yearlyIncome;
	}
	/**
	 * @param yearlyIncome the yearlyIncome to set
	 */
	public void setYearlyIncome(int yearlyIncome) {
		this.yearlyIncome = yearlyIncome;
	}

	/**
	 * @return the workplaceId
	 */
	public int getWorkplaceId() {
		return workplaceId;
	}

	/**
	 * @param workplaceId the workplaceId to set
	 */
	public void setWorkplaceId(int workplaceId) {
		this.workplaceId = workplaceId;
	}
	/**
	 * @return the travelModeCommuting
	 */
	public int getTravelModeCommuting() {
		return travelModeCommuting;
	}
	/**
	 * @param travelModeCommuting the travelModeCommuting to set
	 */
	public void setTravelModeCommuting(int travelModeCommuting) {
		this.travelModeCommuting = travelModeCommuting;
	}
	/**
	 * @return the timeUse
	 */
	public List<DbTimeUseRow> getTimeUse() {
		return timeUse;
	}
	/**
	 * @param timeUse the timeUse to set
	 */
	public void setTimeUse(List<DbTimeUseRow> timeUse) {
		this.timeUse = timeUse;
	}
	/**
	 * @return the inEducation
	 */
	public boolean isInEducation() {
		return inEducation;
	}
	/**
	 * @param inEducation the inEducation to set
	 */
	public void setInEducation(boolean inEducation) {
		this.inEducation = inEducation;
	}
}
