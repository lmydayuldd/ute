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
public class IncomeIndicators implements Indicator {
	private static ArrayList<Long> spatialUnits;
	private static ArrayList<Long> incomeSum;
	private static ArrayList<Long> incomePerHouseholdMemberSum;
	private static ArrayList<Long> incomePerWeightedHouseholdMemberSum;
	private static ArrayList<Long> householdCount;
	private static ArrayList<Long> personCount;
	
	public IncomeIndicators() {
		spatialUnits = new ArrayList<Long>();
		incomeSum = new ArrayList<Long>();
		incomePerHouseholdMemberSum = new ArrayList<Long>();
		incomePerWeightedHouseholdMemberSum = new ArrayList<Long>();
		householdCount = new ArrayList<Long>();
		personCount = new ArrayList<Long>();
	}
	
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#build(at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public void add(HouseholdRow hh) {
		IncomeIndicators.add(hh.getSpatialunitId(), hh.getHouseholdSize(), hh.getYearlyIncome(), hh.getYearlyIncomePerMember(), hh.getYearlyIncomePerMemberWeighted());
	}
	
	private static void add(long spatialUnitId, short memberCount, long income, long incomePerHouseholdMember, long incomePerWeightedHouseholdMember) {
		int pos = Collections.binarySearch(spatialUnits, spatialUnitId);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			spatialUnits.add(pos, spatialUnitId);
			incomeSum.add(pos, income);
			incomePerHouseholdMemberSum.add(pos, incomePerHouseholdMember);
			incomePerWeightedHouseholdMemberSum.add(pos, incomePerWeightedHouseholdMember);
			householdCount.add(pos, (long)1);
			personCount.add(pos, (long)memberCount);
		} else {
			// available at position pos
			long newIncomeSum = incomeSum.get(pos) + income;
			incomeSum.set(pos, newIncomeSum);
			incomePerHouseholdMemberSum.set(pos, incomePerHouseholdMemberSum.get(pos) + incomePerHouseholdMember);
			incomePerWeightedHouseholdMemberSum.set(pos, incomePerWeightedHouseholdMemberSum.get(pos) + incomePerWeightedHouseholdMember);
			householdCount.set(pos, householdCount.get(pos) + 1);
			personCount.set(pos, personCount.get(pos) + memberCount);
		}
	}
	
	public static long getAvgHouseholdIncome(long spatialUnitId) {
		int pos = Collections.binarySearch(spatialUnits, spatialUnitId);
		if (pos >= 0) {
			return incomeSum.get(pos) / householdCount.get(pos);
		} else {
			return 0;
		}
	}
	
	public static long getAvgPersonIncome(long spatialUnitId) {
		int pos = Collections.binarySearch(spatialUnits, spatialUnitId);
		if (pos >= 0) {
			return incomeSum.get(pos) / personCount.get(pos);
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
		int pos = Collections.binarySearch(spatialUnits, spatialUnitId);
		if (pos >= 0) {
			return incomePerHouseholdMemberSum.get(pos) / householdCount.get(pos);
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
		int pos = Collections.binarySearch(spatialUnits, spatialUnitId);
		if (pos >= 0) {
			return incomePerWeightedHouseholdMemberSum.get(pos) / householdCount.get(pos);
		} else {
			return 0;
		}
	}
	
	@Override
	public void reset() {
		for (int i = 0; i != IncomeIndicators.spatialUnits.size(); i++) {
			IncomeIndicators.incomeSum.set(i, (long)0);
			IncomeIndicators.incomePerHouseholdMemberSum.set(i, (long)0);
			IncomeIndicators.incomePerWeightedHouseholdMemberSum.set(i, (long)0);
			IncomeIndicators.householdCount.set(i, (long)0);
			IncomeIndicators.personCount.set(i, (long)0);
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#remove(at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public void remove(HouseholdRow hh) {
		IncomeIndicators.remove(hh.getSpatialunitId(), hh.getHouseholdSize(), hh.getYearlyIncome(), hh.getYearlyIncomePerMember(), hh.getYearlyIncomePerMemberWeighted());
	}

	private static void remove(long spatialUnitId, short memberCount, long income, long incomePerHouseholdMember, long incomePerWeightedHouseholdMember) {
		int pos = Collections.binarySearch(spatialUnits, spatialUnitId);
		if (pos < 0) {
			// not there, unable to remove - throw exception
			throw new AssertionError("IncomeIndicators.remove() - " + spatialUnitId + " is not in the list of spatial units");
		} else {
			// available at position pos - remove
			long newIncomeSum = incomeSum.get(pos) - income;
			incomeSum.set(pos, newIncomeSum);
			incomePerHouseholdMemberSum.set(pos, incomePerHouseholdMemberSum.get(pos) - incomePerHouseholdMember);
			incomePerWeightedHouseholdMemberSum.set(pos, incomePerWeightedHouseholdMemberSum.get(pos) - incomePerWeightedHouseholdMember);
			householdCount.set(pos, householdCount.get(pos) - 1);
			personCount.set(pos, personCount.get(pos) - memberCount);
			
			assert newIncomeSum >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": incomeSum < 0";
			assert incomePerHouseholdMemberSum.get(pos) >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": incomePerHouseholdMemberSum < 0";
			assert incomePerWeightedHouseholdMemberSum.get(pos) >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": incomePerWeightedHouseholdMemberSum < 0";
			assert householdCount.get(pos) >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": householdCount < 0";
			assert personCount.get(pos) >= 0 : "IncomeIndicators.remove() - " + spatialUnitId + ": personCount < 0";
		}
	}
}
