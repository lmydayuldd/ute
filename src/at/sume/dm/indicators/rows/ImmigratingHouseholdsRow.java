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
	private long spatialUnitId;
	private short incomeGroupId;
	private short householdSize;
	private long householdCount;
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
	 * @return the incomeGroup
	 */
	public short getIncomeGroupId() {
		return incomeGroupId;
	}

	/**
	 * @param incomeGroup the incomeGroup to set
	 */
	public void setIncomeGroupId(short incomeGroupId) {
		this.incomeGroupId = incomeGroupId;
	}

	/**
	 * @return the householdSize
	 */
	public short getHouseholdSize() {
		return householdSize;
	}

	/**
	 * @param householdSize the householdSize to set
	 */
	public void setHouseholdSize(short householdSize) {
		this.householdSize = householdSize;
	}

	/**
	 * @return the householdCount
	 */
	public long getHouseholdCount() {
		return householdCount;
	}

	/**
	 * @param householdCount the householdCount to set
	 */
	public void setHouseholdCount(long householdCount) {
		this.householdCount = householdCount;
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

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ImmigratingHouseholdsRow arg0) {
		int result = ((Long)spatialUnitId).compareTo(arg0.getSpatialUnitId());
		if (result == 0)
			result = ((Short)householdSize).compareTo(arg0.getHouseholdSize());
		if (result == 0)
			result = ((Short)incomeGroupId).compareTo(arg0.getIncomeGroupId());
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
