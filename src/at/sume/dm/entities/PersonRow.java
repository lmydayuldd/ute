/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.remesch.db.Sequence;
import net.remesch.db.schema.Ignore;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public class PersonRow extends RecordSetRow<Persons> {
	private int householdId;
	private byte sex;
//	private int yearBorn;
	private byte ageGroupId;
	private byte age;
//	private boolean householdRepresentative;
	private int yearlyIncome;
	private HouseholdRow household;
//	private short personNrInHousehold;
	@Ignore
	private static Sequence personIdSeq = null;
	
	public PersonRow() {
		super();
		if (personIdSeq != null) {
			setPersonId(personIdSeq.getNext());
		}
	}

	/**
	 * @return the personId
	 */
	public int getPersonId() {
		return id;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(int personId) {
		this.id = personId;
	}

	/**
	 * @return the householdId
	 */
	public int getHouseholdId() {
		return householdId;
	}

	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
	}

	/**
	 * @return the sex (1 = female, 2 = male)
	 */
	public byte getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set (1 = female, 2 = male)
	 */
	public void setSex(byte sex) {
		this.sex = sex;
	}

//	/**
//	 * @return the yearBorn
//	 */
//	public int getYearBorn() {
//		return yearBorn;
//	}
//
//	/**
//	 * @param yearBorn the yearBorn to set
//	 */
//	public void setYearBorn(int yearBorn) {
//		this.yearBorn = yearBorn;
//	}

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
	 * @param age the age to set
	 */
	public void setAge(byte age) {
		this.age = age;
	}

	/**
	 * @return the age
	 */
	public byte getAge() {
		return age;
	}

//	/**
//	 * @return the householdRepresentative
//	 */
//	public boolean isHouseholdRepresentative() {
//		return householdRepresentative;
//	}
//
//	/**
//	 * @param householdRepresentative the householdRepresentative to set
//	 */
//	public void setHouseholdRepresentative(boolean householdRepresentative) {
//		this.householdRepresentative = householdRepresentative;
//	}

	/**
	 * @return the yearlyIncome
	 */
	public int getYearlyIncome() {
		return yearlyIncome;
	}

	/**
	 * @param yearlyIncome the yearlyIncome to set
	 */
	public void setYearlyIncome(int yearlyIncome) {
		this.yearlyIncome = yearlyIncome;
	}

	public HouseholdRow getHousehold() {
		return household;
	}

	public void setHousehold(HouseholdRow household) {
		this.household = household;
	}

//	/**
//	 * @return the personNrInHousehold
//	 */
//	public short getPersonNrInHousehold() {
//		return personNrInHousehold;
//	}
//
//	/**
//	 * @param personNrInHousehold the personNrInHousehold to set
//	 */
//	public void setPersonNrInHousehold(short personNrInHousehold) {
//		this.personNrInHousehold = personNrInHousehold;
//	}

	/**
	 * @return the persons
	 */
	public Persons getPersons() {
		return recordSet;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("PersonId")) {
			setPersonId(rs.getInt(name));
		} else if (name.equals("HouseholdId")) {
			setHouseholdId(rs.getInt(name));
		} else if (name.equals("Sex")) {
			setSex(rs.getByte(name));
//		} else if (name.equals("YearBorn")) {
//			setYearBorn(rs.getInt(name));
		} else if (name.equals("Age")) {
			setAge(rs.getByte(name));
		} else if (name.equals("AgeGroupId")) {
			setAgeGroupId(rs.getByte(name));
		} else if (name.equals("HouseholdRepresentative")) {
//			setHouseholdRepresentative(rs.getBoolean(name));
		} else if (name.equals("YearlyIncome")) {
			setYearlyIncome(rs.getInt(name));
		} else if (name.equals("PersonNrInHousehold")) {
//			setPersonNrInHousehold(rs.getShort(name));
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
		recordSet.remove(this);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#saveToDatabase()
	 */
	@Override
	public void saveToDatabase() throws SQLException {
		// TODO: make this more sophisticated depending on whether a parameter is set or not
		// INSERT: "PersonId", "HouseholdId", "Sex", "YearBorn", "AgeGroupId", "HouseholdRepresentative", "YearlyIncome"
		if (psInsert != null) {
			psInsert.setString(1, Long.toString(id));
			psInsert.setString(2, Long.toString(householdId));
			psInsert.setString(3, Short.toString(sex));
//			psInsert.setString(4, Integer.toString(yearBorn));
			psInsert.setString(4, Integer.toString(age));
			psInsert.setString(5, Short.toString(ageGroupId));
//			if (householdRepresentative) {
//				psInsert.setString(6, "-1");
//			} else {
//				psInsert.setString(6, "0");
//			}
			psInsert.setString(7, Long.toString(yearlyIncome));
//			psInsert.setString(8, Short.toString(personNrInHousehold));
		}
		// UPDATE: "HouseholdId", "Sex", "YearBorn", "AgeGroupId", "HouseholdRepresentative", "YearlyIncome", "PersonId"
		if (psUpdate != null) {
			psUpdate.setString(1, Long.toString(householdId));
			psUpdate.setString(2, Short.toString(sex));
//			psUpdate.setString(3, Integer.toString(yearBorn));
			psUpdate.setString(3, Integer.toString(age));
			psUpdate.setString(4, Short.toString(ageGroupId));
//			psUpdate.setString(5, Boolean.toString(householdRepresentative));
			psUpdate.setString(6, Long.toString(yearlyIncome));
//			psUpdate.setString(7, Short.toString(personNrInHousehold));
			// UPDATE: WHERE
			psUpdate.setString(8, Long.toString(id));
		}
	}

	/**
	 * @param personIdSeq the personIdSeq to set
	 */
	public static void setPersonIdSeq(Sequence personIdSeq) {
		PersonRow.personIdSeq = personIdSeq;
	}
}
