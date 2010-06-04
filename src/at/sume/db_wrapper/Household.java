package at.sume.db_wrapper;

public class Household {
	private long HouseholdId;
	private long SpatialunitId;
	private short HouseholdSize;
	private long DwellingId;

	public long getHouseholdId() {
		return HouseholdId;
	}
	public void setHouseholdId(long householdId) {
		HouseholdId = householdId;
	}
	public long getSpatialunitId() {
		return SpatialunitId;
	}
	public void setSpatialunitId(long spatialunitId) {
		SpatialunitId = spatialunitId;
	}
	public short getHouseholdSize() {
		return HouseholdSize;
	}
	public void setHouseholdSize(short householdSize) throws IllegalArgumentException {
		if (householdSize < 1 || householdSize > 4)
			throw new IllegalArgumentException("householdSize must be in the range from 1 to 4");
		HouseholdSize = householdSize;
	}
	public long getDwellingId() {
		return DwellingId;
	}
	public void setDwellingId(long dwellingId) {
		DwellingId = dwellingId;
	}
}
