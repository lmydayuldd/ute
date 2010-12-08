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
	private int spatialUnitId;
	private byte ageGroupId;
	private int personCount;
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
	 * @param ageGroupId the ageGroupId to set
	 */
	public void setAgeGroupId(byte ageGroupId) {
		this.ageGroupId = ageGroupId;
	}
	/**
	 * @return the ageGroupId
	 */
	public byte getAgeGroupId() {
		return ageGroupId;
	}
	/**
	 * @return the personCount
	 */
	public int getPersonCount() {
		return personCount;
	}
	/**
	 * @param personCount the personCount to set
	 */
	public void setPersonCount(int personCount) {
		this.personCount = personCount;
	}
	@Override
	public int compareTo(PopulationPerAgeGroupRow arg0) {
		int result = ((Integer)spatialUnitId).compareTo(arg0.getSpatialUnitId());
		if (result == 0)
			result = ((Byte)ageGroupId).compareTo(arg0.getAgeGroupId());
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
