/**
 * 
 */
package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.indicators.base.Indicator;
import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.IncomeGroup;

/**
 * @author Alexander Remesch
 *
 */
public class AllHouseholdsIndicatorsPerHouseholdTypeAndIncome implements Indicator<HouseholdRow> {
	private static class BaseIndicators implements Comparable<BaseIndicators>, Fileable {
		private HouseholdType householdType;
		private byte incomeGroup;
		private long householdCount;
		private long personCount;
		private long livingSpaceSum;
		private long livingSpacePerHouseholdMemberSum;
		private long livingSpacePerWeightedHouseholdMemberSum;
		
		/**
		 * @return the householdType
		 */
		public HouseholdType getHouseholdType() {
			return householdType;
		}
		/**
		 * @param householdType the householdType to set
		 */
		public void setHouseholdType(HouseholdType householdType) {
			this.householdType = householdType;
		}
		/**
		 * @return the incomeGroup
		 */
		public byte getIncomeGroup() {
			return incomeGroup;
		}
		/**
		 * @param incomeGroup the incomeGroup to set
		 */
		public void setIncomeGroup(byte incomeGroup) {
			this.incomeGroup = incomeGroup;
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
		 * @return the livingSpaceSum
		 */
		public long getLivingSpaceSum() {
			return livingSpaceSum;
		}
		/**
		 * @param livingSpaceSum the livingSpaceSum to set
		 */
		public void setLivingSpaceSum(long livingSpaceSum) {
			this.livingSpaceSum = livingSpaceSum;
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
		/**
		 * @return the livingSpacePerWeightedHouseholdMemberSum
		 */
		public long getLivingSpacePerWeightedHouseholdMemberSum() {
			return livingSpacePerWeightedHouseholdMemberSum;
		}
		/**
		 * @param livingSpacePerWeightedHouseholdMemberSum the livingSpacePerWeightedHouseholdMemberSum to set
		 */
		public void setLivingSpacePerWeightedHouseholdMemberSum(
				long livingSpacePerWeightedHouseholdMemberSum) {
			this.livingSpacePerWeightedHouseholdMemberSum = livingSpacePerWeightedHouseholdMemberSum;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(BaseIndicators arg0) {
			int comp1 = householdType.compareTo(arg0.getHouseholdType());
			int comp2 = ((Byte)incomeGroup).compareTo(arg0.getIncomeGroup());
			if (comp1 != 0)
				return comp1;
			else
				return comp2;
		}
		@Override
		public String toCsvHeadline(String delimiter) {
			return "HouseholdType" + delimiter + "IncomeGroup" + delimiter + "HouseholdCount" + delimiter + "PersonCount" + delimiter + "LivingSpaceSum" + delimiter +
				"LivingSpacePerHouseholdMemberSum" + delimiter + "LivingSpacePerWeightedHouseholdMemberSum";
		}
		@Override
		public String toString(String delimiter) {
			return householdType.toString() + delimiter + IncomeGroup.getIncomeGroupNameDirect(incomeGroup) + delimiter + householdCount +
				delimiter + personCount + delimiter + livingSpaceSum + delimiter + livingSpacePerHouseholdMemberSum + delimiter +
				livingSpacePerWeightedHouseholdMemberSum;
		}
	}

	private static ArrayList<BaseIndicators> indicatorList;
	
	public AllHouseholdsIndicatorsPerHouseholdTypeAndIncome() {
		indicatorList = new ArrayList<BaseIndicators>();
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#add(at.sume.db.RecordSetRow)
	 */
	@Override
	public void add(HouseholdRow household) {
		HouseholdType householdType = household.getHouseholdType();
		byte incomeGroup = IncomeGroup.getIncomeGroupId(household.getYearlyIncome());
		int pos = lookupIndicator(householdType, incomeGroup);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			BaseIndicators b = new BaseIndicators();
			b.setHouseholdType(householdType);
			b.setIncomeGroup(incomeGroup);
			b.setHouseholdCount(1);
			b.setPersonCount(household.getMemberCount());
			b.setLivingSpaceSum(household.getLivingSpace());
			b.setLivingSpacePerHouseholdMemberSum(household.getLivingSpace() / household.getMemberCount());
			b.setLivingSpacePerWeightedHouseholdMemberSum((long)((double)household.getLivingSpace() / household.getWeightedMemberCount()));
			indicatorList.add(pos, b);
		} else {
			// available at position pos
			BaseIndicators b = indicatorList.get(pos);
			b.setHouseholdCount(b.getHouseholdCount() + 1);
			b.setPersonCount(b.getPersonCount() + household.getMemberCount());
			b.setLivingSpaceSum(b.getLivingSpaceSum() + household.getLivingSpace());
			b.setLivingSpacePerHouseholdMemberSum(b.getLivingSpacePerHouseholdMemberSum() + household.getLivingSpace() / household.getMemberCount());
			b.setLivingSpacePerWeightedHouseholdMemberSum(b.getLivingSpacePerWeightedHouseholdMemberSum() + (long)((double)household.getLivingSpace() / household.getWeightedMemberCount()));
			indicatorList.set(pos, b);
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#clear()
	 */
	@Override
	public void clear() {
		AllHouseholdsIndicatorsPerHouseholdTypeAndIncome.indicatorList.clear();
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#remove(at.sume.db.RecordSetRow)
	 */
	@Override
	public void remove(HouseholdRow household) {
		HouseholdType householdType = household.getHouseholdType();
		byte incomeGroup = IncomeGroup.getIncomeGroupId(household.getYearlyIncome());
		int pos = lookupIndicator(householdType, incomeGroup);
		if (pos < 0) {
			// not there, unable to remove - throw exception
			throw new AssertionError("IndicatorsPerHouseholdTypeAndIncome.remove() - incomeGroup " + incomeGroup + ", householdType " + householdType + " is not in the list of spatial units");
		} else {
			// available at position pos - remove
			BaseIndicators b = indicatorList.get(pos);
			b.setHouseholdCount(b.getHouseholdCount() - 1);
			b.setPersonCount(b.getPersonCount() - household.getMemberCount());
			b.setLivingSpaceSum(b.getLivingSpaceSum() - household.getLivingSpace());
			b.setLivingSpacePerHouseholdMemberSum(b.getLivingSpacePerHouseholdMemberSum() - household.getLivingSpace() / household.getMemberCount());
			b.setLivingSpacePerWeightedHouseholdMemberSum(b.getLivingSpacePerWeightedHouseholdMemberSum() - (long)((double)household.getLivingSpace() / household.getWeightedMemberCount()));
			indicatorList.set(pos, b);

			assert b.getLivingSpaceSum() >= 0 : "IndicatorsPerHouseholdTypeAndIncome.remove() - incomeGroup " + incomeGroup + ", householdType " + householdType + ": livingSpaceSum < 0";
			assert b.getLivingSpacePerHouseholdMemberSum() >= 0 : "IndicatorsPerHouseholdTypeAndIncome.remove() - incomeGroup " + incomeGroup + ", householdType " + householdType + ": livingSpacePerHouseholdMemberSum < 0";
			assert b.getLivingSpacePerWeightedHouseholdMemberSum() >= 0 : "IndicatorsPerHouseholdTypeAndIncome.remove() - incomeGroup " + incomeGroup + ", householdType " + householdType + ": livingSpacePerWeightedHouseholdMemberSum < 0";
			assert b.getHouseholdCount() >= 0 : "IndicatorsPerHouseholdTypeAndIncome.remove() - incomeGroup " + incomeGroup + ", householdType " + householdType + ": householdCount < 0";
			assert b.getPersonCount() >= 0 : "IndicatorsPerHouseholdTypeAndIncome.remove() - incomeGroup " + incomeGroup + ", householdType " + householdType + ": personCount < 0";
		}
	}

	private static int lookupIndicator(HouseholdType householdType, byte incomeGroup) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setHouseholdType(householdType);
		lookup.setIncomeGroup(incomeGroup);
		return Collections.binarySearch(indicatorList, lookup);
	}
	
	public static long getAvgLivingSpacePerHousehold(HouseholdType householdType, byte incomeGroup) {
		int pos = lookupIndicator(householdType, incomeGroup);
		if (pos >= 0) {
			BaseIndicators b = indicatorList.get(pos);
			long avgLivingSpace = b.getLivingSpaceSum() / b.getHouseholdCount();
			assert avgLivingSpace > 0 : "Avg. living space for household type " + householdType.toString() + ", income group " + incomeGroup + " is <= 0!";
			return avgLivingSpace;
		} else {
			throw new AssertionError("IndicatorsPerHouseholdTypeAndIncome.remove() - incomeGroup " + incomeGroup + ", householdType " + householdType + " is not in the list of spatial units");
		}
	}
	
	public ArrayList<BaseIndicators> getIndicatorList() {
		return indicatorList;
	}
}
