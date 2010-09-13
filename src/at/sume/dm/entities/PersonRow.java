/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public class PersonRow extends RecordSetRow {
	private long householdId;
	private short sex;
	private int yearBorn;
	private short ageGroupId;
	private short age;
	private boolean householdRepresentative;
	private long yearlyIncome;
	private HouseholdRow household;
	private Persons persons;
	
	public PersonRow(Persons persons) {
		this.persons = persons;
	}

	/**
	 * @return the personId
	 */
	public long getPersonId() {
		return id;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(long personId) {
		this.id = personId;
	}

	/**
	 * @return the householdId
	 */
	public long getHouseholdId() {
		return householdId;
	}

	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(long householdId) {
		this.householdId = householdId;
	}

	/**
	 * @return the sex (1 = female, 2 = male)
	 */
	public short getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set (1 = female, 2 = male)
	 */
	public void setSex(short sex) {
		this.sex = sex;
	}

	/**
	 * @return the yearBorn
	 */
	public int getYearBorn() {
		return yearBorn;
	}

	/**
	 * @param yearBorn the yearBorn to set
	 */
	public void setYearBorn(int yearBorn) {
		this.yearBorn = yearBorn;
	}

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

	/**
	 * @param age the age to set
	 */
	public void setAge(short age) {
		this.age = age;
	}

	/**
	 * @return the age
	 */
	public short getAge() {
		return age;
	}

	/**
	 * @return the householdRepresentative
	 */
	public boolean isHouseholdRepresentative() {
		return householdRepresentative;
	}

	/**
	 * @param householdRepresentative the householdRepresentative to set
	 */
	public void setHouseholdRepresentative(boolean householdRepresentative) {
		this.householdRepresentative = householdRepresentative;
	}

	/**
	 * @return the yearlyIncome
	 */
	public long getYearlyIncome() {
		return yearlyIncome;
	}

	/**
	 * @param yearlyIncome the yearlyIncome to set
	 */
	public void setYearlyIncome(long yearlyIncome) {
		this.yearlyIncome = yearlyIncome;
	}

	public HouseholdRow getHousehold() {
		return household;
	}

	public void setHousehold(HouseholdRow household) {
		this.household = household;
	}

	/**
	 * @return the persons
	 */
	public Persons getPersons() {
		return persons;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void set(ResultSet rs, String name) throws SQLException {
		if (name.equals("PersonId")) {
			setPersonId(rs.getLong(name));
		} else if (name.equals("HouseholdId")) {
			setHouseholdId(rs.getLong(name));
		} else if (name.equals("Sex")) {
			setSex(rs.getShort(name));
		} else if (name.equals("YearBorn")) {
			setYearBorn(rs.getInt(name));
		} else if (name.equals("AgeGroupId")) {
			setAgeGroupId(rs.getShort(name));
		} else if (name.equals("HouseholdRepresentative")) {
			setHouseholdRepresentative(rs.getBoolean(name));
		} else if (name.equals("YearlyIncome")) {
			setYearlyIncome(rs.getLong(name));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}

	/**
	 * Remove this record from the list of persons and the list of household members
	 */
	@Override
	public void remove() {
		household.removeMember(this);
		persons.remove(this);
	}
}
