/**
 * 
 */
package at.sume.dm.demography;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.db.RecordSetRow;

/**
 * Implementation of ProbabilityItem for mortality (= event) depending on age and sex (= properties)
 * @author Alexander Remesch
 */
public class MortalityProbabilityRow extends RecordSetRow {
	private short ageGroupId;
	private short sex;
	
	/**
	 * @return the ageGroupId
	 */
	public short getAge() {
		return ageGroupId;
	}

	/**
	 * @param ageGroupId the ageGroupId to set
	 */
	public void setAgeGroupId(short ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	/**
	 * @return the sex
	 */
	public short getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(short sex) {
		this.sex = sex;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.ProbabilityItem#set(java.lang.String)
	 */
	@Override
	public void set(ResultSet rs, String name) throws SQLException {
		if (name.equals("sex")) {
			setSex(rs.getShort(name));
		} else if (name.equals("AgeGroupId")) {
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
		// TODO Auto-generated method stub
		return false;
	}

}
