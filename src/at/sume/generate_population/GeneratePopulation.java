package at.sume.generate_population;

import java.sql.SQLException;

import net.remesch.util.DateUtil;
import at.sume.db_wrapper.*;
import at.sume.distributions.*;
import at.sume.sampling.SampleHouseholds;

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
		Person pers;

		try {
			hh = new Household(db);
		} catch (SQLException e) {
			System.err.println("Error in constructor Household()\n" + e);
			return;
		}

		try {
			pers = new Person(db);
		} catch (SQLException e) {
			System.err.println("Error in constructor Person()\n" + e);
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
		
		long total_households = SampleHouseholds.getNrHouseholdsTotalSum();
		short hh_size;
		long personId = 1;
		// Sample households including persons
		for (long i = 0; i != total_households; i++) {
			try {
				// Household number
				hh.setHouseholdId(i + 1);
				// Household spatial unit
				int index = SampleHouseholds.determineLocationIndex();
				hhpsu = SampleHouseholds.GetSpatialUnitData(index);
				hh.setSpatialunitId(hhpsu.getSpatialUnitId());
				// Household size
				hh_size = SampleHouseholds.determineSize(index);
				hh.setHouseholdSize(hh_size);
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
			// Sample persons for the current household
			for (long j = 0; j != hh_size; j++) {
				// Person number
				pers.setPersonId(personId++);
				// Household representative if first person
				if (j == 0)
					pers.setHouseholdRepresentative(true);
				else
					pers.setHouseholdRepresentative(false);
					
				// TODO: Person sex

				// TODO: Person age
				
				// TODO: Yearly income
				
				try {
					pers.dbInsert();
				} catch (SQLException e) {
					System.err.println("Error during db insert for PersonId = " + j + "\n" + e);
					return;
				}
			}
			// Sample dwelling of the current household
			// TODO: vacant dwellings must be sampled somewhere else
			
			if ((i % 1000) == 0)
				System.out.println("i = " + i + " @ " + DateUtil.now());
		}
        System.out.println("Start @ " + DateUtil.now());
	}

}
