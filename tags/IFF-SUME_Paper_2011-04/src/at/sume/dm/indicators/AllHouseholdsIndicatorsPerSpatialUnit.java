/**
 * 
 */
package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.indicators.base.Indicator;
import at.sume.dm.model.output.Fileable;

/**
 * @author Alexander Remesch
 *
 */
public class AllHouseholdsIndicatorsPerSpatialUnit implements Indicator<HouseholdRow> {
	// TODO: find common base class with ResidentialObjectRent
	private static class BaseIndicators implements Comparable<BaseIndicators>, Fileable {
		private long spatialUnitId;
		private long householdCount;
		private long personCount;
		private long incomeSum;
		private long incomePerHouseholdMemberSum;
		private long incomePerWeightedHouseholdMemberSum;
		private long costOfResidenceSum;
		private long costOfResidencePerSqmSum;
		private long livingSpacePerHouseholdMemberSum;
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
		 * @return the incomeSum
		 */
		public long getIncomeSum() {
			return incomeSum;
		}
		/**
		 * @param incomeSum the incomeSum to set
		 */
		public void setIncomeSum(long incomeSum) {
			this.incomeSum = incomeSum;
		}
		/**
		 * @return the incomePerHouseholdMemberSum
		 */
		public long getIncomePerHouseholdMemberSum() {
			return incomePerHouseholdMemberSum;
		}
		/**
		 * @param incomePerHouseholdMemberSum the incomePerHouseholdMemberSum to set
		 */
		public void setIncomePerHouseholdMemberSum(long incomePerHouseholdMemberSum) {
			this.incomePerHouseholdMemberSum = incomePerHouseholdMemberSum;
		}
		/**
		 * @return the incomePerWeightedHouseholdMemberSum
		 */
		public long getIncomePerWeightedHouseholdMemberSum() {
			return incomePerWeightedHouseholdMemberSum;
		}
		/**
		 * @param incomePerWeightedHouseholdMemberSum the incomePerWeightedHouseholdMemberSum to set
		 */
		public void setIncomePerWeightedHouseholdMemberSum(
				long incomePerWeightedHouseholdMemberSum) {
			this.incomePerWeightedHouseholdMemberSum = incomePerWeightedHouseholdMemberSum;
		}
		/**
		 * @return the householdCount
		 */
		public long getHouseholdCount() {
			return householdCount;
		}
		/**
		 * @param householdCount the householdCount to set
		 */
		public void setHouseholdCount(long householdCount) {
			this.householdCount = householdCount;
		}
		/**
		 * @return the personCount
		 */
		public long getPersonCount() {
			return personCount;
		}
		/**
		 * @param personCount the personCount to set
		 */
		public void setPersonCount(long personCount) {
			this.personCount = personCount;
		}
		/**
		 * @return the costOfResidenceSum
		 */
		public long getCostOfResidenceSum() {
			return costOfResidenceSum;
		}
		/**
		 * @param costOfResidenceSum the costOfResidenceSum to set
		 */
		public void setCostOfResidenceSum(long costOfResidenceSum) {
			this.costOfResidenceSum = costOfResidenceSum;
		}
		/**
		 * @return the costOfResidencePerSqmSum
		 */
		public long getCostOfResidencePerSqmSum() {
			return costOfResidencePerSqmSum;
		}
		/**
		 * @param costOfResidencePerSqmSum the costOfResidencePerSqmSum to set
		 */
		public void setCostOfResidencePerSqmSum(long costOfResidencePerSqmSum) {
			this.costOfResidencePerSqmSum = costOfResidencePerSqmSum;
		}
		/**
		 * @return the livingSpacePerHouseholdMemberSum
		 */
		public long getLivingSpacePerHouseholdMemberSum() {
			return livingSpacePerHouseholdMemberSum;
		}
		/**
		 * @param livingSpacePerHouseholdMemberSum the livingSpacePerHouseholdMemberSum to set
		 */
		public void setLivingSpacePerHouseholdMemberSum(
				long livingSpacePerHouseholdMemberSum) {
			this.livingSpacePerHouseholdMemberSum = livingSpacePerHouseholdMemberSum;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(BaseIndicators arg0) {
			return ((Long)spatialUnitId).compareTo(arg0.getSpatialUnitId());
		}
		@Override
		public String toCsvHeadline(String delimiter) {
			throw new AssertionError("not yet implemented");
		}
		@Override
		public String toString(String delimiter) {
			throw new AssertionError("not yet implemented");
		}
	}
	private static ArrayList<BaseIndicators> indicatorList;
	
	public AllHouseholdsIndicatorsPerSpatialUnit() {
		indicatorList = new ArrayList<BaseIndicators>();
	}
	
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#build(at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public void add(HouseholdRow household) {
		AllHouseholdsIndicatorsPerSpatialUnit.add(household.getSpatialunitId(), household.getHouseholdSize(), household.getYearlyIncome(), household.getYearlyIncomePerMemberWeighted(), household.getCostOfResidence(), household.getLivingSpace());
	}
	
