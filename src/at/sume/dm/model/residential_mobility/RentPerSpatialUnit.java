/**
 * 
 */
package at.sume.dm.model.residential_mobility;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import net.remesch.db.schema.Ignore;
import at.sume.dm.Common;
import at.sume.dm.entities.SpatialUnitLevel;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.indicators.MoversIndicatorsPerSpatialUnit;
import at.sume.dm.model.output.Fileable;


/**
 * @author Alexander Remesch
 */
public class RentPerSpatialUnit {
	public static class RentPerSpatialUnitRow implements Comparable<RentPerSpatialUnitRow>, Fileable {
		private int spatialUnitId;
		private int yearlyRentPer100Sqm;
		@Ignore
		private int numSamples;
		@Ignore
		private short sampleYear;
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
		 * @return the yearlyRentPer100Sqm
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
		/**
		 * @return the numSamples
		 */
		public int getNumSamples() {
			return numSamples;
		}
		/**
		 * @param numSamples the numSamples to set
		 */
		public void setNumSamples(int numSamples) {
			this.numSamples = numSamples;
		}
		/**
		 * @return the sampleYear
		 */
		public short getSampleYear() {
			return sampleYear;
		}
		/**
		 * @param sampleYear the sampleYear to set
		 */
		public void setSampleYear(short sampleYear) {
			this.sampleYear = sampleYear;
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
			return "ModelRun" + delimiter + "SpatialUnitId" + delimiter + "YearlyRentPer100Sqm" + delimiter + "MonthlyRentPerSqm" + delimiter + "NumSamples" + delimiter + "SampleYear";
		}
		@Override
		public String toString(int modelRun, String delimiter) {
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
			DecimalFormat df = (DecimalFormat)nf;
			double monthlyRentPerSqm = ((double) Math.round(yearlyRentPer100Sqm / 12)) / 100.0;
			String output = df.format(monthlyRentPerSqm);
			return modelRun + delimiter + spatialUnitId + delimiter + yearlyRentPer100Sqm + delimiter + output + delimiter + numSamples + delimiter + sampleYear;
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
	
	private volatile static RentPerSpatialUnit uniqueInstance;
	private static String scenarioName;
	private static SpatialUnitLevel spatialUnitLevel;
	
	private ArrayList<RentPerSpatialUnitRow> rentPerSpatialUnit;
	private int lowestYearlyRentPer100Sqm;
	private int highestYearlyRentPer100Sqm;
	private RentAdjustments rentAdjustments;
	
	/**
	 * Set initial values from table WKO_Mietpreise
	 */
	private RentPerSpatialUnit(String scenarioName, SpatialUnitLevel spatialUnitLevel) {
		try {
			String selectStatement = "";
			switch (spatialUnitLevel) {
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
			rentAdjustments = new RentAdjustments(scenarioName);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static RentPerSpatialUnit getInstance(String scenarioName, SpatialUnitLevel spatialUnitLevel) {
		if (RentPerSpatialUnit.scenarioName == null) {
			RentPerSpatialUnit.scenarioName = scenarioName;
		} else {
			if (!RentPerSpatialUnit.scenarioName.equals(scenarioName)) {
				throw new AssertionError("Can't change scenarioName from '" + RentPerSpatialUnit.scenarioName + "' to '" + scenarioName + "'");
			}
		}
		if (RentPerSpatialUnit.spatialUnitLevel == null) {
			RentPerSpatialUnit.spatialUnitLevel = spatialUnitLevel;
		} else {
			if (!RentPerSpatialUnit.spatialUnitLevel.equals(spatialUnitLevel)) {
				throw new AssertionError("Can't change spatial unit level");
			}
		}
		return getInstance();
	}
	public static RentPerSpatialUnit getInstance() {
		if (RentPerSpatialUnit.scenarioName == null) {
			throw new AssertionError("scenarioName must be set in first call to getInstance()");
		}
		if (RentPerSpatialUnit.spatialUnitLevel == null) {
			throw new AssertionError("spatialUnitLevel must be set in first call to getInstance()");
		}
		if (uniqueInstance == null) {
			synchronized (RentPerSpatialUnit.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new RentPerSpatialUnit(scenarioName, spatialUnitLevel);
				}
			}
		}
		return uniqueInstance;
	}
	/**
	 * Update rents per spatial unit from MoversIndicatorsPerSpatialUnit class
	 */
	public void updateRentPerSpatialUnit(SpatialUnits spatialUnits, int modelYear) {
		for (RentPerSpatialUnitRow rpsu : rentPerSpatialUnit) {
			// TODO: don't calculate new rents for the surroundings of Vienna, since rent-calculation (MoversIndicatorsPerSpatialUnit) depends on
			// dwellings currently - we need to change this to be able to collect rent prices for Vienna surroundings
			if (!spatialUnits.lookup(rpsu.getSpatialUnitId()).isFreeDwellingsAlwaysAvailable()) {
				double rentAdjustment = rentAdjustments.getRentAdjustmentFactor(rpsu.getSpatialUnitId(), (short)modelYear);
				int avgYearlyRentPer100Sqm = (int)Math.round(rentAdjustment * MoversIndicatorsPerSpatialUnit.getAvgYearlyRentPer100Sqm(rpsu.getSpatialUnitId()));
//				assert avgYearlyRentPer100Sqm > 0 : "Average yearly rent per 100m² <= 0 (" + avgYearlyRentPer100Sqm + ")";
//				rpsu.setYearlyRentPer100Sqm(avgYearlyRentPer100Sqm);
				assert avgYearlyRentPer100Sqm >= 0 : "Average yearly rent per 100m² < 0 (" + avgYearlyRentPer100Sqm + ")";
				if (avgYearlyRentPer100Sqm != 0) {
					rpsu.setYearlyRentPer100Sqm(avgYearlyRentPer100Sqm);
					rpsu.setSampleYear((short) modelYear);
					rpsu.setNumSamples(MoversIndicatorsPerSpatialUnit.getHouseholdCount(rpsu.getSpatialUnitId()));
				}
			}
		}
	}
	/**
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	public int lookupSpatialUnitPos(int spatialUnitId) {
		RentPerSpatialUnitRow lookup = new RentPerSpatialUnitRow();
		lookup.setSpatialUnitId(spatialUnitId);
		return Collections.binarySearch(rentPerSpatialUnit, lookup);
	}
	/**
	 * 
	 * @param spatialUnitId
	 * @return
	 */
	public int getYearlyAverageRentPer100Sqm(int spatialUnitId) {
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
	public ArrayList<Integer> getSpatialUnitsBelowGivenPrice(int maxCostOfResidence) {
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
	public ArrayList<Integer> getCheapestSpatialUnits(int numUnits) {
		if (numUnits == 0)
			numUnits = rentPerSpatialUnit.size();
		ArrayList<Integer> result = new ArrayList<Integer>(numUnits);
		@SuppressWarnings("unchecked")
		ArrayList<RentPerSpatialUnitRow> cheapestRents = (ArrayList<RentPerSpatialUnitRow>) rentPerSpatialUnit.clone();
		Collections.sort(cheapestRents,new CompareYearlyRentPerSqm());
		for (int i = 0; i != numUnits; i++) {
			result.add(cheapestRents.get(i).getSpatialUnitId());
		}
		lowestYearlyRentPer100Sqm = cheapestRents.get(0).getYearlyRentPer100Sqm();
		highestYearlyRentPer100Sqm = cheapestRents.get(numUnits - 1).getYearlyRentPer100Sqm();
		return result;
	}
	public ArrayList<RentPerSpatialUnitRow> getRentPerSpatialUnit() {
		return rentPerSpatialUnit;
	}
	/***
	 * Get lowest yearly rent per 100 m² in the whole model area.
	 * Function getCheapestSpatialUnits() must be called first to determine the value returned by this function.
	 * @return
	 */
	public int getLowestYearlyRentPer100Sqm() {
		assert lowestYearlyRentPer100Sqm > 0 : "Lowest rent <= 0. Probably getCheapestSpatialUnits() wasn't called to initialize this value.";
		return lowestYearlyRentPer100Sqm;
	}
	/**
	 * Get highest yearly rent per 100 m² in the whole model area
	 * Function getCheapestSpatialUnits() must be called first to determine the value returned by this function.
	 * @return
	 */
	public int getHighestYearlyRentPer100Sqm() {
		assert highestYearlyRentPer100Sqm > 0 : "Highest rent <= 0. Probably getCheapestSpatialUnits() wasn't called to initialize this value.";
		return highestYearlyRentPer100Sqm;
	}
}
