/**
 * 
 */
package at.sume.dm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.remesch.util.Database;
import net.remesch.util.DateUtil;
import at.sume.db.RecordSetRow;
import at.sume.dm.demography.events.EventManager;
import at.sume.dm.demography.events.PersonDeath;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.entities.SpatialUnits;

/**
 * @author Alexander Remesch
 *
 */
public class Main {
	//ArrayList<Household> householdList;
	//ArrayList<Person> personList;
	private static SpatialUnits spatialUnits;
	private static Households households;
	private static Persons persons;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        System.out.println(DateUtil.now() + ": start");
		Database db = new Database(Common.GetDbLocation());

		// Load entity sets from database
		try {
			spatialUnits = new SpatialUnits(db);
	        System.out.println(DateUtil.now() + ": loaded spatial units");
			households = new Households(db);
	        System.out.println(DateUtil.now() + ": loaded households");
	        persons = new Persons(db);
	        System.out.println(DateUtil.now() + ": loaded persons");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Link households to spatial units
		households.linkSpatialUnits(spatialUnits);
        System.out.println(DateUtil.now() + ": linked households + spatial units");
        // Inter-link persons and households
        persons.linkHouseholds(households);
        System.out.println(DateUtil.now() + ": linked households + persons");
		
		// Model main loop
		// - Biographical events for all persons/households
		// - Find unsatisfied households
		// - Simulate moves of unsatisfied households
        try {
			runModel(db, Common.MODEL_ITERATIONS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		for (RecordSetRow row : spatialUnits) {
//			SpatialUnitRow su = (SpatialUnitRow) row;
//			System.out.println(su.getSpatialUnitId());
//		}
		
//        int i = 0;
//		for (RecordSetRow row : households) {
//			HouseholdRow hh = (HouseholdRow) row;
//			System.out.println(hh.getHouseholdId() + " " + hh.getSpatialunit().getSpatialUnitId());
//			ArrayList<PersonRow> hh_p = (ArrayList<PersonRow>) hh.getMembers();
//			for (PersonRow p : hh_p)
//				System.out.println("Person: " + p.getId() + " " + p.getHouseholdId() + " " + p.getAgeGroupId() + " " + p.getSex());
//			if (i++ > 10)
//				break;
//		}
        System.out.println(DateUtil.now() + ": end");
	}

	/**
	 * Main model loop
	 * @param iterations number of iterations to be run
	 * @throws SQLException 
	 */
	public static void runModel(Database db, int iterations) throws SQLException {
		EventManager<PersonRow> personEventManager = new EventManager<PersonRow>();
		// TODO: how can the events be constructed at another place to have this class/function independent of the
		//       concrete event types??? Maybe put into its own static class or a ModelMain class?
		PersonDeath personDeath = new PersonDeath(db, personEventManager);
		
		for (int i = 0; i != iterations; i++) {
	        System.out.println(DateUtil.now() + ": running model year " + i + " of " + iterations);
			// Loop through all households
	        int j = 0;
	        // the following clone() is necessary because otherwise it wouldn't be possible to remove households from
	        // the original list while iterating through it
	        Households hh_helper = (Households) households.clone();
			for (HouseholdRow household : hh_helper) {
				if (j % 1000 == 0) {
					System.out.println(DateUtil.now() + ": Processing household " + j + " of " + households.size() + ", nr. of persons: " + persons.size());
				}
				// Loop through all persons of the household
				ArrayList<PersonRow> p_helper = (ArrayList<PersonRow>) ((ArrayList<PersonRow>) household.getMembers()).clone();
				for (PersonRow person : p_helper) {
					personEventManager.process(person);
				}
				j++;
			}
		}
	}
}
