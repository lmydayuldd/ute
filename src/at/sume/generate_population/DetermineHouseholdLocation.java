/**
 * 
 */
package at.sume.generate_population;

import java.sql.*;
import java.util.*;

/**
 * @author ar
 *
 */
public class DetermineHouseholdLocation {
	// TODO: pool both ArrayList-instances in one Distribution class
	private static ArrayList<Long> HouseholdNumberThreshold;
	private static ArrayList<Long> SpatialUnit;
	//private static HashMap<Long, Long> Distribution;
	private static long total_households;
	
	/**
	 * Load distribution of households per spatial unit from database
	 */
	public static void LoadDistribution()
	{
		int rowcount = 0;
		Database db = new Database(Common.GetDbLocation());
		ResultSet rs = db.executeQuery("select hh_gesamt, val(oestat) as gkz from [vz_2001_haushalte (zb)] where len(oestat) = 6 order by oestat");
//		try {
//			rs.last();
//			rowcount = rs.getRow();
//			rs.first();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// ResultSet in Array umwandeln
		//Distribution = new ArrayList<HouseholdsPerSpatialUnit>(rowcount);
		//Distribution = new HashMap<Long, Long>(rowcount);
		HouseholdNumberThreshold = new ArrayList<Long>(rowcount);
		SpatialUnit = new ArrayList<Long>(rowcount);
				
		total_households = 0;
		try {
			while (rs.next())
			{
//				HouseholdsPerSpatialUnit hhpsu = new HouseholdsPerSpatialUnit();
//				hhpsu.setSpatialUnitId(rs.getLong("gkz"));
				total_households += rs.getLong("hh_gesamt");
//				hhpsu.setNumberOfHouseholds(total_households);
//				Distribution.add(hhpsu);
//				Distribution.put(total_households, rs.getLong("gkz"));
				HouseholdNumberThreshold.add(total_households);
				SpatialUnit.add(rs.getLong("gkz"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Total = " + total_households);
	}

	/**
	 * Return household-location based on given distribution
	 * @return spatial unit id
	 */
	public static long Sample()
	{
		Random r = new Random();
		// generate random household number
		long random_household = (long) (r.nextDouble() * total_households) + 1;
		// lookup spatial unit
		int index = Collections.binarySearch(HouseholdNumberThreshold, random_household);
		return SpatialUnit.get(Math.abs(index));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Test
		LoadDistribution();
		System.out.println("Sample spatial unit = " + Sample());
		System.out.println("Sample spatial unit = " + Sample());
	}
}
