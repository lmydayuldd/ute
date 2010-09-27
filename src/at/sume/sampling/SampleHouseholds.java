/**
 * 
 */
package at.sume.sampling;

import java.sql.*;
import java.util.*;

import net.remesch.util.Database;
import at.sume.sampling.distributions.HouseholdsPerSpatialUnit;

/**
 * Monte Carlo sampling for household locations and household sizes
 * 
 * @author Alexander Remesch
 *
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
		ResultSet rs = db.executeQuery("select val(oestat) as gkz, hh_gesamt, hh_p1, hh_p2, hh_p3, hh_p4, hh_p5, hh_p6, hh_einrichtungen" +
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
			h.setSpatialUnitId(rs.getLong("gkz"));
			// TODO: Was sind die Einrichtungen und was tun wir damit?
			h.setNrHouseholdsTotal(rs.getLong("hh_gesamt") - rs.getLong("hh_einrichtungen"));
			h.setNrHouseholds_1P(rs.getLong("hh_p1"));
			h.setNrHouseholds_2P(rs.getLong("hh_p2"));
			h.setNrHouseholds_3P(rs.getLong("hh_p3"));
			h.setNrHouseholds_4Pmore(rs.getLong("hh_p4") + rs.getLong("hh_p5") + rs.getLong("hh_p6"));
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
//		hh_threshold += hhpsu.getNrHouseholds_4Pmore();
//		if (random_household <= hh_threshold)
//			return 4;
//		System.out.println("Problem: random_household (" + random_household + ") > hh_threshold (" + hh_threshold + ") bei GKZ " + hhpsu.getSpatialUnitId());
//		return 0;
		return 4;
	}
	
	public static HouseholdsPerSpatialUnit GetSpatialUnitData(int index)
	{
		return spatialUnits.get(index);
	}
	
	public static long getNrHouseholdsTotalSum() {
		return spatialUnits.getMaxThreshold();
	}
}
