/**
 * 
 */
package at.sume.dm.indicators.rows;

import at.sume.dm.indicators.base.IndicatorRow;
import at.sume.dm.types.AgeGroup;

/**
 * @author Alexander Remesch
 *
 */
public class PopulationPerAgeGroupRow extends IndicatorRow implements Comparable<PopulationPerAgeGroupRow> {
	private long spatialUnitId;
	private short ageGroupId;
	private long personCount;
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
	 * @param ageGroupId the ageGroupId to set
	 */
	public void setAgeGroupId(short ageGroupId) {
		this.ageGroupId = ageGroupId;
	}
	/**
	 * @return the ageGroupId
	 */
	public short getAgeGroupId() {
		return ageGroupId;
	}
	/**
	 * @return the personCount
	 */
	public long getPersonCount() {
		return personCount;
	}
	/**
	 * @param personCount the personCount to set
	 */
	public void setPersonCount(long personCount) {
		this.personCount = personCount;
	}
	@Override
	public int compareTo(PopulationPerAgeGroupRow arg0) {
		int result = ((Long)spatialUnitId).compareTo(arg0.getSpatialUnitId());
		if (result == 0)
			result = ((Short)ageGroupId).compareTo(arg0.getAgeGroupId());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		delimiter = ";";
		return spatialUnitId + delimiter + AgeGroup.getAgeGroupNameDirect(ageGroupId) + delimiter + personCount;
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.rows.IndicatorRow#display()
	 */
	@Override
	public String display() {
		return spatialUnitId + delimiter + AgeGroup.getAgeGroupNameDirect(ageGroupId) + delimiter + personCount;
	}
}
