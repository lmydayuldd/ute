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


/**
 * @author Alexander Remesch
 *
 */
public class RentPerSpatialUnit {
	public static class RentPerSpatialUnitRow implements Comparable<RentPerSpatialUnitRow> {
		private long spatialUnitId;
		private long yearlyRentPer100Sqm;
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
		 * @return the yearlyRentPerSqm
		 */
		public long getYearlyRentPer100Sqm() {
			return yearlyRentPer100Sqm;
		}
		/**
		 * @param yearlyRentPer100Sqm the yearlyRentPerSqm to set
		 */
		public void setYearlyRentPer100Sqm(long yearlyRentPer100Sqm) {
			this.yearlyRentPer100Sqm = yearlyRentPer100Sqm;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(RentPerSpatialUnitRow arg0) {
			return ((Long)spatialUnitId).compareTo(arg0.spatialUnitId);
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
			return ((Long)arg0.getYearlyRentPer100Sqm()).compareTo(arg1.getYearlyRentPer100Sqm());
		}
	}
	
	private static ArrayList<RentPerSpatialUnitRow> rentPerSpatialUnit;
	
	/**
	 * Set initial values from table WKO_Mietpreise
	 */
	static {
		try {
			String selectStatement = "SELECT sgt.SpatialUnitId_ZB AS SpatialUnitId, Round(Avg([Preis]) * 12 * 100, 0) AS YearlyRentPer100Sqm " +
			"FROM MA18_Stadtgebietstypen_Zählbezirke  AS sgt INNER JOIN WKO_Mietpreise AS wko ON sgt.SpatialUnitId_AD = wko.SpatialUnitId_AD " +
			"GROUP BY sgt.SpatialUnitId_ZB;";
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
			rpsu.setYearlyRentPer100Sqm(MoversIndicatorsPerSpatialUnit.getAvgCostOfResidencePer100Sqm(rpsu.getSpatialUnitId()));
		}
	}
	/**
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	public static int lookupSpatialUnitPos(long spatialUnitId) {
		RentPerSpatialUnitRow lookup = new RentPerSpatialUnitRow();
		lookup.setSpatialUnitId(spatialUnitId);
		return Collections.binarySearch(rentPerSpatialUnit, lookup);
	}
	/**
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	public static long getYearlyAverageRentPer100Sqm(long spatialUnitId) {
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
	public static ArrayList<Long> getSpatialUnitsBelowGivenPrice(long maxCostOfResidence) {
		ArrayList<Long> result = new ArrayList<Long>();
		for(RentPerSpatialUnitRow r : rentPerSpatialUnit) {
			if (r.getYearlyRentPer100Sqm() / 100 <= maxCostOfResidence)
				result.add(r.getSpatialUnitId());
		}
		return result;
	}
}
