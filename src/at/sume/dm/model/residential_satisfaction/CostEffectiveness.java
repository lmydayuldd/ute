/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;

/**
 * Calculate the cost-effectiveness dimension of residential satisfaction based on the anticipated costs of a new dwelling with
 * the same characteristics (currently the living space only) in the target spatial unit.
 *  
 * TODO: currently only the initial cost of residence per spatial unit is implemented - we need an indicator per spatial unit
 * that can be modified with the actual moves to generate price levels.
 *  
 * @author Alexander Remesch
 *
 */
public class CostEffectiveness extends ResidentialSatisfactionComponent {
	//TODO: find common base class with HouseholdIndicatorsPerSpatialUnit!
	public static class RentPerSpatialUnit implements Comparable<RentPerSpatialUnit> {
		// must be public in order to be able to use Database.select()/java reflection api
		public long spatialUnitId;
		public double yearlyRentPerSqm;
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

	/* (non-Javadoc)
	 * @see at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionComponent#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.SpatialUnitRow, int)
	 */
	@Override
	public double calc(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		// TODO: add household-specific rentPerceptionModifier here, that may also increase over the years if the household
		// is unable to find a new residence for a long time (?)
		double conceivableCostOfResidence = Math.round(hh.getLivingSpace() * getYearlyAverageRent(su.getSpatialUnitId()));
		double currentCostOfResidence = hh.getCostOfResidence();
		if (conceivableCostOfResidence >= currentCostOfResidence)
			return 1;
		else
			return conceivableCostOfResidence / currentCostOfResidence;
	}

	private int lookupSpatialUnitPos(long spatialUnitId) {
		RentPerSpatialUnit lookup = new RentPerSpatialUnit();
		lookup.setSpatialUnitId(spatialUnitId);
		return Collections.binarySearch(rentPerSpatialUnit, lookup);
	}
	
	private double getYearlyAverageRent(long spatialUnitId) {
		int pos = lookupSpatialUnitPos(spatialUnitId);
		assert pos >= 0 : "Can't lookup a price for spatial unit id " + spatialUnitId;
		return rentPerSpatialUnit.get(pos).getYearlyRentPerSqm();
	}
}
