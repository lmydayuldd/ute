package at.sume.generate_population;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.remesch.util.DateUtil;
import at.sume.distributions.*;

public class GeneratePopulation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        System.out.println("Start @ " + DateUtil.now());
		Database db = new Database(Common.GetDbLocation());
		String sqlx = "insert into _DM_Households (HouseholdId, SpatialunitId, HouseholdSize) values (?, ?, ?)";
		PreparedStatement ps = null;
		HouseholdsPerSpatialUnit hhpsu;
		
		try {
			SampleHouseholds.LoadDistribution(db);
		} catch (SQLException e) {
			System.err.println("Error in DetermineHouseholdLocation.LoadDistribution()\n" + e);
			return;
		}
		db.execute("delete * from _DM_Households");
		
		// Use prepared statement for bulk inserts
		try {
			ps = db.con.prepareStatement(sqlx);
		} catch (SQLException e) {
			System.err.println("Error in prepareStatement(" + sqlx + ")\n" + e);
			return;
		}
		
		long total_households = HouseholdsPerSpatialUnit.getNrHouseholdsTotalSum();
		for (int i = 0; i != total_households; i++) {
			//sqlx = "insert into _DM_Households (HouseholdId, SpatialunitId) values (" + (i + 1) + ", " + Sample() + ")";
			//db.execute(sqlx);
			try {
				// Household number
				ps.setString(1, Long.toString(i + 1));
				// Household spatial unit
				int index = SampleHouseholds.determineLocationIndex();
				hhpsu = SampleHouseholds.GetSpatialUnitData(index);
				ps.setString(2, Long.toString(hhpsu.getSpatialUnitId()));
				// Household size
				ps.setString(3, Long.toString(SampleHouseholds.determineSize(index)));
			} catch (SQLException e) {
				System.err.println("Error in setLong(" + sqlx + ")\n" + e);
				return;
			}
			try {
				ps.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Error in executeUpdate(" + sqlx + ")\n" + e);
				return;
			}
			if ((i % 1000) == 0)
				System.out.println("i = " + i + " @ " + DateUtil.now());
		}
        System.out.println("Start @ " + DateUtil.now());
	}

}
