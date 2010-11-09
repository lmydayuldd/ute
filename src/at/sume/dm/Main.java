/**
 * 
 */
package at.sume.dm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.db.Database;
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
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.indicators.AllHouseholdsIndicatorManager;
import at.sume.dm.indicators.MoversIndicatorManager;
import at.sume.dm.model.core.EntityDecisionManager;
import at.sume.dm.model.residential_mobility.DwellingsOnMarket;
import at.sume.dm.model.residential_mobility.MinimumIncome;
import at.sume.dm.model.residential_satisfaction.CostEffectiveness;
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
	private static Dwellings dwellings;
	private static DwellingsOnMarket dwellingsOnMarket;

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
	        System.out.println(DateUtil.now() + ": loaded " + spatialUnits.size() + " spatial units");
			households = new Households(db);
	        System.out.println(DateUtil.now() + ": loaded " + households.size() + " households");
	        persons = new Persons(db);
	        System.out.println(DateUtil.now() + ": loaded " + persons.size() + " persons");
			dwellings = new Dwellings(db);
	        System.out.println(DateUtil.now() + ": loaded " + dwellings.size() + " dwellings");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// Link dwellings to spatial units
		dwellings.linkSpatialUnits(spatialUnits);
        System.out.println(DateUtil.now() + ": linked households + spatial units");
        // Link households to dwellings
        households.linkDwellings(dwellings);
        System.out.println(DateUtil.now() + ": linked households + dwellings");
        households.setSpatialunits(spatialUnits);
        // Inter-link persons and households
        persons.linkHouseholds(households);
        System.out.println(DateUtil.now() + ": linked households + persons");
		
        // get all dwellings on the housing market
        dwellingsOnMarket = new DwellingsOnMarket(dwellings, spatialUnits);
        System.out.println(DateUtil.now() + ": determined all available dwellings on the housing market");
        // determine household-types
        households.determineHouseholdTypes();
        System.out.println(DateUtil.now() + ": determined all household types");
        
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
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        System.out.println(DateUtil.now() + ": end");
        System.exit(0);
    }

	/**
	 * Main model loop
	 * @param iterations number of iterations to be run
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public static void runModel(Database db, int iterations) throws SQLException, FileNotFoundException, IOException {
		EventManager<PersonRow> personEventManager = new EventManager<PersonRow>();
		// TODO: how can the events be constructed at another place to have this class/function independent of the
		//       concrete event types??? Maybe put into its own static class or a ModelMain class?
		// TODO: Use enums!
		@SuppressWarnings("unused")
		PersonDeath personDeath = new PersonDeath(db, personEventManager);
		@SuppressWarnings("unused")
		ChildBirth childBirth = new ChildBirth(db, personEventManager);
		
		EntityDecisionManager<HouseholdRow, Households> householdDecisionManager = new EntityDecisionManager<HouseholdRow, Households>();
		MinimumIncome minimumIncome = new MinimumIncome(db, householdDecisionManager, households);
		int modelStartYear = Common.getModelStartYear();
		int modelEndYear = modelStartYear + iterations;
		for (int modelYear = modelStartYear; modelYear != modelEndYear; modelYear++) {
	        System.out.println(DateUtil.now() + ": running model year " + modelYear + " of " + modelEndYear);
	        AllHouseholdsIndicatorManager.outputIndicators(modelYear);
			ArrayList<HouseholdRow> potential_movers = new ArrayList<HouseholdRow>();
	        int j = 0;
	        // the following clone() is necessary because otherwise it wouldn't be possible to remove households from
	        // the original list while iterating through it
	        Households hh_helper = (Households) households.clone();
			// Loop through all households to find potential movers
			for (HouseholdRow household : hh_helper) {
				if (j % 1000 == 0) {
					System.out.println(DateUtil.now() + ": Processing household " + j + " of " + households.size() + ", nr. of persons: " + persons.size());
				}
				
				// Remove household from all indicators in its original state
				// the disadvantage of this solution is that the currently processed household
				// is missing in the indicators while it is processed - the up side is we don't need a clone-method
				// maybe its more realistic anyway but the effect of this should be limited
				AllHouseholdsIndicatorManager.removeHousehold(household);
				
				// Process demographic events for all household members
				ArrayList<PersonRow> p_helper = (ArrayList<PersonRow>) ((ArrayList<PersonRow>) household.getMembers()).clone();
				for (PersonRow person : p_helper) {
					personEventManager.process(person);
				}
				// Household was removed during demographic events -> process next household 
				if (household.getMembers().size() == 0)
					continue;
				
				// Process household decisions
				// (minimum income, minimum living space, calculation of residential satisfaction)
				householdDecisionManager.process(household);
				// TODO: add to potential_movers if this is the result of householdDecisionManager

				// Calculate residential mobility depending on previous decisions
				// TODO: save residential satisfaction result for later use
				int residential_satisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(household, household.getSpatialunit(), modelYear);
				if (residential_satisfaction + household.getResidentialSatisfactionThreshMod() < Common.getResidentialSatisfactionThreshold()) {
					// TODO: add the household to a random position in the ArrayList
					potential_movers.add(household);
					if (household.getMovingDecisionYear() == 0)
						household.setMovingDecisionYear(modelYear);
					// TODO: add potential mover to the indicators
				}
				
				// Add potentially changed household to the indicators
				AllHouseholdsIndicatorManager.addHousehold(household);
				
				j++;
			}
//			// Save changes
//			households = hh_helper;
			// Update rent prices for each spatial unit from last years data (from the movers indicators)
			// from the second year on
			CostEffectiveness costEffectiveness = (CostEffectiveness)ResidentialSatisfactionManager.COSTEFFECTIVENESS.getComponent();
			if (modelYear > 0)
				costEffectiveness.updateRentPerSpatialUnit();
			// Reset the movers indicators
			MoversIndicatorManager.resetIndicators();
			// Loop through potential movers
			for (HouseholdRow household : potential_movers) {
				int currentResidentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(household, modelYear);
				// 1) estimate the aspiration region: 
				//    a) needed living space - by household size 
				//    b) maximum costs - by current costs, household income
				// lower limit: commonly defined by the current dwelling; upper limit: set by the standards to
				// which the household can reasonably aspire (Knox/Pinch 2010, p.263)
				// TODO: make this part of HouseholdRow (estimateAspirationRegion()) - problem is access to minimumIncome!
				// TODO: or even better: put this in an extra class HouseholdMove which contains all information belonging to a move from
				//       definition of the aspiration region to the actual move itself
				long maxCostOfResidence = household.getYearlyIncome() - minimumIncome.estimateMinIncomeLeftForLiving(household);
				if (household.getMovingDecisionYear() == modelYear) {
					// Household just began searching - set initial values
					household.estimateDesiredLivingSpace();
					// lower value from income share and current dwelling costs (per m²)
					long maxCostOfResidencePerSqm = maxCostOfResidence / household.getAspirationRegionLivingSpaceMin();
					long currentCostOfResidencePerSqm = household.getCostOfResidence() / household.getLivingSpace();
					household.setAspirationRegionMaxCosts(Math.min(maxCostOfResidencePerSqm, currentCostOfResidencePerSqm));
				} else {
					// Household continues searching - modify values from previous year
					// if maximum costs are already at the maximum for the household then reduce minimum
					// living space
					long maxCostOfResidencePerSqm = maxCostOfResidence / household.getAspirationRegionLivingSpaceMin();
					if (maxCostOfResidencePerSqm <= household.getAspirationRegionMaxCosts()) {
						// reduce aspired minimum living space until it drops below the current living space
						// TODO: maybe do this over a configurable number of years
						if (household.getAspirationRegionLivingSpaceMin() > household.getLivingSpace()) {
							household.setAspirationRegionLivingSpaceMin(household.getLivingSpace());
						}
					} else {
						// increase aspired max costs of residence
						// TODO: maybe do this over a configurable number of years
						household.setAspirationRegionMaxCosts(maxCostOfResidencePerSqm);
					}
				}
				// 2) define the search area
				// a) get all spatial units with costs within the aspiration region of the household
				ArrayList<Long> potentialTargetSpatialUnitIds = costEffectiveness.getSpatialUnitsBelowGivenPrice(household.getAspirationRegionMaxCosts());
				// b) compare estimated residential satisfaction in these spatial units and select the highest scoring 
				//    spatial units (random component for each unit, number of units selected as sysparam)
				int maxResidentialSatisfactionEstimate = household.estimateResidentialSatisfaction(potentialTargetSpatialUnitIds, modelYear);
				potentialTargetSpatialUnitIds = household.getPreferredSpatialUnits(Common.getSearchAreaSize());
				// c) look for a configurable number of randomly chosen dwellings in these units and compute residential satisfaction. take the first
				//    dwelling with a higher result than the current dwelling (= satisfying). limit the number of dwellings
				//    considered (sysparam)
				// do this only, if the potential residential satisfaction is higher than the current
				// residential satisfaction - otherwise save computing time! (depending on a sysparam)
				if ((maxResidentialSatisfactionEstimate > currentResidentialSatisfaction) || (Common.getAlwaysLookForDwellings() != 0)) {
					// do the extra mile and look for a dwelling
					dwellingsOnMarket.selectSuitableDwellingsOnMarket(potentialTargetSpatialUnitIds, household.getAspirationRegionLivingSpaceMin(), household.getAspirationRegionLivingSpaceMax(), household.getAspirationRegionMaxCosts());
					DwellingRow suitableDwelling;
					boolean householdMoved = false;
					for (int i = 0; i != Common.getDwellingsConsideredPerYear(); i++) {
						suitableDwelling = dwellingsOnMarket.pickRandomSuitableDwelling();
						int potentialResidentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(household, suitableDwelling, modelYear);
						if (potentialResidentialSatisfaction > currentResidentialSatisfaction) {
							// we have the dwelling - move there!
							household.relocate(suitableDwelling);
							householdMoved = true;
							break;
						}
					}
					if (!householdMoved) {
						// if number of maximum search years reached - stop searching
						//household.redefineAspirations();
					}
					// TODO: what happens if the household didn't find a new dwelling?
				} else {
					// TODO: what to do if the estimated residential satisfaction is not higher than
					// the current one - household stays (except on a forced move)
				}
			}
		}
	}
	
	public static void buildIndicators() {
		AllHouseholdsIndicatorManager.resetIndicators();
		for (HouseholdRow household : households) {
			AllHouseholdsIndicatorManager.addHousehold(household);
		}
	}
}
