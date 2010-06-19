package at.sume.distributions;

/**
 * @author ar
 *
 */
public class PersonsPerAgeSexHouseholdsizePersonnr {
	private long id;
	private long spatialUnitId;
	private int sex;
	private int ageGroup;
	private int householdSize;
	private int personNrInHousehold;
	private double personCount;
	
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
	public int isSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getAgeGroup() {
		return ageGroup;
	}
	public void setAgeGroup(int ageGroup) {
		this.ageGroup = ageGroup;
	}
	public int getHouseholdSize() {
		return householdSize;
	}
	public void setHouseholdSize(int householdSize) {
		this.householdSize = householdSize;
	}
	public int getPersonNrInHousehold() {
		return personNrInHousehold;
	}
	public void setPersonNrInHousehold(int personNrInHousehold) {
		this.personNrInHousehold = personNrInHousehold;
	}
	public double getPersonCount() {
		return personCount;
	}
	public void setPersonCount(double personCount) {
		this.personCount = personCount;
	}
}
