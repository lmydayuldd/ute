/**
 * 
 */
package at.sume.dm.indicators;

import at.sume.dm.Common;
import at.sume.dm.model.output.Fileable;
import at.sume.dm.model.timeuse.TimeUseType;
import at.sume.dm.types.HouseholdType;

/**
 * @author Alexander Remesch
 */
public class AggregatedTimeUseRow implements Fileable, Comparable<AggregatedTimeUseRow> {
	private int spatialUnitId;
	private String activity;
	private byte incomeGroupId;
	private HouseholdType householdType;
	private TimeUseType timeUseType;
	private long timeUseSum;
	private int participatingPersonCount;
	private int participatingHouseholdCount;

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
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}

	/**
	 * @return the incomeGroupId
	 */
	public byte getIncomeGroupId() {
		return incomeGroupId;
	}

	/**
	 * @param incomeGroupId the incomeGroupId to set
	 */
	public void setIncomeGroupId(byte incomeGroupId) {
		this.incomeGroupId = incomeGroupId;
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
	 * @return the timeUseType
	 */
	public TimeUseType getTimeUseType() {
		return timeUseType;
	}

	/**
	 * @param timeUseType the timeUseType to set
	 */
	public void setTimeUseType(TimeUseType timeUseType) {
		this.timeUseType = timeUseType;
	}

	/**
	 * @return the timeUseSum
	 */
	public long getTimeUseSum() {
		return timeUseSum;
	}

	/**
	 * @param timeUseSum the timeUseSum to set
	 */
	public void setTimeUseSum(long timeUseSum) {
		this.timeUseSum = timeUseSum;
	}

	/**
	 * @return the participatingPersonCount
	 */
	public int getParticipatingPersonCount() {
		return participatingPersonCount;
	}

	/**
	 * @param participatingPersonCount the participatingPersonCount to set
	 */
	public void setParticipatingPersonCount(int participatingPersonCount) {
		this.participatingPersonCount = participatingPersonCount;
	}

	/**
	 * @return the participatingHouseholdCount
	 */
	public int getParticipatingHouseholdCount() {
		return participatingHouseholdCount;
	}

	/**
	 * @param participatingHouseholdCount the participatingHouseholdCount to set
	 */
	public void setParticipatingHouseholdCount(int participatingHouseholdCount) {
		this.participatingHouseholdCount = participatingHouseholdCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AggregatedTimeUseRow o) {
		int result;
		result = ((Integer)spatialUnitId).compareTo(o.spatialUnitId);
		if (result != 0) return(result);
		result = activity.compareTo(o.activity);
		if (result != 0) return(result);
		result = ((Byte)incomeGroupId).compareTo(o.incomeGroupId);
		if (result != 0) return(result);
		result = householdType.compareTo(o.householdType);
		if (result != 0) return(result);
		result = timeUseType.compareTo(o.timeUseType);
		if (result != 0) return(result);
		return 0;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.Fileable#toCsvHeadline(java.lang.String)
	 */
	@Override
	public String toCsvHeadline(String delimiter) {
		return "ModelRun" + delimiter + "SpatialUnit" + delimiter + "Activity" + delimiter + "HouseholdIncomeGroup" + delimiter + "HouseholdType" + delimiter + "TimeUseType" + 
				delimiter + "TotalTimeUse" + delimiter + "ParticipatingPersons" + delimiter + "ParticipatingHouseholds";
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.Fileable#toString(java.lang.String)
	 */
	@Override
	public String toString(int modelRun, String delimiter) {
		return modelRun + delimiter + spatialUnitId + delimiter + activity + delimiter + incomeGroupId + delimiter + householdType.toString() +
				delimiter + timeUseType.toString() + delimiter + timeUseSum * Common.getHouseholdReductionFactor() + delimiter + participatingPersonCount * Common.getHouseholdReductionFactor() + 
				delimiter + participatingHouseholdCount * Common.getHouseholdReductionFactor();
	}
}
