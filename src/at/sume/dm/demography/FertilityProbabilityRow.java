/**
 * 
 */
package at.sume.dm.demography;

import net.remesch.db.schema.Ignore;

/**
 * Implementation of ProbabilityItem for fertility (= event) depending on age (= properties)
 * @author Alexander Remesch
 */
public class FertilityProbabilityRow implements Comparable<FertilityProbabilityRow> {
	private byte ageGroupId;
	@Ignore
	private short householdSize;
	private double probabilityBirth;
	
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
	 * @param householdSize the householdSize to set
	 */
	public void setHouseholdSize(short householdSize) {
		this.householdSize = householdSize;
	}

	/**
	 * @return the householdSize
	 */
	public short getHouseholdSize() {
		return householdSize;
	}

	/**
	 * @param probabilityBirth the probabilityBirth to set
	 */
	public void setProbabilityBirth(double probabilityBirth) {
		this.probabilityBirth = probabilityBirth;
	}

	/**
	 * @return the probabilityBirth
	 */
	public double getProbabilityBirth() {
		return probabilityBirth;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ageGroupId + householdSize;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FertilityProbabilityRow other = (FertilityProbabilityRow) obj;
		if (ageGroupId != other.ageGroupId)
			return false;
		if (householdSize != other.householdSize)
			return false;
		return true;
	}

	@Override
	public int compareTo(FertilityProbabilityRow o) {
		int result = ((Byte)ageGroupId).compareTo(o.ageGroupId);
		if (result == 0) {
			return ((Short)householdSize).compareTo(o.householdSize);
		}
		return result;
	}
}
