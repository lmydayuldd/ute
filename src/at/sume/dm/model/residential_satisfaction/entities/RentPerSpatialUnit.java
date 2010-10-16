/**
 * 
 */
package at.sume.dm.model.residential_satisfaction.entities;

/**
 * @author Alexander Remesch
 *
 */
public class RentPerSpatialUnit implements Comparable<RentPerSpatialUnit> {
	// must be public in order to be able to use Database.select()/java reflection api
	public long spatialUnitId;
	public double yearlyRentPerSqm;
	/**
	 * @return the spatialUnitId
	 */
	public long getSpatialUnitId() {
		return spatialUnitId;
	}
	/**
	 * @param spatialUnitId the spatialUnitId to set
	 */
	public void setSpatialUnitId(long spatialUnitId) {
		this.spatialUnitId = spatialUnitId;
	}
	/**
	 * @return the yearlyPricePerSqm
	 */
	public double getYearlyRentPerSqm() {
		return yearlyRentPerSqm;
	}
	/**
	 * @param yearlyRentPerSqm the yearlyPricePerSqm to set
	 */
	public void setYearlyRentPerSqm(double yearlyRentPerSqm) {
		this.yearlyRentPerSqm = yearlyRentPerSqm;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RentPerSpatialUnit arg0) {
		return ((Long)spatialUnitId).compareTo(arg0.getSpatialUnitId());
	}
}

