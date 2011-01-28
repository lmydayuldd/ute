/**
 * 
 */
package at.sume.dm.model.residential_mobility;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import at.sume.dm.Common;
import at.sume.dm.indicators.MoversIndicatorsPerSpatialUnit;
import at.sume.dm.model.output.Fileable;


/**
 * @author Alexander Remesch
 * TODO: better with Singleton?
 */
public class RentPerSpatialUnit {
	public static class RentPerSpatialUnitRow implements Comparable<RentPerSpatialUnitRow>, Fileable {
		private int spatialUnitId;
		private int yearlyRentPer100Sqm;
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
		/**
		 * @return the yearlyRentPerSqm
		 */
		public int getYearlyRentPer100Sqm() {
			return yearlyRentPer100Sqm;
		}
		/**
		 * @param yearlyRentPer100Sqm the yearlyRentPerSqm to set
		 */
		public void setYearlyRentPer100Sqm(int yearlyRentPer100Sqm) {
			this.yearlyRentPer100Sqm = yearlyRentPer100Sqm;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(RentPerSpatialUnitRow arg0) {
			return ((Integer)spatialUnitId).compareTo(arg0.spatialUnitId);
		}
		@Override
		public String toCsvHeadline(String delimiter) {
			return "SpatialUnitId" + delimiter + "YearlyRentPer100Sqm" + delimiter + "MonthlyRentPerSqm";
		}
		@Override
		public String toString(String delimiter) {
			double monthlyRentPerSqm = ((double) Math.round(yearlyRentPer100Sqm / 12)) / 100.0;
			return spatialUnitId + delimiter + yearlyRentPer100Sqm + delimiter + ((Double)monthlyRentPerSqm).toString();
		}
	}

	/**
	 * Comparator class for the yearly rent per m²
	 * @author Alexander Remesch
	 */
	public static class CompareYearlyRentPerSqm implements Comparator<RentPerSpatialUnitRow> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(RentPerSpatialUnitRow arg0, RentPerSpatialUnitRow arg1) {
			return ((Integer)arg0.getYearlyRentPer100Sqm()).compareTo(arg1.getYearlyRentPer100Sqm());
		}
	}
	
	private static ArrayList<RentPerSpatialUnitRow> rentPerSpatialUnit;
	private static int lowestYearlyRentPer100Sqm;
	private static int highestYearlyRentPer100Sqm;
	
