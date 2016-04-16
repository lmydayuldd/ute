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
public class MoversIndicatorsPerSpatialUnit implements Indicator<HouseholdRow> {
	private static class BaseIndicators implements Comparable<BaseIndicators>, Fileable {
		private int spatialUnitId;
		private int householdCount;
		private int personCount;
		private long yearlyRentSum;
		private long yearlyRentPer100SqmSum;
		
		/**
		 * @return the spatialUnitId
		 */
		public int getSpatialUnitId() {
			return spatialUnitId;
		}
		/**
		 * @param spatialUnitId the spatialUnitId to set
		 */
		public void setSpatialUnitId(int spatialUnitId) {
			this.spatialUnitId = spatialUnitId;
		}
		public int getHouseholdCount() {
			return householdCount;
		}
		/**
		 * @param householdCount the householdCount to set
		 */
		public void setHouseholdCount(int householdCount) {
			this.householdCount = householdCount;
		}
		/**
		 * @return the personCount
		 */
		public int getPersonCount() {
			return personCount;
		}
		/**
		 * @param personCount the personCount to set
		 */
		public void setPersonCount(int personCount) {
			this.personCount = personCount;
		}
		/**
		 * @return the costOfResidenceSum
		 */
		public long getYearlyRentSum() {
			return yearlyRentSum;
		}
		/**
		 * @param costOfResidenceSum the costOfResidenceSum to set
		 */
		public void setYearlyRentSum(long yearlyRentSum) {
			this.yearlyRentSum = yearlyRentSum;
		}
		/**
		 * @return the costOfResidencePerSqmSum
		 */
		public long getYearlyRentPer100SqmSum() {
			return yearlyRentPer100SqmSum;
		}
		/**
		 * @param costOfResidencePerSqmSum the costOfResidencePerSqmSum to set
		 */
		public void setYearlyRentPer100SqmSum(long yearlyRentPer100SqmSum) {
			this.yearlyRentPer100SqmSum = yearlyRentPer100SqmSum;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(BaseIndicators arg0) {
			return ((Integer)spatialUnitId).compareTo(arg0.getSpatialUnitId());
		}
		@Override
		public String toCsvHeadline(String delimiter) {
			// TODO Auto-generated method stub
			throw new AssertionError("not yet implemented");
		}
		@Override
		public String toString(int modelRun, String delimiter) {
			// TODO Auto-generated method stub
			throw new AssertionError("not yet implemented");
		}
	}

	private static ArrayList<BaseIndicators> indicatorList = new ArrayList<BaseIndicators>();;

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#add(at.sume.db.RecordSetRow)
	 */
	@Override
	public void add(HouseholdRow hh) {
		assert hh.getCostOfResidence() > 0 : "Household cost of residence <= 0";
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(hh.getSpatialunitId());
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			BaseIndicators b = new BaseIndicators();
			b.setSpatialUnitId(hh.getSpatialunitId());
			b.setHouseholdCount(1);
			b.setPersonCount(hh.getMemberCount());
			b.setYearlyRentSum(hh.getCostOfResidence());
			b.setYearlyRentPer100SqmSum((hh.getCostOfResidence() * 100) / hh.getLivingSpace());
			indicatorList.add(pos, b);
		} else {
			// available at position pos
			BaseIndicators b = indicatorList.get(pos);
			b.setHouseholdCount(b.getHouseholdCount() + 1);
			b.setPersonCount(b.getPersonCount() + hh.getMemberCount());
			b.setYearlyRentSum(b.getYearlyRentSum() + hh.getCostOfResidence());
			b.setYearlyRentPer100SqmSum(b.getYearlyRentPer100SqmSum() + (hh.getCostOfResidence() * 100) / hh.getLivingSpace());
			indicatorList.set(pos, b);
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#remove(at.sume.db.RecordSetRow)
	 */
	@Override
	public void remove(HouseholdRow hh) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(hh.getSpatialunitId());
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos < 0) {
			// not there, unable to remove - throw exception
			throw new AssertionError("MoversIndicatorsPerSpatialUnit.remove() - " + hh.getSpatialunitId() + " is not in the list of spatial units");
		} else {
			// available at position pos - remove
			BaseIndicators b = indicatorList.get(pos);
			b.setHouseholdCount(b.getHouseholdCount() - 1);
			b.setPersonCount(b.getPersonCount() - hh.getMemberCount());
			b.setYearlyRentSum(b.getYearlyRentSum() - hh.getCostOfResidence());
			b.setYearlyRentPer100SqmSum(b.getYearlyRentPer100SqmSum() - (hh.getCostOfResidence() * 100) / hh.getLivingSpace());
			indicatorList.set(pos, b);
			
			assert b.getHouseholdCount() >= 0 : "MoversIndicatorsPerSpatialUnit.remove() - " + hh.getSpatialunitId() + ": householdCount < 0";
			assert b.getPersonCount() >= 0 : "MoversIndicatorsPerSpatialUnit.remove() - " + hh.getSpatialunitId() + ": personCount < 0";
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator#clear()
	 */
	@Override
	public void clear() {
		indicatorList.clear();
	}

	/**
	 * Return the average cost of residence for all moving households (thus the avg. price a household
	 * pays for its new dwelling) in a given spatial unit
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	public static long getAvgYearlyTotalRent(int spatialUnitId) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos >= 0) {
			BaseIndicators b = indicatorList.get(pos);
			return b.getYearlyRentSum() / b.getHouseholdCount();
		} else {
			return 0;
		}
	}

	/**
	 * Return the average cost of residence per m² for all moving households (thus the avg. m²-price a household
	 * pays for its new dwelling) in a given spatial unit
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	public static int getAvgYearlyRentPer100Sqm(int spatialUnitId) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos >= 0) {
			BaseIndicators b = indicatorList.get(pos);
			return (int) (b.getYearlyRentPer100SqmSum() / b.getHouseholdCount());
		} else {
//			throw new AssertionError("SpatialUnit " + spatialUnitId + " not found");
			return 0;
		}
	}

	public static int getHouseholdCount(int spatialUnitId) {
		BaseIndicators lookup = new BaseIndicators();
		lookup.setSpatialUnitId(spatialUnitId);
		int pos = Collections.binarySearch(indicatorList, lookup);
		if (pos >= 0) {
			BaseIndicators b = indicatorList.get(pos);
			return b.getHouseholdCount();
		} else {
//			throw new AssertionError("SpatialUnit " + spatialUnitId + " not found");
			return 0;
		}
	}
	
	@Override
	public List<? extends Fileable> getIndicatorList() {
		return indicatorList;
	}
}
