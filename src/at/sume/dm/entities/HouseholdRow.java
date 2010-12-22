/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import net.remesch.db.Sequence;
import net.remesch.db.schema.Ignore;
import at.sume.db.RecordSetRow;
import at.sume.dm.Common;
import at.sume.dm.indicators.AllHouseholdsIndicatorsPerHouseholdTypeAndIncome;
import at.sume.dm.indicators.managers.MoversIndicatorManager;
import at.sume.dm.model.residential_mobility.DwellingsOnMarket;
import at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionManager;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.IncomeGroup;
//import at.sume.dm.types.ReasonForMoving;

/**
 * @author Alexander Remesch
 *
 */
public class HouseholdRow extends RecordSetRow<Households> {
	private class SpatialUnitScore {
		private long spatialUnitId;
		private long score;
		/**
		 * @return the spatialUnitId
		 */
		public long getSpatialUnitId() {
			return spatialUnitId;
		}
		/**
		 * @param spatialUnitId the spatialUnitId to set
		 */
		public void setSpatialUnitId(long spatialUnitId) {
			this.spatialUnitId = spatialUnitId;
		}
		/**
		 * @return the score
		 */
		public long getScore() {
			return score;
		}
		/**
		 * @param score the score to set
		 */
		public void setScore(long score) {
			this.score = score;
		}
	}
	
	class CompareSpatialUnitScore implements Comparator<SpatialUnitScore> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SpatialUnitScore arg0, SpatialUnitScore arg1) {
			return ((Long)arg0.getScore()).compareTo(arg1.getScore());
		}
	}
	
	private static float childrenWeight = 0;
	private static byte childrenMaxAge = 0;
	private static byte desiredLivingSpaceRandomPct = 0;
	private static byte desiredLivingSpaceRangePct = 0;
	@Ignore
	private static Sequence householdIdSeq = null;

//	private short householdSize;
	private int dwellingIdInp;
	private ArrayList<PersonRow> members;
	private DwellingRow dwelling;
	private float residentialSatisfactionThreshMod;
	private HouseholdType householdType;
	private short movingDecisionYear = 0;
//	private ReasonForMoving movingDecisionReason;
	private short aspirationRegionLivingSpaceMin;
	private short aspirationRegionLivingSpaceMax;
	private int aspirationRegionMaxCosts;
	private ArrayList<SpatialUnitScore> residentialSatisfactionEstimate;
	private short currentResidentialSatisfaction;
	// residential satisfaction components
	public short rsUdp;
	public short rsCostEffectiveness;
	public short rsEnvironmentalAmenities;
	public short rsSocialPrestige;
	public short rsDesiredLivingSpace;
	
	/**
	 * 
	 */
	public HouseholdRow() {
		members = new ArrayList<PersonRow>();
		if (childrenWeight == 0) {
			String sp = Common.getSysParam("ChildrenWeight");
			if (sp.equals(null))
				childrenWeight = 0.5F;
			else
				childrenWeight = Float.parseFloat(sp);
			
		}
		if (childrenMaxAge == 0) {
			String sp = Common.getSysParam("ChildrenMaxAge");
			if (sp.equals(null))
				childrenWeight = 14;
			else
				childrenMaxAge = Byte.parseByte(sp);
		}
		if (desiredLivingSpaceRandomPct == 0) {
			String sp = Common.getSysParam("DesiredLivingSpaceRandomPct");
			if (sp.equals(null))
				desiredLivingSpaceRandomPct = 10;
			else
				desiredLivingSpaceRandomPct = Byte.parseByte(sp);
		}
		if (desiredLivingSpaceRangePct == 0) {
			String sp = Common.getSysParam("DesiredLivingSpaceRangePct");
			if (sp.equals(null))
				desiredLivingSpaceRangePct = 10;
			else
				desiredLivingSpaceRangePct = Byte.parseByte(sp);
		}
		if (householdIdSeq != null) {
			setHouseholdId(householdIdSeq.getNext());
		}
	}

	/**
	 * @return the householdId
	 */
	public int getHouseholdId() {
//		if (id == null)
//			return 0;
//		else
			return id;
	}

	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(int householdId) {
		this.id = householdId;
	}

	/**
	 * @return the spatialunitId of the dwelling
	 */
	public int getSpatialunitId() {
		assert dwelling != null : "no dwelling for household " + getHouseholdId();
		return dwelling.getSpatialunitId();
	}

	/**
	 * @return the householdSize
	 */
	public short getHouseholdSize() {
		return (short) members.size();
	}

