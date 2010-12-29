package net.remesch.obsolete;

import java.util.Random;

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
	private short minAge;
	private short maxAge;
	
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
	/**
	 * @return the minAge
	 */
	public short getMinAge() {
		return minAge;
	}
	/**
	 * @param minAge the minAge to set
	 */
	public void setMinAge(short minAge) {
		this.minAge = minAge;
	}
	/**
	 * @return the maxAge
	 */
	public short getMaxAge() {
		return maxAge;
	}
	/**
	 * @param maxAge the maxAge to set
	 */
	public void setMaxAge(short maxAge) {
		this.maxAge = maxAge;
	}
	
	public short getAge() {
		Random r = new Random();
		return (short) (getMinAge() + (r.nextDouble() * (getMaxAge() - getMinAge())));
	}
}
