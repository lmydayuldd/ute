/**
 * 
 */
package at.sume.sampling;

import java.sql.*;
import java.util.*;

import net.remesch.db.Database;
import at.sume.sampling.distributions.HouseholdsPerSpatialUnit;

/**
 * Monte Carlo sampling for household locations and household sizes
 * Base table is vz_2001_haushalte (zb)
 * 
 * @author Alexander Remesch
 */
public class SampleHouseholds {
	private static Distribution<HouseholdsPerSpatialUnit> spatialUnits;
	
	/**
	 * Load distribution of households per spatial unit from database
	 * @throws SQLException 
	 */
	public static void LoadDistribution(Database db) throws SQLException
	{
		int rowcount = 0;
		ResultSet rs = db.executeQuery("select val(oestat) as gkz, hh_gesamt, hh_p1, hh_p2, hh_p3, hh_p4, hh_p5, hh_p6, personen_hh_p6, hh_einrichtungen" +
				" from [vz_2001_haushalte (zb)] where len(oestat) = 6 order by oestat");
//		try {
//			rs.last();
//			rowcount = rs.getRow();
//			rs.first();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// Transfer ResultSet to ArrayList
		spatialUnits = new Distribution<HouseholdsPerSpatialUnit>(rowcount);
				
		while (rs.next())
		{
			HouseholdsPerSpatialUnit h = new HouseholdsPerSpatialUnit();
			h.setSpatialUnitId(rs.getInt("gkz"));
			// TODO: Was sind die Einrichtungen und was tun wir damit?
			h.setNrHouseholdsTotal(rs.getInt("hh_gesamt") - rs.getInt("hh_einrichtungen"));
			h.setNrHouseholds_1P(rs.getInt("hh_p1"));
			h.setNrHouseholds_2P(rs.getInt("hh_p2"));
			h.setNrHouseholds_3P(rs.getInt("hh_p3"));
			h.setNrHouseholds_4P(rs.getInt("hh_p4"));
			h.setNrHouseholds_5P(rs.getInt("hh_p5"));
			h.setNrHouseholds_6Pplus(rs.getInt("hh_p6"));
			h.setNrPersons_P6plus(rs.getInt("personen_hh_p6"));
			spatialUnits.add(h.getNrHouseholdsTotal(), h);
		}
		rs.close();
	}

	public static void FreeDistribution() {
		if (spatialUnits != null)
			spatialUnits.clear();
	}
	
	/**
	 * Return index of household-location based on given distribution
	 * @return index of list of spatial units with household-numbers
	 */
	public static int determineLocationIndex()
	{
		return spatialUnits.randomSample();
	}
	
	/**
	 * Return household-size based on given distribution of household-sizes at the spatial unit determined by index
	 * @param index index of list of spatial units
	 * @return number of persons in the household
	 */
	public static short determineSize(int index)
	{
		Random r = new Random();
		HouseholdsPerSpatialUnit hhpsu = spatialUnits.get(index);
		long random_household = (long) (r.nextDouble() * hhpsu.getNrHouseholdsTotal());
		long hh_threshold = hhpsu.getNrHouseholds_1P();
		
		if (random_household <= hh_threshold)
			return 1;
		hh_threshold += hhpsu.getNrHouseholds_2P();
		if (random_household <= hh_threshold)
			return 2;
		hh_threshold += hhpsu.getNrHouseholds_3P();
		if (random_household <= hh_threshold)
			return 3;
		hh_threshold += hhpsu.getNrHouseholds_4P();
		if (random_household <= hh_threshold)
			return 4;
		hh_threshold += hhpsu.getNrHouseholds_5P();
		if (random_household <= hh_threshold)
			return 5;
//		hh_threshold += hhpsu.getNrHouseholds_6Pplus();
//		if (random_household <= hh_threshold)
//			return 6;
//		System.out.println("Problem: random_household (" + random_household + ") > hh_threshold (" + hh_threshold + ") bei GKZ " + hhpsu.getSpatialUnitId());
//		return 0;
		// TODO: implement larger than 6 person households here
		return 6;
	}
	
	public static HouseholdsPerSpatialUnit GetSpatialUnitData(int index)
	{
		return spatialUnits.get(index);
	}
	
	public static long getNrHouseholdsTotalSum() {
		return spatialUnits.getMaxThreshold();
	}
	
	public static Distribution<HouseholdsPerSpatialUnit> getDistribution() {
		return spatialUnits;
	}
}
