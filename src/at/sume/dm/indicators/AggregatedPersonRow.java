/**
 * 
 */
package at.sume.dm.indicators;

import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.AgeGroup;

/**
 * @author Alexander Remesch
 */
public class AggregatedPersonRow implements Comparable<AggregatedPersonRow>, Fileable {
	private int spatialUnitId;
	private byte incomeGroupId;
	private byte sex;
	private byte ageGroupId;
	private boolean livingWithParents;
	private short householdSize6;
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
	 * @return the sex
	 */
	public byte getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(byte sex) {
		this.sex = sex;
	}

	/**
	 * @return the ageGroupId
	 */
	public byte getAgeGroupId() {
		return ageGroupId;
	}

	/**
	 * @param ageGroupId the ageGroupId to set
	 */
	public void setAgeGroupId(byte ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	/**
	 * @return the livingWithParents
	 */
	public boolean isLivingWithParents() {
		return livingWithParents;
	}

	/**
	 * @param livingWithParents the livingWithParents to set
	 */
	public void setLivingWithParents(boolean livingWithParents) {
		this.livingWithParents = livingWithParents;
	}

	/**
	 * @return the householdSize6
	 */
	public short getHouseholdSize6() {
		return householdSize6;
	}

	/**
	 * @param householdSize6 the householdSize6 to set
	 */
	public void setHouseholdSize6(short householdSize6) {
		this.householdSize6 = householdSize6;
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
	 * @see at.sume.dm.model.output.Fileable#toCsvHeadline(java.lang.String)
	 */
	@Override
	public String toCsvHeadline(String delimiter) {
		return "SpatialUnit" + delimiter + "IncomeGroup" + delimiter + "Sex" + delimiter + "AgeGroup" + 
			delimiter + "LivingWithParents" + delimiter + "HouseholdSize6" + delimiter + "PersonCount";
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.Fileable#toString(java.lang.String)
	 */
	@Override
	public String toString(String delimiter) {
		return spatialUnitId + delimiter + incomeGroupId + delimiter + sex +
			delimiter + AgeGroup.getAgeGroupNameDirect(ageGroupId) + delimiter + livingWithParents + 
			delimiter + householdSize6 + delimiter + personCount;
	}

	@Override
	public int compareTo(AggregatedPersonRow o) {
		int result;
		result = ((Integer)spatialUnitId).compareTo(o.spatialUnitId);
		if (result != 0) return(result);
		result = ((Byte)incomeGroupId).compareTo(o.incomeGroupId);
		if (result != 0) return(result);
		result = ((Byte)sex).compareTo(o.sex);
		if (result != 0) return(result);
		result = ((Byte)ageGroupId).compareTo(o.ageGroupId);
		if (result != 0) return(result);
		result = ((Boolean)livingWithParents).compareTo(o.livingWithParents);
		if (result != 0) return(result);
		result = ((Short)householdSize6).compareTo(o.householdSize6);
		if (result != 0) return(result);
		return 0;
	}
}
