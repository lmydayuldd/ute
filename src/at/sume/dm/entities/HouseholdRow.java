/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public class HouseholdRow extends RecordSetRow {
	private long spatialunitId;
	private short householdSize;
	private long dwellingId;
	private ArrayList<PersonRow> members;
	private SpatialUnitRow spatialunit;
	private Households households;

	public HouseholdRow(Households households) {
		this.households = households;
		members = new ArrayList<PersonRow>();
	}
	
	/**
	 * @return the householdId
	 */
	public long getHouseholdId() {
		return id;
	}

	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(long householdId) {
		this.id = householdId;
	}

	/**
	 * @return the spatialunitId
	 */
	public long getSpatialunitId() {
		return spatialunitId;
	}

	/**
	 * @param spatialunitId the spatialunitId to set
	 */
	public void setSpatialunitId(long spatialunitId) {
		this.spatialunitId = spatialunitId;
		this.spatialunit = null;
	}

	/**
	 * @return the householdSize
	 */
	public short getHouseholdSize() {
		return householdSize;
	}

	/**
	 * @param householdSize the householdSize to set
	 */
	public void setHouseholdSize(short householdSize) {
		this.householdSize = householdSize;
	}

	/**
	 * @return the dwellingId
	 */
	public long getDwellingId() {
		return dwellingId;
	}

	/**
	 * @param dwellingId the dwellingId to set
	 */
	public void setDwellingId(long dwellingId) {
		this.dwellingId = dwellingId;
	}

	public void addMembers(ArrayList<PersonRow> members){
		this.members.addAll(members);
	}
	
	public void addMember(PersonRow person) {
		this.members.add(person);
	}
	
	public void removeMember(PersonRow person) {
		int i = members.indexOf(person);
		if (i >= 0) {
			members.remove(i);
//		} else {
//			throw new IllegalArgumentException("Person " + person.getId() + " is not a member of household " + this.getId());
		}
		// remove a household if there are no members left
		if (members.size() <= 0) {
			households.remove(this);
		}
	}
	
	public ArrayList<PersonRow> getMembers() {
		return members;
	}
	
	/**
	 * @return the spatialunit
	 */
	public SpatialUnitRow getSpatialunit() {
		return spatialunit;
	}

	/**
	 * @param spatialunit the spatialunit to set
	 */
	public void setSpatialunit(SpatialUnitRow spatialunit) {
		this.spatialunit = spatialunit;
	}

	/**
	 * @return the households
	 */
	public Households getHouseholds() {
		return households;
	}

	/**
	 * @param households the households to set
	 */
	public void setHouseholds(Households households) {
		this.households = households;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void set(ResultSet rs, String name) throws SQLException {
		if (name.equals("HouseholdId")) {
			setHouseholdId(rs.getLong(name));
		} else if (name.equals("SpatialunitId")) {
			setSpatialunitId(rs.getLong(name));
		} else if (name.equals("HouseholdSize")) {
			setHouseholdSize(rs.getShort(name));
		} else if (name.equals("DwellingId")) {
			setDwellingId(rs.getLong(name));
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
			if (lookupKey == getHouseholdId())
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
		households.remove(this);
	}
}
