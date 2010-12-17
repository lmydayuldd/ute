package at.sume.sampling.distributions;

/**
 * Database record class for distribution of households per spatial unit and household-size
 * 
 * @author Alexander Remesch
 * 
 */
public class HouseholdsPerSpatialUnit {
	private int spatialUnitId;
	private int nrHouseholdsTotal;
//	private long nrHouseholdsRunningTotal;
	private int nrHouseholds_1P;
	private int nrHouseholds_2P;
	private int nrHouseholds_3P;
	private int nrHouseholds_4P;
	private int nrHouseholds_5P;
	private int nrHouseholds_6Pplus;
	private int nrPersons_P6plus;
	
//	public HouseholdsPerSpatialUnit()
//	{
//		nrHouseholdsRunningTotal = 0;
//	}
	public int getSpatialUnitId() {
		return spatialUnitId;
	}
	public void setSpatialUnitId(int spatialUnitId) {
		this.spatialUnitId = spatialUnitId;
	}
	public int getNrHouseholdsTotal() {
		return nrHouseholdsTotal;
	}
	public void setNrHouseholdsTotal(int nrHouseholdsTotal) {
		this.nrHouseholdsTotal = nrHouseholdsTotal;
	}
//	public long getNrHouseholdsRunningTotal() {
//		return nrHouseholdsRunningTotal;
//	}
	public void setNrHouseholds_1P(int nrHouseholds_1P) {
		this.nrHouseholds_1P = nrHouseholds_1P;
	}
	public int getNrHouseholds_1P() {
		return nrHouseholds_1P;
	}
	public void setNrHouseholds_2P(int nrHouseholds_2P) {
		this.nrHouseholds_2P = nrHouseholds_2P;
	}
	public int getNrHouseholds_2P() {
		return nrHouseholds_2P;
	}
	public void setNrHouseholds_3P(int nrHouseholds_3P) {
		this.nrHouseholds_3P = nrHouseholds_3P;
	}
	public int getNrHouseholds_3P() {
		return nrHouseholds_3P;
	}
	/**
	 * @return the nrHouseholds_4P
	 */
	public int getNrHouseholds_4P() {
		return nrHouseholds_4P;
	}
	/**
	 * @param nrHouseholds_4P the nrHouseholds_4P to set
	 */
	public void setNrHouseholds_4P(int nrHouseholds_4P) {
		this.nrHouseholds_4P = nrHouseholds_4P;
	}
	/**
	 * @return the nrHouseholds_5P
	 */
	public int getNrHouseholds_5P() {
		return nrHouseholds_5P;
	}
	/**
	 * @param nrHouseholds_5P the nrHouseholds_5P to set
	 */
	public void setNrHouseholds_5P(int nrHouseholds_5P) {
		this.nrHouseholds_5P = nrHouseholds_5P;
	}
	/**
	 * @return the nrHouseholds_6Pplus
	 */
	public int getNrHouseholds_6Pplus() {
		return nrHouseholds_6Pplus;
	}
	/**
	 * @param nrHouseholds_6Pplus the nrHouseholds_6Pplus to set
	 */
	public void setNrHouseholds_6Pplus(int nrHouseholds_6Pplus) {
		this.nrHouseholds_6Pplus = nrHouseholds_6Pplus;
	}
	/**
	 * @param nrPersons_P6plus the nrPersons_P6plus to set
	 */
	public void setNrPersons_P6plus(int nrPersons_P6plus) {
		this.nrPersons_P6plus = nrPersons_P6plus;
	}
	/**
	 * @return the nrPersons_P6plus
	 */
	public int getNrPersons_P6plus() {
		return nrPersons_P6plus;
	}
}
