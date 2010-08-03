/**
 * 
 */
package at.sume.dm;

import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.util.Database;
import net.remesch.util.DateUtil;
import at.sume.db.RecordSetRow;
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
        System.out.println("Start @ " + DateUtil.now());
		Database db = new Database(Common.GetDbLocation());

		// Load entity sets from database
		try {
			spatialUnits = new SpatialUnits(db);
	        System.out.println("Loaded spatial units @ " + DateUtil.now());
			households = new Households(db);
	        System.out.println("Loaded households @ " + DateUtil.now());
	        persons = new Persons(db);
	        System.out.println("Loaded persons @ " + DateUtil.now());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Link households to spatial units
		households.linkSpatialUnits(spatialUnits);
        System.out.println("Linked households + spatial units @ " + DateUtil.now());
        // Inter-link persons and households
        persons.linkHouseholds(households);
        System.out.println("Linked households + persons @ " + DateUtil.now());
		
		// Model main loop
		// - Biographical events for all persons/households
		// - Find unsatisfied households
		// - Simulate moves of unsatisfied households
		
//		for (RecordSetRow row : spatialUnits) {
//			SpatialUnitRow su = (SpatialUnitRow) row;
//			System.out.println(su.getSpatialUnitId());
//		}
		
        int i = 0;
		for (RecordSetRow row : households) {
			HouseholdRow hh = (HouseholdRow) row;
			System.out.println(hh.getHouseholdId() + " " + hh.getSpatialunit().getSpatialUnitId());
			ArrayList<PersonRow> hh_p = (ArrayList<PersonRow>) hh.getMembers();
			for (PersonRow p : hh_p)
				System.out.println("Person: " + p.getId() + " " + p.getHouseholdId() + " " + p.getAgeGroupId() + " " + p.getSex());
			if (i++ > 10)
				break;
		}
        System.out.println("End @ " + DateUtil.now());
	}

}
