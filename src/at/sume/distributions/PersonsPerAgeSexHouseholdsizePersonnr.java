package at.sume.distributions;

/**
 * @author ar
 *
 */
public class PersonsPerAgeSexHouseholdsizePersonnr {
	private long id;
	private long spatialUnitId;
	private short sex;
	private int ageGroup;
	private short householdSize;
	private short personNrInHousehold;
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
	public short getSex() {
		return sex;
	}
	public void setSex(short sex) {
		this.sex = sex;
	}
	public int getAgeGroup() {
		return ageGroup;
	}
	public void setAgeGroup(int ageGroup) {
		this.ageGroup = ageGroup;
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
}
