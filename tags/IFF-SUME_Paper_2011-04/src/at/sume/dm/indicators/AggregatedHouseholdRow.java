/**
 * 
 */
package at.sume.dm.indicators;

import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.IncomeGroup;
import at.sume.dm.types.LivingSpaceGroup6;

/**
 * @author Alexander Remesch
 */
public class AggregatedHouseholdRow implements Comparable<AggregatedHouseholdRow>, Fileable {
	private int spatialUnitId;
	private byte incomeGroupId;
	private byte livingSpaceGroupId;
	private byte householdSize;
	private HouseholdType householdType;
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
	 * @return the livingSpaceGroup
	 */
	public byte getLivingSpaceGroupId() {
		return livingSpaceGroupId;
	}

	/**
	 * @param livingSpaceGroup the livingSpaceGroup to set
	 */
	public void setLivingSpaceGroupId(byte livingSpaceGroupId) {
		this.livingSpaceGroupId = livingSpaceGroupId;
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
	public void setHouseholdSize(byte householdSize) {
		this.householdSize = householdSize;
	}

	/**
	 * @return the householdType
	 */
	public HouseholdType getHouseholdType() {
		return householdType;
	}

	/**
	 * @param householdType the householdType to set
	 */
	public void setHouseholdType(HouseholdType householdType) {
		this.householdType = householdType;
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

	@Override
	public String toCsvHeadline(String delimiter) {
		return "SpatialUnit" + delimiter + "IncomeGroup" + delimiter + "LivingSpaceGroup" + delimiter + "HouseholdSize" + 
			delimiter + "HouseholdType" + delimiter + "HouseholdCount" + delimiter + "PersonCount";
	}

	@Override
	public String toString(String delimiter) {
		return spatialUnitId + delimiter + IncomeGroup.getIncomeGroupNameDirect(incomeGroupId) + delimiter + LivingSpaceGroup6.getLivingSpaceGroupName(livingSpaceGroupId) +
			delimiter + householdSize + delimiter + householdType.toString() + delimiter + householdCount + delimiter + personCount;
	}

	@Override
	public int compareTo(AggregatedHouseholdRow o) {
		int result;
		result = ((Integer)spatialUnitId).compareTo(o.spatialUnitId);
		if (result != 0) return(result);
		result = ((Byte)incomeGroupId).compareTo(o.incomeGroupId);
		if (result != 0) return(result);
		result = ((Byte)livingSpaceGroupId).compareTo(o.livingSpaceGroupId);
		if (result != 0) return(result);
		result = ((Byte)householdSize).compareTo(o.householdSize);
		if (result != 0) return(result);
		result = householdType.compareTo(o.householdType);
		if (result != 0) return(result);
		return 0;
	}
}
