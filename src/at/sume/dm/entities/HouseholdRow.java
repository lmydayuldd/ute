/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
	
	// the following parameters might eventually go into a separate Dwelling-class
	private long livingSpace;
	private long costOfResidence;

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

	/**
	 * @return the livingSpace
	 */
	public long getLivingSpace() {
		return livingSpace;
	}

	/**
	 * @param livingSpace the livingSpace to set
	 */
	public void setLivingSpace(long livingSpace) {
		this.livingSpace = livingSpace;
	}

	/**
	 * @return the costOfResidence
	 */
	public long getCostOfResidence() {
		return costOfResidence;
	}

	/**
	 * @param costOfResidence the costOfResidence to set
	 */
	public void setCostOfResidence(long costOfResidence) {
		this.costOfResidence = costOfResidence;
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
		} else if (name.equals("LivingSpace")) {
			setDwellingId(rs.getInt(name));
		} else if (name.equals("CostOfResidence")) {
			setDwellingId(rs.getLong(name));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}

	/**
	 * Remove this record from the list
	 */
	@Override
	public void remove() {
		households.remove(this);
	}

	/**
	 * Calculate and return the yearly household income
	 * @return the yearly income
	 */
	public long getYearlyIncome() {
		long yearlyIncome = 0;
		for (PersonRow person : members) {
			yearlyIncome += person.getYearlyIncome();
		}
		return yearlyIncome;
	}
}
