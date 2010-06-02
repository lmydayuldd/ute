/**
 * 
 */
package at.sume.generate_population;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import net.remesch.util.*;

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
	private static Database db;
	
	/**
	 * Load distribution of households per spatial unit from database
	 */
	public static void LoadDistribution()
	{
		int rowcount = 0;
		db = new Database(Common.GetDbLocation());
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
		long random_household = (long) (r.nextDouble() * total_households);
		// lookup spatial unit
		int index = Collections.binarySearch(HouseholdNumberThreshold, random_household);
		if (index < 0)
			index = (index + 1) * -1;
		if (index > SpatialUnit.size())
			System.out.println("random_household = " + random_household + ", total_households = " + total_households + ", index = " + index);
		//return SpatialUnit.get(Math.abs(index));
		return SpatialUnit.get(index);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Test
		// TODO: dzt. ca. 4 Std. zum Erzeugen der Haushalte und ODBC bringt Fehler. Gegenmaﬂnahmen:
		// 1) Access 2007 in der VM installieren (oder einen aktuellen ODBC-Treiber), ev. DB-Format auf accdb ‰ndern, jedenfalls einen besseren
		//    ODBC-Treiber verwenden
		// 2) Erzeugen im RAM (ArrayList oder noch besser ResultSet) und dann Bulk-‹bertragung in die Datenbank! 
        System.out.println("Start @ " + DateUtil.now());
		//Database db = new Database(Common.GetDbLocation());
		String sqlx;
		LoadDistribution();
		for (int i = 0; i != total_households; i++) {
			sqlx = "insert into _DM_Households (HouseholdId, SpatialunitId) values (" + (i + 1) + ", " + Sample() + ")";
			db.execute(sqlx);
			if ((i % 1000) == 0)
				System.out.println("i = " + i + " @ " + DateUtil.now());
		}
        System.out.println("Start @ " + DateUtil.now());
	}
}
