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
public class HouseholdRow extends RecordSetRow<Households> {
	private long spatialunitId;
	private short householdSize;
	private long dwellingId;
	private ArrayList<PersonRow> members;
	private SpatialUnitRow spatialunit;
	
	// the following parameters might eventually go into a separate Dwelling-class
	private int livingSpace;
	private long costOfResidence;
	private short livingSpaceGroupId;
	private short costOfResidenceGroupId;

	public HouseholdRow(Households households) {
		super(households);
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
		if (householdSize < 1 || householdSize > 4)
			throw new IllegalArgumentException("householdSize must be in the range from 1 to 4");
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
			recordSet.remove(this);
		} else {
			if (person.isHouseholdRepresentative()) {
				// TODO: make the oldest person in the household the household-representative
				// - but currently this information is not used within the model...
			}
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
		return recordSet;
	}

	/**
	 * @param households the households to set
	 */
	public void setHouseholds(Households households) {
		this.recordSet = households;
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
	public void setLivingSpace(int livingSpace) {
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

	/**
	 * @return the livingSpaceGroupId
	 */
	public short getLivingSpaceGroupId() {
		return livingSpaceGroupId;
	}

	/**
	 * @param livingSpaceGroupId the livingSpaceGroupId to set
	 */
	public void setLivingSpaceGroupId(short livingSpaceGroupId) {
		this.livingSpaceGroupId = livingSpaceGroupId;
	}

	/**
	 * @return the costOfResidenceGroupId
	 */
	public short getCostOfResidenceGroupId() {
		return costOfResidenceGroupId;
	}

	/**
	 * @param costOfResidenceGroupId the costOfResidenceGroupId to set
	 */
	public void setCostOfResidenceGroupId(short costOfResidenceGroupId) {
		this.costOfResidenceGroupId = costOfResidenceGroupId;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
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
		} else if (name.equals("LivingSpaceGroupId")) {
			setLivingSpaceGroupId(rs.getShort(name));
		} else if (name.equals("CostOfResidenceGroupId")) {
			setCostOfResidenceGroupId(rs.getShort(name));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
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

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#saveToDatabase()
	 */
	@Override
	public void saveToDatabase() throws SQLException {
		// TODO: make this more sophisticated depending on whether a parameter is set or not
		// INSERT: "HouseholdId", "SpatialunitId", "HouseholdSize", "DwellingId", "LivingSpace", "CostOfResidence"
		if (psInsert != null) {
			psInsert.setString(1, Long.toString(getHouseholdId()));
			psInsert.setString(2, Long.toString(spatialunitId));
			psInsert.setString(3, Long.toString(householdSize));
			psInsert.setString(4, Long.toString(dwellingId));
			psInsert.setString(5, Integer.toString(livingSpace));
			psInsert.setString(6, Long.toString(costOfResidence));
			psInsert.setString(7, Integer.toString(livingSpaceGroupId));
			psInsert.setString(8, Long.toString(costOfResidenceGroupId));
		}
		// UPDATE: "SpatialunitId", "HouseholdSize", "DwellingId", "LivingSpace", "CostOfResidence", "HouseholdId"
		if (psUpdate != null) {
			psUpdate.setString(1, Long.toString(spatialunitId));
			psUpdate.setString(2, Long.toString(householdSize));
			psUpdate.setString(3, Long.toString(dwellingId));
			psUpdate.setString(4, Integer.toString(livingSpace));
			psUpdate.setString(5, Long.toString(costOfResidence));
			psUpdate.setString(6, Integer.toString(livingSpaceGroupId));
			psUpdate.setString(7, Long.toString(costOfResidenceGroupId));
			// UPDATE: WHERE
			psUpdate.setString(8, Long.toString(getHouseholdId()));
		}
	}
}
