/**
 * 
 */
package at.sume.dm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import net.remesch.db.Database;
import net.remesch.db.Sequence;
import net.remesch.util.DateUtil;
import at.sume.dm.demography.events.ChildBirth;
import at.sume.dm.demography.events.EventManager;
import at.sume.dm.demography.events.PersonDeath;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.Dwellings;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager;
import at.sume.dm.indicators.managers.MoversIndicatorManager;
import at.sume.dm.indicators.managers.PercentileIndicatorManager;
import at.sume.dm.migration.SampleImmigratingHouseholds;
import at.sume.dm.model.core.EntityDecisionManager;
import at.sume.dm.model.output.OutputManager;
import at.sume.dm.model.residential_mobility.DwellingsOnMarket;
import at.sume.dm.model.residential_mobility.MinimumIncome;
import at.sume.dm.model.residential_mobility.RentPerSpatialUnit;
import at.sume.dm.model.residential_mobility.ResidentialMobility;
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
	private static Sequence householdSeq;
	private static Persons persons;
	private static Sequence personSeq;
	private static Dwellings dwellings;
	private static Sequence dwellingSeq;
	private static DwellingsOnMarket dwellingsOnMarket;
	private static OutputManager outputManager;

	private static String printInfo() {
		return DateUtil.now() + " (usedmem=" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 + "m)";
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
        System.out.println(printInfo() + ": start");
		Database db = Common.openDatabase();
//		Database odb = Common.openOutputDatabase();
		Common.init();

		// Load entity sets from database
		try {
			spatialUnits = new SpatialUnits(db);
	        System.out.println(printInfo() + ": loaded " + spatialUnits.size() + " spatial units");
			dwellings = new Dwellings(db);
			dwellingSeq = new Sequence(dwellings.get(dwellings.size() - 1).getDwellingId() + 1);
			DwellingRow.setDwellingIdSeq(dwellingSeq);
	        System.out.println(printInfo() + ": loaded " + dwellings.size() + " dwellings");
			households = new Households(db);
	        System.out.println(printInfo() + ": loaded " + households.size() + " households");
	        // TODO: sequence generation could be completely put into RecordSetRow class
	        householdSeq = new Sequence(households.get(households.size() - 1).getHouseholdId() + 1);
	        HouseholdRow.setHouseholdIdSeq(householdSeq);
	        persons = new Persons(db);
	        personSeq = new Sequence(persons.get(persons.size() - 1).getPersonId() + 1);
	        PersonRow.setPersonIdSeq(personSeq);
	        System.out.println(printInfo() + ": loaded " + persons.size() + " persons");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// Link dwellings to spatial units
		dwellings.linkSpatialUnits(spatialUnits);
        System.out.println(printInfo() + ": linked dwellings + spatial units");
        // Link households to dwellings
        households.linkDwellings(dwellings);
        System.out.println(printInfo() + ": linked households + dwellings");
        households.setSpatialunits(spatialUnits);
        // Inter-link persons and households
        persons.linkHouseholds(households);
        System.out.println(printInfo() + ": linked households + persons");
		
        // get all dwellings on the housing market
        dwellingsOnMarket = new DwellingsOnMarket(dwellings, spatialUnits);
        System.out.println(printInfo() + ": determined all available dwellings on the housing market");
        // determine household-types
        households.determineHouseholdTypes();
        System.out.println(printInfo() + ": determined all household types");
        
        outputManager = new OutputManager(Common.getPathOutput(), households, dwellings, persons);
        
		// Model main loop
		// - Biographical events for all persons/households
		// - Find unsatisfied households
		// - Simulate moves of unsatisfied households
        try {
        	short modelIterations = Short.parseShort(Common.getSysParam("ModelIterations"));
			runModel(db, modelIterations);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        System.out.println(printInfo() + ": end");
        System.exit(0);
    }

	/**
	 * Main model loop
	 * @param iterations number of iterations to be run
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	@SuppressWarnings("unchecked")
	public static void runModel(Database db, short iterations) throws SQLException, FileNotFoundException, IOException, SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		EventManager<PersonRow> personEventManager = new EventManager<PersonRow>();
		// TODO: how can the events be constructed at another place to have this class/function independent of the
		//       concrete event types??? Maybe put into its own static class or a ModelMain class?
		// TODO: Use enums!
		@SuppressWarnings("unused")
		PersonDeath personDeath = new PersonDeath(db, personEventManager);
		@SuppressWarnings("unused")
		ChildBirth childBirth = new ChildBirth(db, personEventManager);
		// TODO: include scenario handling!!!
		SampleImmigratingHouseholds sampleImmigratingHouseholds = new SampleImmigratingHouseholds("STATA2010");
		
		EntityDecisionManager<HouseholdRow, Households> householdDecisionManager = new EntityDecisionManager<HouseholdRow, Households>();
		MinimumIncome minimumIncome = new MinimumIncome(db, householdDecisionManager, households);
		ResidentialMobility residentialMobility = new ResidentialMobility(minimumIncome);
		int modelStartYear = Common.getModelStartYear();
		int modelEndYear = modelStartYear + iterations;
		for (int modelYear = modelStartYear; modelYear != modelEndYear; modelYear++) {
	        // this must be done each model year because with add/remove it is a problem when the age of a person changes
			buildIndicators();			
	        System.out.println(printInfo() + ": build of model indicators complete");
	        outputManager.output((short) modelYear);
	        System.out.println(printInfo() + ": model data output to database");
	        AllHouseholdsIndicatorManager.outputIndicators(modelYear);
			ArrayList<HouseholdRow> potentialMovers = new ArrayList<HouseholdRow>();
	        int j = 0;
	        // the following clone() is necessary because otherwise it wouldn't be possible to remove households from
	        // the original list while iterating through it
	        Households hh_helper = (Households) households.clone();
			// Loop through all households to find potential movers, process demographic events
			for (HouseholdRow household : hh_helper) {
				if (j % 100000 == 0) {
					System.out.println(printInfo() + ": Processing household " + j + " of " + households.size() + " in year " + modelYear + ", nr. of persons: " + persons.size());
				}
				
				// Remove household from all indicators in its original state
				// the disadvantage of this solution is that the currently processed household
				// is missing in the indicators while it is processed - the up side is we don't need a clone-method
				// maybe its more realistic anyway but the effect of this should be limited
//				AllHouseholdsIndicatorManager.removeHousehold(household);
				
				// Process demographic events for all household members
				ArrayList<PersonRow> p_helper = (ArrayList<PersonRow>) ((ArrayList<PersonRow>) household.getMembers()).clone();
				for (PersonRow person : p_helper) {
					personEventManager.process(person);
				}
				// Household was removed during demographic events -> process next household 
				if (household.getMembers().size() == 0)
					continue;
				
				// Add household if it already made a moving decision previously
				if (household.getMovingDecisionYear() != 0) {
					potentialMovers.add(household);
					continue;
				}
				
				// Process household decisions
				// (minimum income, minimum living space, calculation of residential satisfaction)
//				householdDecisionManager.process(household);
				// TODO: add to potential_movers if this is the result of householdDecisionManager

				// Calculate residential mobility depending on previous decisions
				// TODO: save residential satisfaction result for later use
				short residential_satisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(household, modelYear);
				assert (residential_satisfaction >= 0) && (residential_satisfaction <= 1000) : "residential satisfaction out of range (" + residential_satisfaction + ")";
				household.setCurrentResidentialSatisfaction(residential_satisfaction);
				if (residential_satisfaction + household.getResidentialSatisfactionThreshMod() < Common.getResidentialSatisfactionThreshold()) {
					// TODO: add the household to a random position in the ArrayList
					potentialMovers.add(household);
					if (household.getMovingDecisionYear() == 0) {
						if (modelYear == modelStartYear) {
							// In first model year, household may already have decided earlier
							Random r = new Random();
							int movingDecisionYear = Common.getMovingDecisionMin() + r.nextInt(modelYear - Common.getMovingDecisionMin() + 1);
							household.setMovingDecisionYear((short) movingDecisionYear);
						} else {
							household.setMovingDecisionYear((short) modelYear);
						}
					}
					// TODO: add potential mover to the indicators
				}
				
				// Add potentially changed household to the indicators
//				AllHouseholdsIndicatorManager.addHousehold(household);
				
				j++;
			}
//			// Save changes
//			households = hh_helper;
			// Update rent prices for each spatial unit from last years data (from the movers indicators)
			// from the second year on
//			CostEffectiveness costEffectiveness = (CostEffectiveness)ResidentialSatisfactionManager.COSTEFFECTIVENESS.getComponent();
			// TODO: shouldn't this be done after the movings??? But we need fresh movers indicators...
			if (modelYear > modelStartYear)
				RentPerSpatialUnit.updateRentPerSpatialUnit();
			// Reset the movers indicators
			MoversIndicatorManager.resetIndicators();
			outputFreeDwellings(modelYear, "before moving households");
			// Loop through potential movers
			int hhFoundNoDwellings = 0, hhNoSatisfaction = 0, hhNoAspiration = 0, hhZeroIncome = 0, hhLowIncome = 0;
			j = 0;
			for (HouseholdRow household : potentialMovers) {
				if (j % 10000 == 0) {
					System.out.println(printInfo() + ": Processing potential mover " + j + " of " + potentialMovers.size() + " in year " + modelYear);
				}
				// 1) estimate the aspiration region: 
				//    a) needed living space - by household size 
				//    b) maximum costs - by current costs, household income
				// lower limit: commonly defined by the current dwelling; upper limit: set by the standards to
				// which the household can reasonably aspire (Knox/Pinch 2010, p.263)
				residentialMobility.estimateAspirationRegion(household, modelYear);
				
				// 2) define the search area
				// a) get all spatial units with costs within the aspiration region of the household
				ArrayList<Integer> potentialTargetSpatialUnitIds = RentPerSpatialUnit.getSpatialUnitsBelowGivenPrice(household.getAspirationRegionMaxCosts());
				if (potentialTargetSpatialUnitIds.size() == 0) {
					hhNoAspiration++;
					if (household.getAspirationRegionMaxCosts() <= 0)
						hhZeroIncome++;
					if (household.getAspirationRegionMaxCosts() <= 63)
						hhLowIncome++;
				} else {
					// b) compare estimated residential satisfaction in these spatial units and select the highest scoring 
					//    spatial units (random component for each unit, number of units selected as sysparam)
					ArrayList<SpatialUnitRow> potentialTargetSpatialUnits = spatialUnits.getSpatialUnits(potentialTargetSpatialUnitIds);
					int maxResidentialSatisfactionEstimate = household.estimateResidentialSatisfaction(potentialTargetSpatialUnits, modelYear);
					// c) look for a configurable number of randomly chosen dwellings in these units and compute residential satisfaction. take the first
					//    dwelling with a higher result than the current dwelling (= satisfying). limit the number of dwellings
					//    considered (sysparam)
					// do this only, if the potential residential satisfaction is higher than the current
					// residential satisfaction - otherwise save computing time! (depending on a sysparam)
					if ((maxResidentialSatisfactionEstimate > household.getCurrentResidentialSatisfaction()) || (Common.getAlwaysLookForDwellings() != 0)) {
						// do the extra mile and look for a dwelling
						DwellingRow suitableDwelling = residentialMobility.searchDwelling(household, modelYear, dwellingsOnMarket);
						household.clearResidentialSatisfactionEstimate();
						if (suitableDwelling != null) {
							household.relocate(dwellingsOnMarket, suitableDwelling);
						} else {
	//						if (!residentialMobility.searchDwelling(household, modelYear, dwellingsOnMarket)) {
							// if number of maximum search years reached - stop searching
							//household.redefineAspirations();
							hhFoundNoDwellings++;
						}
						// TODO: what happens if the household didn't find a new dwelling?
						// depending on whether this was a forced move the household could stay at its current residence or vanish (= move outside)
					} else {
						// TODO: what to do if the estimated residential satisfaction is not higher than
						// the current one - household stays (except on a forced move)
						hhNoSatisfaction++;
					}
				}
				j++;
			}
			System.out.println(printInfo() + " " + hhNoAspiration + " out of " + potentialMovers.size() + " potential moving households found no spatial unit within their aspiration region. " + 
					"Out of these " + hhZeroIncome + " households can afford a dwelling with costs <= 0 " +
					"and " + hhLowIncome + " households can afford a dwelling with costs <= 63 per m²/year only");
			System.out.println(printInfo() + " " + hhNoSatisfaction + " out of " + potentialMovers.size() + " potential moving households would not improve their estimated residential satisfaction");
			System.out.println(printInfo() + " " + hhFoundNoDwellings + " out of " + potentialMovers.size() + " potential moving households found no dwelling");
			outputFreeDwellings(modelYear, "after moving households, before immigration");
			// Immigrating Households
			ArrayList<HouseholdRow> immigratingHouseholds = sampleImmigratingHouseholds.sample(modelYear);
			hhFoundNoDwellings = 0; hhNoSatisfaction = 0;
			for (HouseholdRow household : immigratingHouseholds) {
				residentialMobility.estimateAspirationRegion(household, modelYear);
				ArrayList<Integer> potentialTargetSpatialUnitIds = RentPerSpatialUnit.getSpatialUnitsBelowGivenPrice(household.getAspirationRegionMaxCosts());
				if (potentialTargetSpatialUnitIds.size() > 0) {
					ArrayList<SpatialUnitRow> potentialTargetSpatialUnits = spatialUnits.getSpatialUnits(potentialTargetSpatialUnitIds);
					household.estimateResidentialSatisfaction(potentialTargetSpatialUnits, modelYear);
					DwellingRow dwelling = residentialMobility.searchDwelling(household, modelYear, dwellingsOnMarket);
					household.clearResidentialSatisfactionEstimate();
					if (dwelling == null) {
						// household didn't find a suitable dwelling -> join another household
						// TODO: update indicators
						//household.redefineAspirations();
						hhFoundNoDwellings++;
					} else {
						// 1) add household to common lists
						households.add(household);
						for (PersonRow member : household.getMembers()) {
							persons.add(member);
						}
						// 2 move household
						household.relocate(dwellingsOnMarket, dwelling);
						// TODO: update indicators
					}
				} else {
					// no spatial units below the max costs for the household -> join another household
					// TODO: update indicators
					hhNoSatisfaction++;
				}
			}
			System.out.println(printInfo() + " " + hhFoundNoDwellings + " out of " + immigratingHouseholds.size() + " immigrating households found no dwelling");
			System.out.println(printInfo() + " " + hhNoSatisfaction + " out of " + immigratingHouseholds.size() + " immigrating households could not find spatial units matching their aspirations");
			if (modelYear == modelEndYear - 1)
				outputFreeDwellings(modelYear, "after immigration");
			
			// Aging of persons
			persons.aging();
		} // model year
	}
	
	public static void buildIndicators() {
//		int j = 0;
		AllHouseholdsIndicatorManager.resetIndicators();
		for (HouseholdRow household : households) {
//			if (++j % 10000 == 0) {
//				System.out.println(printInfo() + ": Added household " + j + " of " + households.size() + " to indicators");
//			}
			AllHouseholdsIndicatorManager.addHousehold(household);
			PercentileIndicatorManager.addHousehold(household);
		}
	}
	
	public static void outputFreeDwellings(int modelYear, String label) throws FileNotFoundException {
		// TODO: put this in an extra class and delete the file once per model run
		String path = Common.getPathOutput();
		if (path == null) path = "";
		String pathName;
		if (path.endsWith("\\") || (path == ""))
			pathName = path + "FreeDwellings.csv";
		else
			pathName = path + "\\" + "FreeDwellings.csv";
		FileOutputStream freeDwellingsFile = new FileOutputStream(pathName, true);
		PrintStream ps = new PrintStream(freeDwellingsFile);
//		ps.println("======= " + label + "=======" + modelYear + "=======");
		dwellingsOnMarket.outputDwellingsPerSize(ps, modelYear);
	}
}
