/**
 * 
 */
package at.sume.sampling.entities;


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
	private int workplaceId;		// spatial unit id of workplace
	
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
}
