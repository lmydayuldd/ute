package at.sume.generate_population;

import java.sql.SQLException;

import net.remesch.util.DateUtil;
import at.sume.db_wrapper.Household;
import at.sume.distributions.*;

/**
 * Entry point class for generation of synthetic population
 * 
 * @author Alexander Remesch
 *
 */
public class GeneratePopulation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        System.out.println("Start @ " + DateUtil.now());
		Database db = new Database(Common.GetDbLocation());
		HouseholdsPerSpatialUnit hhpsu;
		Household hh;

		try {
			hh = new Household(db);
		} catch (SQLException e) {
			System.err.println("Error in constructor Household()\n" + e);
			return;
		}

		try {
			SampleHouseholds.LoadDistribution(db);
		} catch (SQLException e) {
			System.err.println("Error in DetermineHouseholdLocation.LoadDistribution()\n" + e);
			return;
		}
		// TODO: put into a table-class, method truncate
		db.execute("delete * from _DM_Households");
		
		long total_households = HouseholdsPerSpatialUnit.getNrHouseholdsTotalSum();
		for (long i = 0; i != total_households; i++) {
			try {
				// Sample households
				// Household number
				hh.setHouseholdId(i + 1);
				// Household spatial unit
				int index = SampleHouseholds.determineLocationIndex();
				hhpsu = SampleHouseholds.GetSpatialUnitData(index);
				hh.setSpatialunitId(hhpsu.getSpatialUnitId());
				// Household size
				hh.setHouseholdSize(SampleHouseholds.determineSize(index));
				
				// Sample persons
				// TODO: Household representative if first person
				
				// TODO: Person sex

				// TODO: Person age
				
				// TODO: Yearly income
				
			} catch (SQLException e) {
				System.err.println("Error while setting db fields for HouseholdId = " + i + "\n" + e);
				return;
			}
			try {
				hh.dbInsert();
			} catch (SQLException e) {
				System.err.println("Error during db insert for HouseholdId = " + i + "\n" + e);
				return;
			}
			if ((i % 1000) == 0)
				System.out.println("i = " + i + " @ " + DateUtil.now());
		}
        System.out.println("Start @ " + DateUtil.now());
	}

}