//	/**
//	 * @param householdSize the householdSize to set
//	 */
//	public void setHouseholdSize(short householdSize) {
//		if (householdSize < 1 || householdSize > 4)
//			throw new IllegalArgumentException("householdSize must be in the range from 1 to 4");
//		this.householdSize = householdSize;
//	}

	/**
	 * @return the dwellingId
	 */
	public int getDwellingId() {
		return getDwelling().getDwellingId();
	}

	public void addMembers(ArrayList<PersonRow> members){
		this.members.addAll(members);
	}
	
	public void addMember(PersonRow person) {
		this.members.add(person);
		setAspirationRegionLivingSpaceMax((short) 0);
		setAspirationRegionLivingSpaceMin((short) 0);
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
//		} else {
//			if (person.isHouseholdRepresentative()) {
//				// TODO: make the oldest person in the household the household-representative
//				// - but currently this information is not used within the model...
//			}
		}
		setAspirationRegionLivingSpaceMax((short) 0);
		setAspirationRegionLivingSpaceMin((short) 0);
	}
	
	public ArrayList<PersonRow> getMembers() {
		return members;
	}
	
	/**
	 * @return the spatialunit of the dwelling
	 */
	public SpatialUnitRow getSpatialunit() {
		assert dwelling != null : "no dwelling for household " + getHouseholdId();
		return dwelling.getSpatialunit();
	}

	/**
	 * @return the dwelling
	 */
	public DwellingRow getDwelling() {
		return dwelling;
	}

	/**
	 * @param dwelling the dwelling to set
	 */
	public void setDwelling(DwellingRow dwelling) {
		this.dwelling = dwelling;
		setMovingDecisionYear((short) 0);
	}

	/**
	 * @return the residentialSatisfactionThreshMod
	 */
	public float getResidentialSatisfactionThreshMod() {
		return residentialSatisfactionThreshMod;
	}

	/**
	 * @param residentialSatisfactionThreshMod the residentialSatisfactionThreshMod to set
	 */
	public void setResidentialSatisfactionThreshMod(
			float residentialSatisfactionThreshMod) {
		this.residentialSatisfactionThreshMod = residentialSatisfactionThreshMod;
	}

	/**
	 * @param socialPrestigeThreshMod the socialPrestigeThreshMod to set
	 */
	public void setSocialPrestigeThreshMod(float socialPrestigeThreshMod) {
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
		switch (getHouseholdSize()) {
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
		default:
			assert (getHouseholdSize() > 0) && (getHouseholdSize() <= 9) : "Invalid household size: " + getHouseholdSize();
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
				if (getHouseholdSize() == 3)
					this.householdType = HouseholdType.SMALL_FAMILY;	// TODO: maybe we should look for male/female relationships here?
				else
					this.householdType = HouseholdType.OTHER;
				break;
			case 2:
				if (getHouseholdSize() == 3)
					this.householdType = HouseholdType.SINGLE_PARENT;
				else
					this.householdType = HouseholdType.LARGE_FAMILY;
				break;
			case 3:
				this.householdType = HouseholdType.SINGLE_PARENT;
				break;
			default:
//				throw new AssertionError("Household with 4 children unexpected");
				// TODO: do something about this in the synthetic population generation!
				System.out.println("Household " + getHouseholdId() + " unexpectedly consists of " + numChildren + " children (total of " + getHouseholdSize() + " persons).");
				this.householdType = HouseholdType.OTHER;
				break;
			}
			break;
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
	 * @return the movingDecisionYear
	 */
	public short getMovingDecisionYear() {
		return movingDecisionYear;
	}

	/**
	 * @param movingDecisionYear the movingDecisionYear to set
	 */
	public void setMovingDecisionYear(short movingDecisionYear) {
		this.movingDecisionYear = movingDecisionYear;
	}

//	/**
//	 * @return the movingDecisionReason
//	 */
//	public ReasonForMoving getMovingDecisionReason() {
//		return movingDecisionReason;
//	}
//
//	/**
//	 * @param movingDecisionReason the movingDecisionReason to set
//	 */
//	public void setMovingDecisionReason(ReasonForMoving movingDecisionReason) {
//		this.movingDecisionReason = movingDecisionReason;
//	}

	/**
	 * @return the aspirationRegionLivingSpaceMin
	 */
	public short getAspirationRegionLivingSpaceMin() {
		return aspirationRegionLivingSpaceMin;
	}

	/**
	 * @param aspirationRegionLivingSpaceMin the aspirationRegionLivingSpaceMin to set
	 */
	public void setAspirationRegionLivingSpaceMin(
			short aspirationRegionLivingSpaceMin) {
		this.aspirationRegionLivingSpaceMin = aspirationRegionLivingSpaceMin;
	}

	/**
	 * @return the aspirationRegionLivingSpaceMax
	 */
	public short getAspirationRegionLivingSpaceMax() {
		return aspirationRegionLivingSpaceMax;
	}

	/**
	 * @param aspirationRegionLivingSpaceMax the aspirationRegionLivingSpaceMax to set
	 */
	public void setAspirationRegionLivingSpaceMax(
			short aspirationRegionLivingSpaceMax) {
		this.aspirationRegionLivingSpaceMax = aspirationRegionLivingSpaceMax;
	}

	/**
	 * @return the maximum yearly rent per m² that the household is willing/able to pay for a new dwelling
	 */
	public int getAspirationRegionMaxCosts() {
		return aspirationRegionMaxCosts;
	}

	/**
	 * @param aspirationRegionMaxCosts the maximum yearly rent per m² that the household is willing/able to pay for a new dwelling to set
	 */
	public void setAspirationRegionMaxCosts(int aspirationRegionMaxCosts) {
		this.aspirationRegionMaxCosts = aspirationRegionMaxCosts;
	}

//	/**
//	 * @return the households
//	 */
//	public Households getHouseholds() {
//		return recordSet;
//	}
//
//	/**
//	 * @param households the households to set
//	 */
//	public void setHouseholds(Households households) {
//		this.recordSet = households;
//	}

	/**
	 * @return the livingSpace of the dwelling
	 */
	public short getLivingSpace() {
		if (dwelling != null)
			return dwelling.getDwellingSize();
		else
			return 0;
	}

	/**
	 * @return the yearly total rent for the dwelling
	 */
	public int getCostOfResidence() {
		assert dwelling != null : "no dwelling for household " + getHouseholdId();
		return dwelling.getTotalYearlyDwellingCosts();
	}

	/**
	 * @return the livingSpaceGroupId of the dwelling
	 */
	public short getLivingSpaceGroupId() {
		assert dwelling != null : "no dwelling for household " + getHouseholdId();
		return dwelling.getLivingSpaceGroup6Id();
	}

	/**
	 * @return the costOfResidenceGroupId
	 */
	public short getCostOfResidenceGroupId() {
		assert dwelling != null : "no dwelling for household " + getHouseholdId();
		return dwelling.getCostOfResidenceGroupId();
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("HouseholdId")) {
			setHouseholdId(rs.getInt(name));
//		} else if (name.equals("SpatialunitId")) {
//			setSpatialunitId(rs.getLong(name));
//		} else if (name.equals("HouseholdSize")) {
//			setHouseholdSize(rs.getShort(name));
		} else if (name.equals("DwellingId")) {
			setDwellingIdInp(rs.getInt(name));
//		} else if (name.equals("LivingSpace")) {
//			setDwellingId(rs.getInt(name));
//		} else if (name.equals("CostOfResidence")) {
//			setDwellingId(rs.getLong(name));
//		} else if (name.equals("LivingSpaceGroupId")) {
//			setLivingSpaceGroupId(rs.getShort(name));
//		} else if (name.equals("CostOfResidenceGroupId")) {
//			setCostOfResidenceGroupId(rs.getShort(name));
		} else {
			throw new AssertionError("Unknown field name " + name);
		}
	}

	/**
	 * Calculate and return the yearly household income
	 * @return the yearly income
	 */
	public int getYearlyIncome() {
		int yearlyIncome = 0;
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
	 * Get the number of household members
	 * @return
	 */
	public int getMemberCount() {
		return getMembers().size();
	}
	/**
	 * Get the number of household members with children weighted by a factor defined as a systemparameter
	 * @return
	 */
	public double getWeightedMemberCount() {
		double memberCount = 0;
		for (PersonRow person : members) {
			if (person.getAge() <= childrenMaxAge) {
				memberCount += childrenWeight;
			} else {
				memberCount++;
			}
		}
		return memberCount;
	}
	
	/**
	 * Calculate and return the yearly household income per weighted household member
	 * Weight is a factor children are multiplied with
	 * @return
	 */
	public long getYearlyIncomePerMemberWeighted() {
		long yearlyIncome = getYearlyIncome();
		double memberCount = getWeightedMemberCount();
//		for (PersonRow person : members) {
//			yearlyIncome += person.getYearlyIncome();
//			if (person.getAge() <= childrenMaxAge) {
//				memberCount += childrenWeight;
//			} else {
//				memberCount++;
//			}
//		}
		return Math.round(yearlyIncome / memberCount);
	}
	
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#saveToDatabase()
	 */
	@Override
	public void saveToDatabase() throws SQLException {
		// TODO: this is completely obsolete now (101023) and should be replaced by a general insert/update routine
		// INSERT: "HouseholdId", "SpatialunitId", "HouseholdSize", "DwellingId", "LivingSpace", "CostOfResidence"
		if (psInsert != null) {
			psInsert.setString(1, Long.toString(getHouseholdId()));
//			psInsert.setString(2, Long.toString(spatialunitId));
//			psInsert.setString(3, Long.toString(householdSize));
//			psInsert.setString(4, Long.toString(dwellingId));
//			psInsert.setString(5, Integer.toString(livingSpace));
//			psInsert.setString(6, Long.toString(costOfResidence));
//			psInsert.setString(7, Integer.toString(livingSpaceGroupId));
//			psInsert.setString(8, Long.toString(costOfResidenceGroupId));
		}
		// UPDATE: "SpatialunitId", "HouseholdSize", "DwellingId", "LivingSpace", "CostOfResidence", "HouseholdId"
		if (psUpdate != null) {
//			psUpdate.setString(1, Long.toString(spatialunitId));
//			psUpdate.setString(2, Long.toString(householdSize));
//			psUpdate.setString(3, Long.toString(dwellingId));
//			psUpdate.setString(4, Integer.toString(livingSpace));
//			psUpdate.setString(5, Long.toString(costOfResidence));
//			psUpdate.setString(6, Integer.toString(livingSpaceGroupId));
//			psUpdate.setString(7, Long.toString(costOfResidenceGroupId));
			// UPDATE: WHERE
			psUpdate.setString(8, Long.toString(getHouseholdId()));
		}
	}
	
	/**
	 * Estimate the desired living space of the household based on the number of persons in the household 
	 * and set the values of aspirationRegionLivingSpaceMin and aspirationRegionLivingSpaceMax according to the results
	 */
	public void estimateDesiredLivingSpace() {
		if (getAspirationRegionLivingSpaceMin() == 0 && getAspirationRegionLivingSpaceMax() == 0) {
			Random r = new Random();
			// calculate mean
			long desiredLivingSpaceSqm = AllHouseholdsIndicatorsPerHouseholdTypeAndIncome.getAvgLivingSpacePerHousehold(getHouseholdType(), IncomeGroup.getIncomeGroupId(getYearlyIncome()));
//			short desiredLivingSpaceModifier = (short) (100 + desiredLivingSpaceRandomPct * r.nextGaussian());
//			desiredLivingSpaceSqm = Math.round(desiredLivingSpaceSqm * desiredLivingSpaceModifier / 100);
			// calculate boundary 1
			short desiredLivingSpaceModifier = (short) (110 + desiredLivingSpaceRangePct * r.nextGaussian());
			short desiredLivingSpaceSqm1 = (short) Math.round(desiredLivingSpaceSqm * desiredLivingSpaceModifier / 100);
			// calculate boundary 2
			desiredLivingSpaceModifier = (short) (90 - desiredLivingSpaceRangePct * r.nextGaussian());
			short desiredLivingSpaceSqm2 = (short) Math.round(desiredLivingSpaceSqm * desiredLivingSpaceModifier / 100);
			if (desiredLivingSpaceSqm1 > desiredLivingSpaceSqm2) {
				setAspirationRegionLivingSpaceMin(desiredLivingSpaceSqm2);
				setAspirationRegionLivingSpaceMax(desiredLivingSpaceSqm1);
			} else  {
				setAspirationRegionLivingSpaceMin(desiredLivingSpaceSqm1);
				setAspirationRegionLivingSpaceMax(desiredLivingSpaceSqm2);
			}
		}
	}
	
//	/**
//	 * Estimate residential satisfaction for the given spatial units and store results in array
//	 * residentialSatisfactionEstimate
//	 * 
//	 * @param spatialUnitIdList
//	 * @param modelYear
//	 * @return highest residential satisfaction found
//	 */
//	public int estimateResidentialSatisfaction(ArrayList<Long> spatialUnitIdList, int modelYear) {
//		SpatialUnits spatialUnits = recordSet.getSpatialunits();
//		int result = 0;
//		for (Long spatialUnitId : spatialUnitIdList) {
//			SpatialUnitScore s = new SpatialUnitScore();
//			s.setSpatialUnitId(spatialUnitId);
//			// TODO: introduce random factor into score (sysparam ~10%?)
//			int residentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(this, spatialUnits.lookup(spatialUnitId), modelYear); 
//			s.setScore(residentialSatisfaction);
//			residentialSatisfactionEstimate.add(s);
//			if (residentialSatisfaction > result)
//				result = residentialSatisfaction;
//		}
//		return result;
//	}
	/**
	 * Estimate residential satisfaction for the given spatial units and store results in array
	 * residentialSatisfactionEstimate
	 * 
	 * @param spatialUnitIdList
	 * @param modelYear
	 * @return highest residential satisfaction found
	 */
	public int estimateResidentialSatisfaction(ArrayList<SpatialUnitRow> spatialUnitList, int modelYear) {
		int result = 0;
		assert spatialUnitList.size() > 0 : "spatialUnitList must be initialized (size > 0)";
		residentialSatisfactionEstimate = new ArrayList<SpatialUnitScore>(spatialUnitList.size());
		for (SpatialUnitRow spatialUnit : spatialUnitList) {
			SpatialUnitScore s = new SpatialUnitScore();
			s.setSpatialUnitId(spatialUnit.getSpatialUnitId());
			// TODO: introduce random factor into score (sysparam ~10%?)
			int residentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(this, spatialUnit, modelYear); 
			s.setScore(residentialSatisfaction);
			residentialSatisfactionEstimate.add(s);
			if (residentialSatisfaction > result)
				result = residentialSatisfaction;
		}
		return result;
	}
	/**
	 * Return the spatial units that are estimated to provide the highest residential satisfaction
	 * for the household
	 * 
	 * @param numSpatialUnits the number of spatial units to return
	 * @return
	 * TODO: prefer target areas that are close to the current residence
	 */
	public ArrayList<Long> getPreferredSpatialUnits(int numSpatialUnits) {
		// sort results descending according to residential satisfaction (find highest scoring spatial units)
		assert residentialSatisfactionEstimate.size() > 0 : "residentialSatisfactionEstimate must be initialized! (size = 0)";
		if (residentialSatisfactionEstimate.size() < numSpatialUnits)
			numSpatialUnits = residentialSatisfactionEstimate.size();
		Comparator<SpatialUnitScore> compareSpatialUnitScoreDesc = Collections.reverseOrder(new CompareSpatialUnitScore());
		Collections.sort(residentialSatisfactionEstimate, compareSpatialUnitScoreDesc);
		// build new array to return
		ArrayList<Long> ret = new ArrayList<Long>(numSpatialUnits);
		for (int i = 0; i != numSpatialUnits; i++) {
			ret.add(residentialSatisfactionEstimate.get(i).getSpatialUnitId());
		}
		return ret;
	}
	public void clearResidentialSatisfactionEstimate() {
		residentialSatisfactionEstimate.clear();
		residentialSatisfactionEstimate = null;
	}
	/**
	 * Relocate the household to the given dwelling
	 * 
	 * @param dwelling
	 */
	public void relocate(DwellingsOnMarket dwellingsOnMarket, DwellingRow dwelling) {
		if (hasDwelling())
			dwellingsOnMarket.putDwellingOnMarket(getDwelling());
		dwellingsOnMarket.removeDwellingFromMarket(dwelling);
		setDwelling(dwelling);
		dwelling.setHousehold(this);
		// Update indicators
		MoversIndicatorManager.addHousehold(this);
	}

	/**
	 * @param currentResidentialSatisfaction the currentResidentialSatisfaction to set
	 */
	public void setCurrentResidentialSatisfaction(
			short currentResidentialSatisfaction) {
		this.currentResidentialSatisfaction = currentResidentialSatisfaction;
	}

	/**
	 * @return the currentResidentialSatisfaction
	 */
	public short getCurrentResidentialSatisfaction() {
		return currentResidentialSatisfaction;
	}
	/**
	 * Does the household have a dwelling?
	 * @return
	 */
	public boolean hasDwelling() {
		return (dwelling != null);
	}

	/**
	 * @param householdIdSeq the householdIdSeq to set
	 */
	public static void setHouseholdIdSeq(Sequence householdIdSeq) {
		HouseholdRow.householdIdSeq = householdIdSeq;
	}

	/**
	 * @param dwellingIdInp the dwellingIdInp to set
	 */
	public void setDwellingIdInp(int dwellingIdInp) {
		this.dwellingIdInp = dwellingIdInp;
	}

	/**
	 * @return the dwellingIdInp
	 */
	public int getDwellingIdInp() {
		return dwellingIdInp;
	}
}
