/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import at.sume.db.RecordSetRow;
import at.sume.dm.demography.events.EventObservable;
import at.sume.dm.demography.events.EventObserver;

/**
 * @author Alexander Remesch
 *
 */
public class PersonRow extends RecordSetRow implements EventObservable {
//	private long personId;
	private long householdId;
	private short sex;
	private int yearBorn;
	private short ageGroupId;
	private boolean householdRepresentative;
	private long yearlyIncome;
	private HouseholdRow household;
	private Persons persons;
	private ArrayList<EventObserver> observers;
	
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

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#primaryKeyEquals(java.lang.Object[])
	 */
	@Override
	public boolean primaryKeyEquals(Object... lookupKeys) {
		if (lookupKeys.length != 1) {
			throw new IllegalArgumentException("PK is only one field");
		}
		if (lookupKeys[0] instanceof Long) {
			long lookupKey = (Long) lookupKeys[0];
			if (lookupKey == getPersonId())
				return true;
			else
				return false;
		} else {
			throw new IllegalArgumentException("PK must by of type Long");
		}
	}
	
	/**
	 * Remove this record from the list
	 */
	@Override
	public void remove() {
		persons.remove(this);
	}

	/* (non-Javadoc)
	 * @see net.remesch.util.Observable#notifyObservers()
	 */
	@Override
	public void notifyObservers() {
		for (EventObserver observer : observers) {
			observer.notify();
		}
	}

	/* (non-Javadoc)
	 * @see net.remesch.util.Observable#registerObserver(net.remesch.util.Observer)
	 */
	@Override
	public void registerObserver(EventObserver observer) {
		observers.add(observer);
	}

	/* (non-Javadoc)
	 * @see net.remesch.util.Observable#removeObserver(net.remesch.util.Observer)
	 */
	@Override
	public void removeObserver(EventObserver observer) {
		int i = observers.indexOf(observer);
		if (i >= 0) {
			observers.remove(i);
		}
	}
}
