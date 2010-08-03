package at.sume.generate_population;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.remesch.util.Database;
import net.remesch.util.DateUtil;
import at.sume.db_wrapper.*;
import at.sume.distributions.*;
import at.sume.dm.Common;
import at.sume.sampling.SampleHouseholds;
import at.sume.sampling.SamplePersons;

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
		// TODO: put in security message to avoid accidental deletion
		//db.execute("delete * from _DM_Households");
		//db.execute("delete * from _DM_Persons");
		
		long total_households = SampleHouseholds.getNrHouseholdsTotalSum();
		short hh_size;
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
			if ((i % 1000) == 0)
				System.out.println("Household i = " + i + " @ " + DateUtil.now());
		}
		SampleHouseholds.FreeDistribution();

		// Generate first persons for each household: iterate through all households
		// Runtime: ~3,5 hrs.
		ResultSet rs = db.executeQuery("SELECT HouseholdId, SpatialunitId, HouseholdSize FROM _DM_Households " +
										"ORDER BY SpatialunitId, HouseholdSize;");
		Person pers;
		try {
			pers = new Person(db);
		} catch (SQLException e) {
			System.err.println("Error in constructor Person()\n" + e);
			return;
		}
		long personId = 1;
		long spatialUnitId_prev = 0, spatialUnitId_curr = 0;
		short householdSize_prev = 0, householdSize_curr = 0;
		try {
			while (rs.next()) {
//			for (long j = 0; j != hh_size; j++) {
					// Person number
					pers.setPersonId(personId);
					// Household representative if first person
//				if (j == 0)
					pers.setHouseholdRepresentative(true);
//				else
//					pers.setHouseholdRepresentative(false);
					// Person sex & age: load distribution
					spatialUnitId_curr = rs.getLong("SpatialunitId");
					householdSize_curr = rs.getShort("HouseholdSize");
					// new spatial unit or household size -> different distribution necessary
					if ((spatialUnitId_curr != spatialUnitId_prev) || (householdSize_curr != householdSize_prev)) {
						try {
							SamplePersons.FreeDistribution();
							SamplePersons.LoadDistribution(db, (short) 1, householdSize_curr, spatialUnitId_curr);
							spatialUnitId_prev = spatialUnitId_curr;
							householdSize_prev = householdSize_curr;
						} catch (SQLException e) {
							System.err.println("Error in DetermineHouseholdLocation.LoadDistribution()\n" + e);
							return;
						}
					}
					// Person sex
					PersonsPerAgeSexHouseholdsizePersonnr pc = SamplePersons.GetPersonData(SamplePersons.determineLocationIndex());
					pers.setSex(pc.getSex());
					// Person age
					pers.setAgeGroupId(pc.getAgeGroupId());
					// Household-Id
					pers.setHouseholdId(rs.getLong("HouseholdId"));
					
					// TODO: Yearly income
					
					try {
						pers.dbInsert();
					} catch (SQLException e) {
						System.err.println("Error during db insert for PersonId = " + personId);
						e.printStackTrace();
						return;
//				}
					}
				// Sample dwelling of the current household
				// TODO: vacant dwellings must be sampled somewhere else
				if ((personId % 1000) == 0)
					System.out.println("Person personId = " + personId + " @ " + DateUtil.now());
				personId++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        System.out.println("End @ " + DateUtil.now());
	}

}
