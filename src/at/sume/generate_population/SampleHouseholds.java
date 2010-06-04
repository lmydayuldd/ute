/**
 * 
 */
package at.sume.generate_population;

import java.sql.*;
import java.util.*;
import at.sume.distributions.HouseholdsPerSpatialUnit;

/**
 * @author ar
 *
 */
public class SampleHouseholds {
	private static ArrayList<HouseholdsPerSpatialUnit> spatialUnits;
	private static ArrayList<Long> HouseholdNumberThreshold;	// needed for Collections.binarySearch()!
	
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
		spatialUnits = new ArrayList<HouseholdsPerSpatialUnit>(rowcount);
		HouseholdNumberThreshold = new ArrayList<Long>(rowcount);
				
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
			spatialUnits.add(h);
			HouseholdNumberThreshold.add(HouseholdsPerSpatialUnit.getNrHouseholdsTotalSum());
		}
		System.out.println("Total = " + HouseholdsPerSpatialUnit.getNrHouseholdsTotalSum());
	}

	/**
	 * Return index of household-location based on given distribution
	 * @return index of list of spatial units with household-numbers
	 */
	public static int determineLocationIndex()
	{
		Random r = new Random();
		// generate random household number
		long random_household = (long) (r.nextDouble() * HouseholdsPerSpatialUnit.getNrHouseholdsTotalSum());
		// lookup spatial unit
		int index = Collections.binarySearch(HouseholdNumberThreshold, random_household);
		if (index < 0)
			index = (index + 1) * -1;
		if (index > spatialUnits.size())
			System.out.println("random_household = " + random_household + ", total_households = " + HouseholdsPerSpatialUnit.getNrHouseholdsTotalSum() + ", index = " + index);
		return index;
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
}