	/**
	 * Set initial values from table WKO_Mietpreise
	 */
	static {
		try {
			String selectStatement = "";
			switch (Common.getSpatialUnitLevel()) {
			case ZB:
				selectStatement = "SELECT sgt.SpatialUnitId_ZB AS SpatialUnitId, Round(Avg([PreisJahr]) * 100, 0) AS YearlyRentPer100Sqm " +
					"FROM MA18_Stadtgebietstypen_Zählbezirke  AS sgt INNER JOIN WKO_Mietpreise AS wko ON sgt.SpatialUnitId_AD = wko.SpatialUnitId_AD " +
					"GROUP BY sgt.SpatialUnitId_ZB;";
				break;
			case SGT:
				selectStatement = "SELECT sgt.SpatialUnitId_SGT AS SpatialUnitId, Round(Avg([PreisJahr]) * 100, 0) AS YearlyRentPer100Sqm " +
					"FROM MA18_Stadtgebietstypen_Zählbezirke  AS sgt INNER JOIN WKO_Mietpreise AS wko ON sgt.SpatialUnitId_AD = wko.SpatialUnitId_AD " +
					"GROUP BY sgt.SpatialUnitId_SGT;";
				break;
			default:
				throw new AssertionError("Unknown spatial unit level (not ZB or SGT)");
			}
			rentPerSpatialUnit = Common.db.select(RentPerSpatialUnitRow.class, selectStatement);
			assert rentPerSpatialUnit.size() > 0 : "No rows selected from WKO_Mietpreise";
			assert rentPerSpatialUnit.get(0).yearlyRentPer100Sqm > 0 : "Rent per spatial unit = " + rentPerSpatialUnit.get(0).yearlyRentPer100Sqm;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Update rents per spatial unit from MoversIndicatorsPerSpatialUnit class
	 */
	public static void updateRentPerSpatialUnit() {
		for (RentPerSpatialUnitRow rpsu : rentPerSpatialUnit) {
			int avgYearlyRentPer100Sqm = MoversIndicatorsPerSpatialUnit.getAvgYearlyRentPer100Sqm(rpsu.getSpatialUnitId());
			assert avgYearlyRentPer100Sqm >= 0 : "Average yearly rent per 100m² < 0 (" + avgYearlyRentPer100Sqm + ")";
			if (avgYearlyRentPer100Sqm != 0) {
				rpsu.setYearlyRentPer100Sqm(avgYearlyRentPer100Sqm);
			}
		}
	}
	/**
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	public static int lookupSpatialUnitPos(int spatialUnitId) {
		RentPerSpatialUnitRow lookup = new RentPerSpatialUnitRow();
		lookup.setSpatialUnitId(spatialUnitId);
		return Collections.binarySearch(rentPerSpatialUnit, lookup);
	}
	/**
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	public static int getYearlyAverageRentPer100Sqm(int spatialUnitId) {
		int pos = lookupSpatialUnitPos(spatialUnitId);
		assert pos >= 0 : "Can't lookup a price for spatial unit id " + spatialUnitId;
		return rentPerSpatialUnit.get(pos).getYearlyRentPer100Sqm();
	}
	/**
	 * Return an array of all spatial units with a rent price level below the given yearly maximum
	 * cost of residence per m²
	 * @param maxCostOfResidence Yearly maximum cost of residence (rent) per m²
	 * @return
	 */
	public static ArrayList<Integer> getSpatialUnitsBelowGivenPrice(int maxCostOfResidence) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(RentPerSpatialUnitRow r : rentPerSpatialUnit) {
			int yearlyRentPerSqm = r.getYearlyRentPer100Sqm() / 100;
			if (yearlyRentPerSqm <= maxCostOfResidence)
				result.add(r.getSpatialUnitId());
		}
		return result;
	}
	/**
	 * Return the currently cheapest spatial units
	 * @param numUnits Number of spatial units to return
	 * @return
	 * TODO: speed this up by remembering the list?
	 */
	public static ArrayList<Integer> getCheapestSpatialUnits(int numUnits) {
		if (numUnits == 0)
			numUnits = rentPerSpatialUnit.size();
		ArrayList<Integer> result = new ArrayList<Integer>(numUnits);
		ArrayList<RentPerSpatialUnitRow> cheapestRents = (ArrayList<RentPerSpatialUnitRow>) rentPerSpatialUnit.clone();
		Collections.sort(cheapestRents,new CompareYearlyRentPerSqm());
		for (int i = 0; i != numUnits; i++) {
			result.add(cheapestRents.get(i).getSpatialUnitId());
		}
		lowestYearlyRentPer100Sqm = cheapestRents.get(0).getYearlyRentPer100Sqm();
		highestYearlyRentPer100Sqm = cheapestRents.get(numUnits - 1).getYearlyRentPer100Sqm();
		return result;
	}
	public static ArrayList<RentPerSpatialUnitRow> getRentPerSpatialUnit() {
		return rentPerSpatialUnit;
	}
	/***
	 * Get lowest yearly rent per 100 m² in the whole model area.
	 * Function getCheapestSpatialUnits() must be called first to determine the value returned by this function.
	 * @return
	 */
	public static int getLowestYearlyRentPer100Sqm() {
		assert lowestYearlyRentPer100Sqm > 0 : "Lowest rent <= 0. Probably getCheapestSpatialUnits() wasn't called to initialize this value.";
		return lowestYearlyRentPer100Sqm;
	}
	/**
	 * Get highest yearly rent per 100 m² in the whole model area
	 * Function getCheapestSpatialUnits() must be called first to determine the value returned by this function.
	 * @return
	 */
	public static int getHighestYearlyRentPer100Sqm() {
		assert highestYearlyRentPer100Sqm > 0 : "Highest rent <= 0. Probably getCheapestSpatialUnits() wasn't called to initialize this value.";
		return highestYearlyRentPer100Sqm;
	}
}
