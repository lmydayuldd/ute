/**
 * 
 */
package at.sume.dm.model.residential_satisfaction.entities;

import java.util.Comparator;

/**
 * @author Alexander Remesch
 *
 */
public class SpatialUnitUdpComparatorSpatialUnitId implements Comparator<SpatialUnitUdp> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(SpatialUnitUdp arg0, SpatialUnitUdp arg1) {
		if (arg0.spatialUnitId == arg1.spatialUnitId)
			return 0;
		else if (arg0.spatialUnitId > arg1.spatialUnitId) 
			return 1;
		else
			return -1;
	}
}
