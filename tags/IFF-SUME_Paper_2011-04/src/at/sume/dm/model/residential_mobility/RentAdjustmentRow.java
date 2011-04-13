/**
 * 
 */
package at.sume.dm.model.residential_mobility;

/**
 * @author Alexander Remesch
 *
 */
public class RentAdjustmentRow implements Comparable<RentAdjustmentRow> {
	private int spatialUnitId;
	private short modelYear;
	private short rentAdjustment;
	
	/**
	 * @return the spatialUnitId
	 */
	public int getSpatialUnitId() {
		return spatialUnitId;
	}
	/**
	 * @param spatialUnitId the spatialUnitId to set
	 */
	public void setSpatialUnitId(int spatialUnitId) {
		this.spatialUnitId = spatialUnitId;
	}
	/**
	 * @return the modelYear
	 */
	public short getModelYear() {
		return modelYear;
	}
	/**
	 * @param modelYear the modelYear to set
	 */
	public void setModelYear(short modelYear) {
		this.modelYear = modelYear;
	}
	/**
	 * @return the rentAdjustment
	 */
	public short getRentAdjustment() {
		return rentAdjustment;
	}
	/**
	 * @param rentAdjustment the rentAdjustment to set
	 */
	public void setRentAdjustment(short rentAdjustment) {
		this.rentAdjustment = rentAdjustment;
	}
	@Override
	public int compareTo(RentAdjustmentRow o) {
		int result = ((Integer)spatialUnitId).compareTo(o.spatialUnitId);
		if (result == 0) {
			return ((Short)modelYear).compareTo(o.modelYear);
		}
		return result;
	}
	
}
