package at.sume.distributions;

public class HouseholdsPerSpatialUnit {
	private long index;
	private long spatialUnitId;
	private long nrHouseholdsTotal;
	private long nrHouseholdsRunningTotal;
	private long nrHouseholds_1P;
	private long nrHouseholds_2P;
	private long nrHouseholds_3P;
	private long nrHouseholds_4Pmore;
	private static long nrHouseholdsTotalSum;
	
	public HouseholdsPerSpatialUnit()
	{
		nrHouseholdsRunningTotal = 0;
	}
	public long getSpatialUnitId() {
		return spatialUnitId;
	}
	public void setSpatialUnitId(long spatialUnitId) {
		this.spatialUnitId = spatialUnitId;
	}
	public long getNrHouseholdsTotal() {
		return nrHouseholdsTotal;
	}
	public void setNrHouseholdsTotal(long nrHouseholdsTotal) {
		this.nrHouseholdsTotal = nrHouseholdsTotal;
		nrHouseholdsTotalSum += nrHouseholdsTotal;
		this.nrHouseholdsRunningTotal = nrHouseholdsTotalSum;
	}
	public long getNrHouseholdsRunningTotal() {
		return nrHouseholdsRunningTotal;
	}
	public void setNrHouseholds_1P(long nrHouseholds_1P) {
		this.nrHouseholds_1P = nrHouseholds_1P;
	}
	public long getNrHouseholds_1P() {
		return nrHouseholds_1P;
	}
	public void setNrHouseholds_2P(long nrHouseholds_2P) {
		this.nrHouseholds_2P = nrHouseholds_2P;
	}
	public long getNrHouseholds_2P() {
		return nrHouseholds_2P;
	}
	public void setNrHouseholds_3P(long nrHouseholds_3P) {
		this.nrHouseholds_3P = nrHouseholds_3P;
	}
	public long getNrHouseholds_3P() {
		return nrHouseholds_3P;
	}
	public void setNrHouseholds_4Pmore(long nrHouseholds_4Pmore) {
		this.nrHouseholds_4Pmore = nrHouseholds_4Pmore;
	}
	public long getNrHouseholds_4Pmore() {
		return nrHouseholds_4Pmore;
	}
	public static long getNrHouseholdsTotalSum() {
		return nrHouseholdsTotalSum;
	}
}
