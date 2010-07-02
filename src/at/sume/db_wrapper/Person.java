package at.sume.db_wrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.generate_population.Database;

/**
 * Database wrapper class for one record of table _DM_Households
 * 
 * @author Alexander Remesch
 * 
 */
public class Person extends DatabaseRecord {
	private long personId;
	private long householdId;
	private short sex;
	private int yearBorn;
	private short ageGroupId;
	private boolean householdRepresentative;
	private long yearlyIncome;
	
	public Person(Database pdb) throws SQLException {
		super(pdb);
		//db = pdb;
		String sqlx = "insert into _DM_Persons (PersonId, HouseholdId, Sex, HouseholdRepresentative, AgeGroupId) values (?, ?, ?, ?, ?)";
		prepareStatement(sqlx);
	}

	public long getPersonId() {
		return personId;
	}
	public void setPersonId(long personId) throws SQLException {
		this.personId = personId;
		ps.setString(1, Long.toString(personId));
}
	public long getHouseholdId() {
		return householdId;
	}
	public void setHouseholdId(long householdId) throws SQLException {
		this.householdId = householdId;
		ps.setString(2, Long.toString(householdId));
	}
	public short getSex() {
		return sex;
	}
	public void setSex(short sex) throws SQLException {
		if (sex < 1 || sex > 2)
			throw new IllegalArgumentException("sex must be in the range from 1 to 2");
		this.sex = sex;
		ps.setString(3, Short.toString(sex));
	}
	public int getYearBorn() {
		return yearBorn;
	}
	public void setYearBorn(int yearBorn) throws SQLException {
		// TODO: find a more flexible way to check the validity of the years
		if (yearBorn < 1880 || yearBorn > 2015)
			throw new IllegalArgumentException("yearBorn must be in the range from 1880 to 2015");
		this.yearBorn = yearBorn;
		//ps.setString(3, Integer.toString(yearBorn));
	}
	public boolean isHouseholdRepresentative() {
		return householdRepresentative;
	}
	public void setHouseholdRepresentative(boolean householdRepresentative) throws SQLException {
		this.householdRepresentative = householdRepresentative;
		if (householdRepresentative)
			ps.setString(4, "-1");
		else
			ps.setString(4, "0");
	}
	public long getYearlyIncome() {
		return yearlyIncome;
	}
	public void setYearlyIncome(long yearlyIncome) throws SQLException {
		this.yearlyIncome = yearlyIncome;
	}
	/**
	 * @return the ageGroupId
	 */
	public short getAgeGroupId() {
		return ageGroupId;
	}

	/**
	 * @param ageGroupId the ageGroupId to set
	 * @throws SQLException 
	 */
	public void setAgeGroupId(short ageGroupId) throws SQLException {
		this.ageGroupId = ageGroupId;
		ps.setString(5, Short.toString(ageGroupId));
	}
	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
