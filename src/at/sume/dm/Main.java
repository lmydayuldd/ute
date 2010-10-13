/**
 * 
 */
package at.sume.dm;

import java.sql.SQLException;
import java.util.ArrayList;
import net.remesch.util.Database;
import net.remesch.util.DateUtil;
import at.sume.dm.demography.events.ChildBirth;
import at.sume.dm.demography.events.EventManager;
import at.sume.dm.demography.events.PersonDeath;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.indicators.IndicatorManager;
import at.sume.dm.model.core.EntityDecisionManager;
import at.sume.dm.model.residential_mobility.MinimumIncome;
import at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionManager;

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
		Database db = Common.openDatabase();
		Common.init();

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
		
        // Initial build of model indicators
		buildIndicators();			
        System.out.println(DateUtil.now() + ": initial built of model indicators complete");
        
		// Model main loop
		// - Biographical events for all persons/households
		// - Find unsatisfied households
		// - Simulate moves of unsatisfied households
        try {
        	int modelIterations = Integer.parseInt(Common.getSysParam("ModelIterations"));
			runModel(db, modelIterations);
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
	@SuppressWarnings("unchecked")
	public static void runModel(Database db, int iterations) throws SQLException {
		EventManager<PersonRow> personEventManager = new EventManager<PersonRow>();
		// TODO: how can the events be constructed at another place to have this class/function independent of the
		//       concrete event types??? Maybe put into its own static class or a ModelMain class?
		// TODO: Use enums!
		@SuppressWarnings("unused")
		PersonDeath personDeath = new PersonDeath(db, personEventManager);
		@SuppressWarnings("unused")
		ChildBirth childBirth = new ChildBirth(db, personEventManager);
		
		EntityDecisionManager<HouseholdRow, Households> householdDecisionManager = new EntityDecisionManager<HouseholdRow, Households>();
		@SuppressWarnings("unused")
		MinimumIncome minimumIncome = new MinimumIncome(db, householdDecisionManager, households);
		
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
				
				// TODO: Store household in its original state for later removal from the indicators
//				HouseholdRow hhr_helper = household.clone();
				// Remove household from all indicators in its original state
				// the disadvantage of this solution is that the currently processed household
				// is missing in the indicators while it is processed - the up side is we don't need a clone-method
				IndicatorManager.removeHousehold(household);
				
				// Process demographic events for all household members
				ArrayList<PersonRow> p_helper = (ArrayList<PersonRow>) ((ArrayList<PersonRow>) household.getMembers()).clone();
				for (PersonRow person : p_helper) {
					personEventManager.process(person);
				}
				
				// Process household decisions
				// (minimum income, minimum living space, calculation of residential satisfaction)
				householdDecisionManager.process(household);

				// TODO: residential mobility depending on previous decisions
				
				// Add potentially changed household to the indicators
				IndicatorManager.addHousehold(household);
				
				j++;
			}
		}
	}
	
	public static void buildIndicators() {
		IndicatorManager.resetIndicators();
		for (HouseholdRow household : households) {
			IndicatorManager.addHousehold(household);
		}
	}
	
	public static void calcResidentialSatisfaction(int modelYear) {
		for (HouseholdRow household : households) {
			ResidentialSatisfactionManager.calcResidentialSatisfaction(household, household.getSpatialunit(), modelYear);
			// TODO: Ergebnis merken, wenn unter threshold, dann Haushalt in eigene Liste hinzufügen (an
			// zufälliger Stelle), die dann anschließend abgearbeitet wird für die Umzüge
			// TODO: how is the residential satisfaction influenced by its value from previous year?
		}
	}
}
