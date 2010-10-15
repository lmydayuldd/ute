/**
 * 
 */
package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.entities.HouseholdRow;

/**
 * @author Alexander Remesch
 *
 */
public class IndicatorsPerSpatialUnit implements Indicator {
	private static class BaseIndicators implements Comparable<BaseIndicators> {
		private long spatialUnitId;
		private long incomeSum;
		private long incomePerHouseholdMemberSum;
		private long incomePerWeightedHouseholdMemberSum;
		private long householdCount;
		private long personCount;
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
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(BaseIndicators arg0) {
			return ((Long)spatialUnitId).compareTo(arg0.getSpatialUnitId());
		}
	}
	private static ArrayList<BaseIndicators> indicatorList;
	
	public IndicatorsPerSpatialUnit() {
		indicatorList = new ArrayList<BaseIndicators>();
	}
	
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#build(at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public void add(HouseholdRow hh) {
		IndicatorsPerSpatialUnit.add(hh.getSpatialunitId(), hh.getHouseholdSize(), hh.getYearlyIncome(), hh.getYearlyIncomePerMember(), hh.getYearlyIncomePerMemberWeighted());
	}
	
	private static void add(long spatialUnitId, short memberCount, long income, long incomePerHouseholdMember, long incomePerWeightedHouseholdMember) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			lookup.setIncomeSum(income);
			lookup.setIncomePerHouseholdMemberSum(incomePerHouseholdMember);
			lookup.setIncomePerWeightedHouseholdMemberSum(incomePerWeightedHouseholdMember);
			lookup.setHouseholdCount(1);
			lookup.setPersonCount(memberCount);
			indicatorList.add(pos, lookup);
		} else {
			// available at position pos
			BaseIndicators b = indicatorList.get(pos);
			b.setIncomeSum(b.getIncomeSum() + income);
			b.setIncomePerHouseholdMemberSum(b.getIncomePerHouseholdMemberSum() + incomePerHouseholdMember);
			b.setIncomePerWeightedHouseholdMemberSum(b.getIncomePerWeightedHouseholdMemberSum() + incomePerWeightedHouseholdMember);
			b.setHouseholdCount(b.getHouseholdCount() + 1);
			b.setPersonCount(b.getPersonCount() + memberCount);
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
		IndicatorsPerSpatialUnit.indicatorList.clear();
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#remove(at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public void remove(HouseholdRow hh) {
		IndicatorsPerSpatialUnit.remove(hh.getSpatialunitId(), hh.getHouseholdSize(), hh.getYearlyIncome(), hh.getYearlyIncomePerMember(), hh.getYearlyIncomePerMemberWeighted());
	}

	private static void remove(long spatialUnitId, short memberCount, long income, long incomePerHouseholdMember, long incomePerWeightedHouseholdMember) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos < 0) {
			// not there, unable to remove - throw exception
			throw new AssertionError("IncomeIndicators.remove() - " + spatialUnitId + " is not in the list of spatial units");
		} else {
			// available at position pos - remove
			BaseIndicators b = indicatorList.get(pos);
			b.setIncomeSum(b.getIncomeSum() - income);
			b.setIncomePerHouseholdMemberSum(b.getIncomePerHouseholdMemberSum() - incomePerHouseholdMember);
			b.setIncomePerWeightedHouseholdMemberSum(b.getIncomePerWeightedHouseholdMemberSum() - incomePerWeightedHouseholdMember);
			b.setHouseholdCount(b.getHouseholdCount() - 1);
			b.setPersonCount(b.getPersonCount() - memberCount);
			indicatorList.set(pos, b);
			
			assert b.getIncomeSum() >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": incomeSum < 0";
			assert b.getIncomePerHouseholdMemberSum() >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": incomePerHouseholdMemberSum < 0";
			assert b.getIncomePerWeightedHouseholdMemberSum() >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": incomePerWeightedHouseholdMemberSum < 0";
			assert b.getHouseholdCount() >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": householdCount < 0";
			assert b.getPersonCount() >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": personCount < 0";
		}
	}
}
