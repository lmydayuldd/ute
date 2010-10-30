/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import at.sume.dm.Common;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.indicators.MoversIndicatorsPerSpatialUnit;

/**
 * Calculate the cost-effectiveness dimension of residential satisfaction based on the anticipated costs of a new dwelling with
 * the same characteristics (currently the living space only) in the target spatial unit.
 * 
 * The yearly average rent per spatial unit used here is derived from the table WKO_Mietpreise for the first
 * model year and later on the committed sales are used to determine the rents in each model year.
 * 
 * @author Alexander Remesch
 */
public class CostEffectiveness extends ResidentialSatisfactionComponent {
	//TODO: find common base class with HouseholdIndicatorsPerSpatialUnit!
	public static class RentPerSpatialUnit implements Comparable<RentPerSpatialUnit> {
		// must be public in order to be able to use Database.select()/java reflection api
		public long spatialUnitId;
		public long yearlyRentPerSqm;
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
		public long getYearlyRentPerSqm() {
			return yearlyRentPerSqm;
		}
		/**
		 * @param yearlyRentPerSqm the yearlyPricePerSqm to set
		 */
		public void setYearlyRentPerSqm(long yearlyRentPerSqm) {
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

	/**
	 * Comparator class for the yearly rent per m²
	 * @author Alexander Remesch
	 */
	class CompareYearlyRentPerSqm implements Comparator<RentPerSpatialUnit> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(RentPerSpatialUnit arg0, RentPerSpatialUnit arg1) {
			return ((Long)arg0.getYearlyRentPerSqm()).compareTo(arg1.getYearlyRentPerSqm());
		}
	}
	
	private ArrayList<RentPerSpatialUnit> rentPerSpatialUnit;
	
	/**
	 * Set initial values from table WKO_Mietpreise
	 */
	public CostEffectiveness() {
		try {
			String selectStatement = "SELECT sgt.SpatialUnitId_ZB AS SpatialUnitId, Round(Avg([Preis]) * 12,2) AS YearlyRentPerSqm " +
			"FROM MA18_Stadtgebietstypen_Zählbezirke  AS sgt INNER JOIN WKO_Mietpreise AS wko ON sgt.SpatialUnitId_AD = wko.SpatialUnitId_AD " +
			"GROUP BY sgt.SpatialUnitId_ZB;";
			rentPerSpatialUnit = Common.db.select(RentPerSpatialUnit.class, selectStatement);
			assert rentPerSpatialUnit.size() > 0 : "No rows selected from WKO_Mietpreise";
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
	public void updateRentPerSpatialUnit() {
		for (RentPerSpatialUnit rpsu : rentPerSpatialUnit) {
			rpsu.setYearlyRentPerSqm(MoversIndicatorsPerSpatialUnit.getAvgCostOfResidencePerSqm(rpsu.getSpatialUnitId()));
		}
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionComponent#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.DwellingRow, at.sume.dm.entities.SpatialUnitRow, int)
	 */
	@Override
	public long calc(HouseholdRow household, DwellingRow dwelling,
			SpatialUnitRow spatialUnit, int ModelYear) {
		// TODO: add household-specific rentPerceptionModifier here, that may also increase over the years if the household
		// is unable to find a new residence for a long time (?)
		long potentialCostOfResidence = Math.round(dwelling.getDwellingSize() * getYearlyAverageRent(spatialUnit.getSpatialUnitId()));
		long currentCostOfResidence = dwelling.getDwellingCosts();
		if (potentialCostOfResidence >= currentCostOfResidence)
			return 1000;
		else
			return (potentialCostOfResidence * 1000) / currentCostOfResidence;
	}
	/**
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	private int lookupSpatialUnitPos(long spatialUnitId) {
		RentPerSpatialUnit lookup = new RentPerSpatialUnit();
		lookup.setSpatialUnitId(spatialUnitId);
		return Collections.binarySearch(rentPerSpatialUnit, lookup);
	}
	/**
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	private long getYearlyAverageRent(long spatialUnitId) {
		int pos = lookupSpatialUnitPos(spatialUnitId);
		assert pos >= 0 : "Can't lookup a price for spatial unit id " + spatialUnitId;
		return rentPerSpatialUnit.get(pos).getYearlyRentPerSqm();
	}
	/**
	 * Return an array of all spatial units with a rent price level below the given yearly maximum
	 * cost of residence per m²
	 * @param maxCostOfResidence Yearly maximum cost of residence (rent) per m²
	 * @return
	 */
	public ArrayList<Long> getSpatialUnitsBelowGivenPrice(long maxCostOfResidence) {
		ArrayList<Long> result = new ArrayList<Long>();
		for(RentPerSpatialUnit r : rentPerSpatialUnit) {
			if (r.getYearlyRentPerSqm() <= maxCostOfResidence)
				result.add(r.getSpatialUnitId());
		}
		return result;
	}
}
