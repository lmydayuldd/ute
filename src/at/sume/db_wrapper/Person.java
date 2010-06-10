package at.sume.db_wrapper;

import java.sql.SQLException;

import at.sume.generate_population.Database;

/**
 * Database wrapper class for one record of table _DM_Households
 * 
 * @author Alexander Remesch
 * 
 */
public class Person extends DatabaseRecord {
	private long PersonId;
	private long HouseholdId;
	private short Sex;
	private int YearBorn;
	private boolean HouseholdRepresentative;
	private long YearlyIncome;
	
	public Person(Database db) throws SQLException {
		super(db);
		String sqlx = "insert into _DM_Households (HouseholdId, SpatialunitId, HouseholdSize) values (?, ?, ?)";
		prepareStatement(sqlx);
	}

	public long getPersonId() {
		return PersonId;
	}
	public void setPersonId(long personId) {
		PersonId = personId;
	}
	public long getHouseholdId() {
		return HouseholdId;
	}
	public void setHouseholdId(long householdId) {
		HouseholdId = householdId;
	}
	public short getSex() {
		return Sex;
	}
	public void setSex(short sex) {
		if (sex < 1 || sex > 2)
			throw new IllegalArgumentException("sex must be in the range from 1 to 2");
		Sex = sex;
	}
	public int getYearBorn() {
		return YearBorn;
	}
	public void setYearBorn(int yearBorn) {
		// TODO: find a more flexible way to check the validity of the years
		if (yearBorn < 1880 || yearBorn > 2015)
			throw new IllegalArgumentException("yearBorn must be in the range from 1880 to 2015");
		YearBorn = yearBorn;
	}
	public boolean isHouseholdRepresentative() {
		return HouseholdRepresentative;
	}
	public void setHouseholdRepresentative(boolean householdRepresentative) {
		HouseholdRepresentative = householdRepresentative;
	}
	public long getYearlyIncome() {
		return YearlyIncome;
	}
	public void setYearlyIncome(long yearlyIncome) {
		YearlyIncome = yearlyIncome;
	}
	@Override
	public void dbInsert() throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
