/**
 * 
 */
package at.sume.dm.indicators;

import at.sume.dm.Common;
import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.CostOfResidenceGroup;
import at.sume.dm.types.LivingSpaceGroup6;

/**
 * @author Alexander Remesch
 *
 */
public class AggregatedDwellingRow implements Comparable<AggregatedDwellingRow>, Fileable{
	private int spatialUnitId;
	private short costOfResidenceGroupId;
	private byte livingSpaceGroupId;
	private boolean vacant;
	private int dwellingCount;
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
	 * @return the costOfResidenceGroupId
	 */
	public short getCostOfResidenceGroupId() {
		return costOfResidenceGroupId;
	}
	/**
	 * @param costOfResidenceGroupId the costOfResidenceGroupId to set
	 */
	public void setCostOfResidenceGroupId(short costOfResidenceGroupId) {
		this.costOfResidenceGroupId = costOfResidenceGroupId;
	}
	/**
	 * @return the livingSpaceGroupId
	 */
	public byte getLivingSpaceGroupId() {
		return livingSpaceGroupId;
	}
	/**
	 * @param livingSpaceGroupId the livingSpaceGroupId to set
	 */
	public void setLivingSpaceGroupId(byte livingSpaceGroupId) {
		this.livingSpaceGroupId = livingSpaceGroupId;
	}
	/**
	 * @return the vacant
	 */
	public boolean isVacant() {
		return vacant;
	}
	/**
	 * @param vacant the vacant to set
	 */
	public void setVacant(boolean vacant) {
		this.vacant = vacant;
	}
	/**
	 * @return the dwellingCount
	 */
	public int getDwellingCount() {
		return dwellingCount;
	}
	/**
	 * @param dwellingCount the dwellingCount to set
	 */
	public void setDwellingCount(int dwellingCount) {
		this.dwellingCount = dwellingCount;
	}
	@Override
	public String toCsvHeadline(String delimiter) {
		return "ModelRun" + delimiter + "SpatialUnit" + delimiter + "YearlyRentPerSqmGroup" + delimiter + "LivingSpaceGroup" + delimiter + "Vacant" + 
			delimiter + "DwellingCount";
	}
	@Override
	public String toString(int modelRun, String delimiter) {
		return modelRun + delimiter + spatialUnitId + delimiter + CostOfResidenceGroup.getCostOfResidenceGroupName(costOfResidenceGroupId) + delimiter + LivingSpaceGroup6.getLivingSpaceGroupName(livingSpaceGroupId) +
			delimiter + Boolean.toString(isVacant()) + delimiter + dwellingCount * Common.getHouseholdReductionFactor();
	}
	@Override
	public int compareTo(AggregatedDwellingRow o) {
		int result;
		result = ((Integer)spatialUnitId).compareTo(o.spatialUnitId);
		if (result != 0) return(result);
		result = ((Short)costOfResidenceGroupId).compareTo(o.costOfResidenceGroupId);
		if (result != 0) return(result);
		result = ((Byte)livingSpaceGroupId).compareTo(o.livingSpaceGroupId);
		if (result != 0) return(result);
		result = ((Boolean)vacant).compareTo(o.vacant);
		if (result != 0) return(result);
		return 0;
	}
}
