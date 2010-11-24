/**
 * 
 */
package at.sume.dm.demography;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.db.RecordSetRow;

/**
 * Implementation of ProbabilityItem for fertility (= event) depending on age (= properties)
 * @author Alexander Remesch
 */
public class FertilityProbabilityRow extends RecordSetRow<Fertility> {
	private short ageGroupId;
	
	/**
	 * @return the ageGroupId
	 */
	public short getAgeGroupId() {
		return ageGroupId;
	}

	/**
	 * @param ageGroupId the ageGroupId to set
	 */
	public void setAgeGroupId(short ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.ProbabilityItem#set(java.lang.String)
	 */
	@Override
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("AgeGroupId")) {
			setAgeGroupId(rs.getShort("AgeGroupId"));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#primaryKeyEquals(java.lang.Object[])
	 */
	@Override
	public boolean primaryKeyEquals(Object... lookupKeys) {
		if (lookupKeys.length != 2) {
			throw new IllegalArgumentException("PK must be two fields (of type Short)");
		}
		if ((lookupKeys[0] instanceof Short) && (lookupKeys[1] instanceof Short)) {
			short lookupAgeGroupId = (Short) lookupKeys[0];
			if (lookupAgeGroupId == getAgeGroupId())
				return true;
			else
				return false;
		} else {
			throw new IllegalArgumentException("PK field must by of type Short");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ageGroupId;
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
		return true;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#remove()
	 */
	@Override
	public void remove() {
		throw new IllegalArgumentException("FertilityProbabilityRow.remove() not allowed");
	}
}
