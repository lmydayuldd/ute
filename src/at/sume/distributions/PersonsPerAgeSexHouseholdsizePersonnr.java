package at.sume.distributions;

/**
 * This class contains the number of persons in a spatial unit that have a certain sex, age and live in a household of a certain
 * size as nth person.
 * 
 * @author Alexander Remesch
 *
 */
public class PersonsPerAgeSexHouseholdsizePersonnr {
	private long id;
	private long spatialUnitId;
	private short sex;
	private short ageGroupId;
	private short householdSize;
	private short personNrInHousehold;
	private double personCount;
	private double personCountRunningTotal;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getSpatialUnitId() {
		return spatialUnitId;
	}
	public void setSpatialUnitId(long spatialUnitId) {
		this.spatialUnitId = spatialUnitId;
	}
	public short getSex() {
		return sex;
	}
	public void setSex(short sex) {
		this.sex = sex;
	}
	public short getAgeGroupId() {
		return ageGroupId;
	}
	public void setAgeGroupId(short ageGroupId) {
		this.ageGroupId = ageGroupId;
	}
	public short getHouseholdSize() {
		return householdSize;
	}
	public void setHouseholdSize(short householdSize) {
		this.householdSize = householdSize;
	}
	public short getPersonNrInHousehold() {
		return personNrInHousehold;
	}
	public void setPersonNrInHousehold(short personNrInHousehold) {
		this.personNrInHousehold = personNrInHousehold;
	}
	public double getPersonCount() {
		return personCount;
	}
	public void setPersonCount(double personCount) {
		this.personCount = personCount;
	}
	/**
	 * @return the personCountRunningTotal
	 */
	public double getPersonCountRunningTotal() {
		return personCountRunningTotal;
	}
	/**
	 * @param personCountRunningTotal the personCountRunningTotal to set
	 */
	public void setPersonCountRunningTotal(double personCountRunningTotal) {
		this.personCountRunningTotal = personCountRunningTotal;
	}
}