	private static void add(long spatialUnitId, short memberCount, long income, long incomePerWeightedHouseholdMember, long costOfResidence, long livingSpace) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			BaseIndicators b = new BaseIndicators();
			b.setSpatialUnitId(spatialUnitId);
			b.setIncomeSum(income);
			b.setIncomePerHouseholdMemberSum(income / memberCount);
			b.setIncomePerWeightedHouseholdMemberSum(incomePerWeightedHouseholdMember);
			b.setHouseholdCount(1);
			b.setPersonCount(memberCount);
			b.setCostOfResidenceSum(costOfResidence);
			b.setCostOfResidencePerSqmSum(costOfResidence / livingSpace);
			b.setLivingSpacePerHouseholdMemberSum(livingSpace / memberCount);
			indicatorList.add(pos, b);
		} else {
			// available at position pos
			BaseIndicators b = indicatorList.get(pos);
			b.setIncomeSum(b.getIncomeSum() + income);
			b.setIncomePerHouseholdMemberSum(b.getIncomePerHouseholdMemberSum() + income / memberCount);
			b.setIncomePerWeightedHouseholdMemberSum(b.getIncomePerWeightedHouseholdMemberSum() + incomePerWeightedHouseholdMember);
			b.setHouseholdCount(b.getHouseholdCount() + 1);
			b.setPersonCount(b.getPersonCount() + memberCount);
			b.setCostOfResidenceSum(b.getCostOfResidenceSum() + costOfResidence);
			b.setCostOfResidencePerSqmSum(b.getCostOfResidencePerSqmSum() + costOfResidence / livingSpace);
			b.setLivingSpacePerHouseholdMemberSum(b.getLivingSpacePerHouseholdMemberSum() + livingSpace / memberCount);
			indicatorList.set(pos, b);
		}
	}
	
	public static long getAvgHouseholdIncome(long spatialUnitId) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos >= 0) {
			BaseIndicators b = indicatorList.get(pos);
			return b.getIncomeSum() / b.getHouseholdCount();
		} else {
			return 0;
		}
	}
	
	public static long getAvgPersonIncome(long spatialUnitId) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos >= 0) {
			BaseIndicators b = indicatorList.get(pos);
			return b.getIncomeSum() / b.getPersonCount();
		} else {
			return 0;
		}
	}

	/**
	 * Household income per household member
	 * @param spatialUnitId
	 * @return
	 */
	public static long getAvgHouseholdIncomePerMember(long spatialUnitId) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos >= 0) {
			BaseIndicators b = indicatorList.get(pos);
			return b.getIncomePerHouseholdMemberSum() / b.getHouseholdCount();
		} else {
			return 0;
		}
	}

	/**
	 * Household income per household member weighted for children
	 * @param spatialUnitId
	 * @return
	 */
	public static long getAvgHouseholdIncomePerMemberWeighted(long spatialUnitId) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos >= 0) {
			BaseIndicators b = indicatorList.get(pos);
			return b.getIncomePerWeightedHouseholdMemberSum() / b.getHouseholdCount();
		} else {
			return 0;
		}
	}
	
	@Override
	public void clear() {
		AllHouseholdsIndicatorsPerSpatialUnit.indicatorList.clear();
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#remove(at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public void remove(HouseholdRow household) {
		AllHouseholdsIndicatorsPerSpatialUnit.remove(household.getSpatialunitId(), household.getHouseholdSize(), household.getYearlyIncome(), household.getYearlyIncomePerMemberWeighted(), household.getCostOfResidence(), household.getLivingSpace());
	}

	private static void remove(long spatialUnitId, short memberCount, long income, long incomePerWeightedHouseholdMember, long costOfResidence, long livingSpace) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos < 0) {
			// not there, unable to remove - throw exception
			throw new AssertionError("IndicatorsPerSpatialUnit.remove() - " + spatialUnitId + " is not in the list of spatial units");
		} else {
			// available at position pos - remove
			BaseIndicators b = indicatorList.get(pos);
			b.setIncomeSum(b.getIncomeSum() - income);
			b.setIncomePerHouseholdMemberSum(b.getIncomePerHouseholdMemberSum() - income / memberCount);
			b.setIncomePerWeightedHouseholdMemberSum(b.getIncomePerWeightedHouseholdMemberSum() - incomePerWeightedHouseholdMember);
			b.setHouseholdCount(b.getHouseholdCount() - 1);
			b.setPersonCount(b.getPersonCount() - memberCount);
			b.setCostOfResidenceSum(b.getCostOfResidenceSum() - costOfResidence);
			b.setCostOfResidencePerSqmSum(b.getCostOfResidencePerSqmSum() - costOfResidence / livingSpace);
			b.setLivingSpacePerHouseholdMemberSum(b.getLivingSpacePerHouseholdMemberSum() - livingSpace / memberCount);
			indicatorList.set(pos, b);
			
			assert b.getIncomeSum() >= 0 : "IndicatorsPerSpatialUnit.remove() - " + spatialUnitId + ": incomeSum < 0";
			assert b.getIncomePerHouseholdMemberSum() >= 0 : "IndicatorsPerSpatialUnit.remove() - " + spatialUnitId + ": incomePerHouseholdMemberSum < 0";
			assert b.getIncomePerWeightedHouseholdMemberSum() >= 0 : "IndicatorsPerSpatialUnit.remove() - " + spatialUnitId + ": incomePerWeightedHouseholdMemberSum < 0";
			assert b.getHouseholdCount() >= 0 : "IndicatorsPerSpatialUnit.remove() - " + spatialUnitId + ": householdCount < 0";
			assert b.getPersonCount() >= 0 : "IndicatorsPerSpatialUnit.remove() - " + spatialUnitId + ": personCount < 0";
		}
	}

	@Override
	public List<? extends Fileable> getIndicatorList() {
		return indicatorList;
	}
}
