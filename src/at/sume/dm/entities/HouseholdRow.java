/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import at.sume.db.RecordSetRow;
import at.sume.dm.Common;
import at.sume.dm.types.HouseholdType;

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
	private double residentialSatisfactionThreshMod;
	private HouseholdType householdType;
	
	// the following parameters might eventually go into a separate Dwelling-class
	private int livingSpace;
	private long costOfResidence;
	private short livingSpaceGroupId;
	private short costOfResidenceGroupId;
	private static double childrenWeight = 0;
	private static short childrenMaxAge = 0;

	public HouseholdRow(Households households) {
		super(households);
		members = new ArrayList<PersonRow>();
		if (childrenWeight == 0) {
			String sp = Common.getSysParam("ChildrenWeight");
			if (sp.equals(null))
				childrenWeight = 0.5;
			else
				childrenWeight = Double.parseDouble(sp);
			
		}
		if (childrenMaxAge == 0) {
			String sp = Common.getSysParam("ChildrenMaxAge");
			if (sp.equals(null))
				childrenWeight = 14;
			else
				childrenMaxAge = Short.parseShort(sp);
		}
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
	 * @param socialPrestigeThreshMod the socialPrestigeThreshMod to set
	 */
	public void setSocialPrestigeThreshMod(double socialPrestigeThreshMod) {
		this.residentialSatisfactionThreshMod = socialPrestigeThreshMod;
	}

	/**
	 * @return the socialPrestigeThreshMod
	 */
	public double getSocialPrestigeThreshMod() {
		return residentialSatisfactionThreshMod;
	}

	/**
	 * Determine the household type from the household structure and save it for later use
	 * This function is only intended for the first determination of the household type. Later on in the
	 * model run, it makes more sense to determine the household type through the type of demographic event
	 * happening to the household
	 * @param householdType the householdType to set
	 */
	public HouseholdType determineInitialHouseholdType() {
		short age1, age2, sex1, sex2;
		switch (householdSize) {
		case 1:
			if (members.get(0).getAge() <= 45)
				this.householdType = HouseholdType.SINGLE_YOUNG;
			else
				this.householdType = HouseholdType.SINGLE_OLD;
			break;
		case 2:
			age1 = members.get(0).getAge();
			age2 = members.get(1).getAge();
			sex1 = members.get(0).getSex();
			sex2 = members.get(1).getSex();
			if (Math.abs(age1 - age2) > 15) {
				// this could be either parent-child, a couple or a flat-sharing community
				// the most common case is parent with a young child so we ignore the other cases for now
				// - no change if the household type is already set
				if (this.householdType == null) {
					if ((Math.min(age1, age2) > 25) && (sex1 != sex2)) {
						this.householdType = HouseholdType.COUPLE_YOUNG;
					} else {
						this.householdType = HouseholdType.SINGLE_PARENT;
					}
				}
			} else {
				if (sex1 == sex2) {
					// heterosexuality is not seen as necessity for being a couple here - a flat-sharing
					// community might be more common in that case, but don't have data to decide that
					// currently
					if ((age1 > 45) || (age2 > 45)) {
						this.householdType = HouseholdType.COUPLE_OLD;
					} else {
						this.householdType = HouseholdType.COUPLE_YOUNG;
					}
				} else {
					// the age of the female determines whether it is a "young" or "old" couple, i.e. whether
					// it can become a family or not
					if (sex1 == 1) {	// person 1 is the female
						if (age1 > 45) {
							this.householdType = HouseholdType.COUPLE_OLD;
						} else {
							this.householdType = HouseholdType.COUPLE_YOUNG;
						}
					} else {			// person 2 is the female 
						if (age2 > 45) {
							this.householdType = HouseholdType.COUPLE_OLD;
						} else {
							this.householdType = HouseholdType.COUPLE_YOUNG;
						}
					}
				}
			}
			break;
		case 3:
		case 4:
			// determine the number of children
			int numChildren = 0;
			for (PersonRow member : members) {
				if (member.getAge() < 19)
					numChildren++;
			}
			switch (numChildren) {
			case 0:
				this.householdType = HouseholdType.OTHER;
				break;
			case 1:
				if (householdSize == 3)
					this.householdType = HouseholdType.SMALL_FAMILY;	// TODO: maybe we should look for mal/female relationships here?
				else
					this.householdType = HouseholdType.OTHER;
				break;
			case 2:
				if (householdSize == 3)
					this.householdType = HouseholdType.SINGLE_PARENT;
				else
					this.householdType = HouseholdType.LARGE_FAMILY;
				break;
			case 3:
				this.householdType = HouseholdType.SINGLE_PARENT;
				break;
			case 4:
				throw new AssertionError("Household with 4 children unexpected");
			}
			break;
		default:
			throw new AssertionError("Unexpeceted HouseholdSize = " + householdSize);
		}
		return this.householdType;
	}

	/**
	 * Setter for the household type - should be only necessary in the demographic module for transitions
	 * between household-types
	 * @param householdType
	 */
	public void setHouseholdType(HouseholdType householdType) {
		this.householdType = householdType;
	}
	
	/**
	 * Return the stored household type calculated previously by determineHouseholdType()
	 * @return the householdType
	 */
	public HouseholdType getHouseholdType() {
		return householdType;
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

	/**
	 * Calculate and return the yearly household income per household member
	 * @return the yearly household income per household member
	 */
	public long getYearlyIncomePerMember() {
		return getYearlyIncome() / getMembers().size();
	}
	
	/**
	 * Calculate and return the yearly household income per weighted household member
	 * Weight is a factor children are multiplied with
	 * @return
	 */
	public long getYearlyIncomePerMemberWeighted() {
		long yearlyIncome = 0;
		double memberCount = 0;
		for (PersonRow person : members) {
			yearlyIncome += person.getYearlyIncome();
			if (person.getAge() <= childrenMaxAge) {
				memberCount += childrenWeight;
			} else {
				memberCount++;
			}
		}
		return Math.round(yearlyIncome / memberCount);
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
