/**
 * 
 */
package net.remesch.obsolete;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.Common;

/**
 * @author Alexander Remesch
 *
 */
public class ResidentialObjectRent {
	//TODO: find common base class with HouseholdIndicatorsPerSpatialUnit!
	private class RentPerSpatialUnit implements Comparable<RentPerSpatialUnit> {
		private long spatialUnitId;
		private double yearlyRentPerSqm;
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
		 * @return the yearlyPricePerSqm
		 */
		public double getYearlyRentPerSqm() {
			return yearlyRentPerSqm;
		}
		/**
		 * @param yearlyRentPerSqm the yearlyPricePerSqm to set
		 */
		public void setYearlyRentPerSqm(double yearlyRentPerSqm) {
			this.yearlyRentPerSqm = yearlyRentPerSqm;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(RentPerSpatialUnit arg0) {
			return ((Long)spatialUnitId).compareTo(arg0.getSpatialUnitId());
		}
	}
	
	private ArrayList<RentPerSpatialUnit> rentPerSpatialUnit;
	
	public ResidentialObjectRent() throws SQLException, InstantiationException, IllegalAccessException {
		rentPerSpatialUnit = new ArrayList<RentPerSpatialUnit>();
		String selectStatement = "SELECT sgt.SpatialUnitId_ZB AS SpatialUnitId, Round(Avg([Preis]) * 12,2) AS YearlyRentPerSqm " +
		"FROM MA18_Stadtgebietstypen_Zählbezirke  AS sgt INNER JOIN WKO_Mietpreise AS wko ON sgt.SpatialUnitId_AD = wko.SpatialUnitId_AD " +
		"GROUP BY sgt.SpatialUnitId_ZB;";
		rentPerSpatialUnit = Common.db.select(RentPerSpatialUnit.class, selectStatement);
		assert rentPerSpatialUnit.size() > 0 : "No rows selected from WKO_Mietpreise";
	}
	
	private int lookupSpatialUnitPos(long spatialUnitId) {
		RentPerSpatialUnit lookup = new RentPerSpatialUnit();
		lookup.setSpatialUnitId(spatialUnitId);
		return Collections.binarySearch(rentPerSpatialUnit, lookup);
	}
	
	public double getYearlyRent(long spatialUnitId) {
		int pos = lookupSpatialUnitPos(spatialUnitId);
		assert pos >= 0 : "Can't lookup a price for spatial unit id " + spatialUnitId;
		return rentPerSpatialUnit.get(pos).getYearlyRentPerSqm();
	}
	
	public void setYearlyRent(long spatialUnitId, double yearlyRentPerSqm) {
		int pos = lookupSpatialUnitPos(spatialUnitId);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			RentPerSpatialUnit r = new RentPerSpatialUnit();
			r.setSpatialUnitId(spatialUnitId);
			r.setYearlyRentPerSqm(yearlyRentPerSqm);
			rentPerSpatialUnit.add(pos, r);
		} else {
			// change at position pos
			RentPerSpatialUnit r = rentPerSpatialUnit.get(pos);
			r.setYearlyRentPerSqm(yearlyRentPerSqm);
			rentPerSpatialUnit.set(pos, r);
		}
	}
}
