/**
 * 
 */
package at.sume.dm.indicators.rows;

import at.sume.dm.indicators.base.IndicatorRow;
import at.sume.dm.types.IncomeGroup;

/**
 * @author Alexander Remesch
 *
 */
public class ImmigratingHouseholdsRow extends IndicatorRow implements Comparable<ImmigratingHouseholdsRow> {
	private int spatialUnitId;
	private byte incomeGroupId;
	private byte householdSize;
	private int householdCount;
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
	 * @return the incomeGroup
	 */
	public byte getIncomeGroupId() {
		return incomeGroupId;
	}

	/**
	 * @param incomeGroup the incomeGroup to set
	 */
	public void setIncomeGroupId(byte incomeGroupId) {
		this.incomeGroupId = incomeGroupId;
	}

	/**
	 * @return the householdSize
	 */
	public byte getHouseholdSize() {
		return householdSize;
	}

	/**
	 * @param householdSize the householdSize to set
	 */
	public void setHouseholdSize(byte householdSize) {
		this.householdSize = householdSize;
	}

	/**
	 * @return the householdCount
	 */
	public int getHouseholdCount() {
		return householdCount;
	}

	/**
	 * @param householdCount the householdCount to set
	 */
	public void setHouseholdCount(int householdCount) {
		this.householdCount = householdCount;
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

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ImmigratingHouseholdsRow arg0) {
		int result = ((Integer)spatialUnitId).compareTo(arg0.getSpatialUnitId());
		if (result == 0)
			result = ((Byte)householdSize).compareTo(arg0.getHouseholdSize());
		if (result == 0)
			result = ((Byte)incomeGroupId).compareTo(arg0.getIncomeGroupId());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		delimiter = ";";
		return spatialUnitId + delimiter + IncomeGroup.getIncomeGroupNameDirect(incomeGroupId) + delimiter + householdCount + delimiter + personCount;
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.rows.IndicatorRow#display()
	 */
	@Override
	public String display() {
		return spatialUnitId + delimiter + IncomeGroup.getIncomeGroupNameDirect(incomeGroupId) + delimiter + householdCount + delimiter + personCount;
	}
}
