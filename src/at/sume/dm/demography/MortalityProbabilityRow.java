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
public class MortalityProbabilityRow extends RecordSetRow<Mortality> {
	private short age;
	private short sex;
	
	/**
	 * @return the age
	 */
	public short age() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(short age) {
		this.age = age;
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
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("sex")) {
			setSex(rs.getShort(name));
		} else if (name.equals("age")) {
			setAge(rs.getShort("age"));
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
			short lookupSex = (Short) lookupKeys[1];
			if ((lookupAgeGroupId == age()) && (lookupSex == getSex()))
				return true;
			else
				return false;
		} else {
			throw new IllegalArgumentException("Both PK fields must by of type Short");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + age;
		result = prime * result + sex;
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
		MortalityProbabilityRow other = (MortalityProbabilityRow) obj;
		if (age != other.age)
			return false;
		if (sex != other.sex)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#remove()
	 */
	@Override
	public void remove() {
		throw new IllegalArgumentException("MortalityProbabilityRow.remove() not allowed");
	}
}
