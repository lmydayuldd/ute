package at.sume.generate_population;

public class HouseholdsPerSpatialUnit {
	private long SpatialUnitId;
	private long NumberOfHouseholds;
	
	public long getSpatialUnitId() {
		return SpatialUnitId;
	}
	public void setSpatialUnitId(long spatialUnitId) {
		SpatialUnitId = spatialUnitId;
	}
	public long getNumberOfHouseholds() {
		return NumberOfHouseholds;
	}
	public void setNumberOfHouseholds(long numberOfHouseholds) {
		NumberOfHouseholds = numberOfHouseholds;
	}
}
