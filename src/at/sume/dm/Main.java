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
import java.util.Collections;
import java.util.List;
import net.remesch.util.Random;

import net.remesch.db.Database;
import net.remesch.db.Sequence;
import net.remesch.util.DateUtil;
import net.remesch.util.FileUtil;
import at.sume.dm.buildingprojects.AdditionalDwellingsPerYear;
import at.sume.dm.buildingprojects.SampleBuildingProjects;
import at.sume.dm.demography.events.ChildBirth;
import at.sume.dm.demography.events.MovingTogether;
import at.sume.dm.demography.events.EventManager;
import at.sume.dm.demography.events.LeavingParents;
import at.sume.dm.demography.events.PersonDeath;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.Dwellings;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.indicators.AggregatedDwellings;
import at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager;
import at.sume.dm.indicators.managers.MoversIndicatorManager;
import at.sume.dm.indicators.managers.PercentileIndicatorManager;
import at.sume.dm.indicators.simple.CountDemographicMovements;
import at.sume.dm.indicators.simple.CountMigrationDetails;
import at.sume.dm.indicators.simple.CountMigrationPerSpatialUnit;
import at.sume.dm.migration.SampleMigratingHouseholds;
import at.sume.dm.model.core.EntityDecisionManager;
import at.sume.dm.model.output.Fileable;
import at.sume.dm.model.output.OutputManager;
import at.sume.dm.model.residential_mobility.DwellingsOnMarket;
import at.sume.dm.model.residential_mobility.MinimumIncome;
import at.sume.dm.model.residential_mobility.NoDwellingFoundReason;
import at.sume.dm.model.residential_mobility.RentPerSpatialUnit;
import at.sume.dm.model.residential_mobility.ResidentialMobility;
import at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionManager;
import at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionWeight;
import at.sume.dm.model.residential_satisfaction.UDPCentrality;
import at.sume.dm.model.residential_satisfaction.UDPPublicTransportAccessibility;
import at.sume.dm.scenario_handling.Scenario;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.MigrationRealm;

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
	private static CountMigrationPerSpatialUnit migrationPerSpatialUnit;
	private static CountMigrationDetails migrationDetails;
	private static CountDemographicMovements demographicMovementsPerSpatialUnit;
	private static AggregatedDwellings aggregatedDwellings;
	private static RentPerSpatialUnit rentPerSpatialUnit;
	private static Scenario scenario;

	private static String printInfo() {
		return DateUtil.now() + " (usedmem=" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 + "m)";
	}

	private static String printInfo(int modelRun) {
		return DateUtil.now() + " (usedmem=" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 + "m)@" + (modelRun + 1);
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        System.out.println(printInfo() + ": start");
		Database db = Common.openDatabase();
//		Database odb = Common.openOutputDatabase();
		Common.init();
		try {
			scenario = new Scenario(db, Common.getScenarioId());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
        rentPerSpatialUnit = RentPerSpatialUnit.getInstance(scenario.getRentScenario(), Common.getSpatialUnitLevel());

        int modelRuns = Common.getModelRuns();
        for (int modelRun = 0; modelRun != modelRuns; modelRun++) {
            System.out.println(printInfo(modelRun) + ": ======================= model run " + (modelRun + 1) + " of " + modelRuns);
			// Load entity sets from database
			try {
				spatialUnits = new SpatialUnits(db, Common.getSpatialUnitLevel());
		        System.out.println(printInfo(modelRun) + ": loaded " + spatialUnits.size() + " spatial units");
				dwellings = new Dwellings(db, Common.getSpatialUnitLevel());
				dwellingSeq = new Sequence(dwellings.get(dwellings.size() - 1).getDwellingId() + 1);
				DwellingRow.setDwellingIdSeq(dwellingSeq);
		        System.out.println(printInfo(modelRun) + ": loaded " + dwellings.size() + " dwellings");
				households = new Households(db);
		        System.out.println(printInfo(modelRun) + ": loaded " + households.size() + " households");
		        // TODO: sequence generation could be completely put into RecordSetRow class
		        householdSeq = new Sequence(households.get(households.size() - 1).getHouseholdId() + 1);
		        HouseholdRow.setHouseholdIdSeq(householdSeq);
		        persons = new Persons(db);
		        personSeq = new Sequence(persons.get(persons.size() - 1).getPersonId() + 1);
		        PersonRow.setPersonIdSeq(personSeq);
		        System.out.println(printInfo(modelRun) + ": loaded " + persons.size() + " persons");
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	
			// Link dwellings to spatial units
			dwellings.linkSpatialUnits(spatialUnits);
	        System.out.println(printInfo(modelRun) + ": linked dwellings + spatial units");
	        // Link households to dwellings
	        households.linkDwellings(dwellings);
	        System.out.println(printInfo(modelRun) + ": linked households + dwellings");
	        households.setSpatialunits(spatialUnits);
	        // Inter-link persons and households
	        persons.linkHouseholds(households);
	        System.out.println(printInfo(modelRun) + ": linked households + persons");
	
	        // create migration counter + register with households (once!)
	        migrationPerSpatialUnit = new CountMigrationPerSpatialUnit();
	        migrationDetails = new CountMigrationDetails();
	        // TODO: find a way to make this registration out of the MigrationPerSpatialUnit() class directly (but only once!!!)
	        households.get(0).registerMigrationObserver(migrationPerSpatialUnit);
	        households.get(0).registerMigrationObserver(migrationDetails);
	        demographicMovementsPerSpatialUnit = new CountDemographicMovements();
	        persons.get(0).registerDemographyObserver(demographicMovementsPerSpatialUnit);
	        
	        // get all dwellings on the housing market
	        dwellingsOnMarket = new DwellingsOnMarket(dwellings, spatialUnits);
	        System.out.println(printInfo(modelRun) + ": determined all available dwellings on the housing market (" + dwellingsOnMarket.getFreeDwellingsCount() + " out of " + dwellingsOnMarket.getGrossFreeDwellingTotal() + " total free dwellings)");
	        // determine household-types
	        households.determineHouseholdTypes(true);
	        System.out.println(printInfo(modelRun) + ": determined all household types");
	
	        List<List<? extends Fileable>> fileableList = new ArrayList<List<? extends Fileable>>();
	        List<String> fileNameList = new ArrayList<String>();
	        if (Common.isOutputFullData()) {
		        fileableList.add(households.getRowList());
		        fileNameList.add("Households");
		        fileableList.add(dwellings.getRowList());
		        fileNameList.add("Dwellings");
		        fileableList.add(persons.getRowList());
		        fileNameList.add("Persons");
	        }
	        fileableList.add(rentPerSpatialUnit.getRentPerSpatialUnit());
	        fileNameList.add("RentPerSpatialUnit");
	        fileableList.add(AllHouseholdsIndicatorManager.INDICATORS_PER_HOUSEHOLDTYPE_AND_INCOME.getIndicator().getIndicatorList());
	        fileNameList.add("IndicatorsPerHouseholdTypeAndIncome");
	        fileableList.add(AllHouseholdsIndicatorManager.AGGREGATED_HOUSEHOLDS.getIndicator().getIndicatorList());
	        fileNameList.add("AggregatedHouseholds");
	        fileableList.add(AllHouseholdsIndicatorManager.AGGREGATED_PERSONS.getIndicator().getIndicatorList());
	        fileNameList.add("AggregatedPersons");
	        aggregatedDwellings = new AggregatedDwellings();
	        fileableList.add(aggregatedDwellings.getIndicatorList());
	        fileNameList.add("AggregatedDwellings");
	//        fileableList.add(migrationPerSpatialUnit.getIndicatorList());
	//        fileNameList.add("Migrations");
	        outputManager = new OutputManager(Common.getPathOutput(), fileNameList, fileableList);
	        initSimpleOutputFiles();
	        
			// Model main loop
			// - Biographical events for all persons/households
			// - Find unsatisfied households
			// - Simulate moves of unsatisfied households
	        try {
	        	short modelIterations = Short.parseShort(Common.getSysParam("ModelIterations"));
				runModel(db, modelIterations, modelRun);
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
			outputManager.close();
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
	public static void runModel(Database db, short iterations, int modelRun) throws SQLException, FileNotFoundException, IOException, SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		Random r = new Random();
		EventManager<PersonRow> personEventManager = new EventManager<PersonRow>();
		// TODO: how can the events be constructed at another place to have this class/function independent of the
		//       concrete event types??? Maybe put into its own static class or a ModelMain class?
		// TODO: Use enums!
		@SuppressWarnings("unused")
		PersonDeath personDeath = new PersonDeath(db, personEventManager, Common.getPersonMaxAge(), Common.getDeathAdjustment());
		@SuppressWarnings("unused")
		ChildBirth childBirth = new ChildBirth(db, scenario.getFertilityScenario(), personEventManager, Common.getBirthAdjustment());
		SampleMigratingHouseholds sampleMigratingHouseholds = new SampleMigratingHouseholds(scenario.getMigrationScenario(), scenario.getMigrationHouseholdSizeScenario(), scenario.getMigrationIncomeScenario());
		SampleBuildingProjects sampleBuildingProjects = new SampleBuildingProjects(scenario.getBuildingProjectScenario(), scenario.getNewDwellingSizeScenario(), spatialUnits);
		AdditionalDwellingsPerYear additionalDwellingsPerYear = new AdditionalDwellingsPerYear(scenario.getAdditionalDwellingsScenario());
		
		EntityDecisionManager<HouseholdRow, Households> householdDecisionManager = new EntityDecisionManager<HouseholdRow, Households>();
		MinimumIncome minimumIncome = new MinimumIncome(db, householdDecisionManager, households);
		ResidentialMobility residentialMobility = new ResidentialMobility(minimumIncome);
		int modelStartYear = Common.getModelStartYear();
		int modelEndYear = modelStartYear + iterations;
		for (int modelYear = modelStartYear; modelYear != modelEndYear; modelYear++) {
			// for breakpoints in a certain year
			if (modelYear == 2012) {
				@SuppressWarnings("unused")
				int xy = 0;
			}
			
			// Clear migration indicators
			// TODO: include these three into the OutputManager (which might need to have a function for output of data at the end of a model year)
			migrationPerSpatialUnit.clearIndicatorList();
			demographicMovementsPerSpatialUnit.clearIndicatorList();
			migrationDetails.clear();
	        // (Re)build household indicators - this must be done each model year because with add/remove it is a problem when the age of a person changes
			buildIndicators();			
	        System.out.println(printInfo() + ": build of model indicators complete");
	        aggregatedDwellings.build(dwellings.getRowList());
	        outputManager.output((short) modelYear);
	        System.out.println(printInfo() + ": model data output to database");
	        AllHouseholdsIndicatorManager.outputIndicators(modelYear);
	        
	        // Create new-built dwellings
	        List<DwellingRow> newDwellings = sampleBuildingProjects.sample(modelYear);
        	System.out.println(printInfo(modelRun) + ": adding " + newDwellings.size() + " new built dwellings from building projects and putting them on the market");
	        dwellings.addAll(newDwellings);
	        dwellingsOnMarket.addAll(newDwellings);
	        int additionalDwellingsOnMarket = additionalDwellingsPerYear.getAdditionalDwellingsOnMarket(modelYear);
	        if (additionalDwellingsOnMarket > 0) {
	        	System.out.println(printInfo(modelRun) + ": putting additional " + additionalDwellingsOnMarket + " existing dwellings on the market");
	        	int result = dwellingsOnMarket.increase(additionalDwellingsOnMarket);
	        	System.out.println(printInfo(modelRun) + ": " + result + " dwellings were put on the market");
	        }
	        int newRandomDwellingCount = additionalDwellingsPerYear.getNewlyBuiltDwellings(modelYear);
	        if (newRandomDwellingCount > 0) {
	        	System.out.println(printInfo(modelRun) + ": adding " + newRandomDwellingCount + " new built dwellings and putting them on the market");
		        newDwellings = sampleBuildingProjects.sampleRandomDwellings(newRandomDwellingCount);
		        dwellings.addAll(newDwellings);
		        dwellingsOnMarket.addAll(newDwellings);
	        }

	        int movingTogetherCount = Common.getMovingTogetherRate() * (persons.size() / 1000);
	        MovingTogether movingTogether = new MovingTogether(movingTogetherCount, modelYear, dwellingsOnMarket);
	        
	        LeavingParents leavingParents = new LeavingParents(Common.getLeavingParentsProbability(), Common.getChildrenMaxAge(), modelYear);
	        
			ArrayList<HouseholdRow> potentialMovers = new ArrayList<HouseholdRow>();
	        int j = 0;
	        // the following clone() is necessary because otherwise it wouldn't be possible to remove households from
	        // the original list while iterating through it
	        Households hh_helper = (Households) households.clone();
	        // bring the households into a random order
	        Collections.shuffle(hh_helper.getRowList());
			// Loop through all households to find potential movers, process demographic events
			for (HouseholdRow household : hh_helper) {
				if (j % 100000 == 0) {
					System.out.println(printInfo(modelRun) + ": Processing household " + j + " of " + households.size() + " in year " + modelYear + ", nr. of persons: " + persons.size());
				}
				// Process demographic events for all household members
				ArrayList<PersonRow> p_helper = (ArrayList<PersonRow>) ((ArrayList<PersonRow>) household.getMembers()).clone();
				for (PersonRow person : p_helper) {
					personEventManager.process(person);
				}
				// Household was removed during demographic events -> process next household 
				if (household.getMembers().size() == 0) {
					household.remove(dwellingsOnMarket);
					continue;
				}
					
				if (Common.isDemographyOnly() == false) {
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
					// - set scenario for residential satisfaction weight to be used in ResidentialSatisfactionManager
					ResidentialSatisfactionWeight.getInstance(scenario.getHouseholdPrefsScenario());
					UDPCentrality.getInstance(Common.getSpatialUnitLevel());
					UDPPublicTransportAccessibility.getInstance(Common.getSpatialUnitLevel());
					// TODO: save residential satisfaction result for later use
					short residential_satisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(household, modelYear);
					assert (residential_satisfaction >= -1) && (residential_satisfaction <= 1000) : "residential satisfaction out of range (" + residential_satisfaction + ")";
					if (residential_satisfaction != -1) {
						household.setCurrentResidentialSatisfaction(residential_satisfaction);
						if (residential_satisfaction + household.getResidentialSatisfactionThreshMod() < Common.getResidentialSatisfactionThreshold()) {
							// add the household to a random position in the ArrayList
							potentialMovers.add((int)(r.nextDouble() * potentialMovers.size()), household);
							if (household.getMovingDecisionYear() == 0) {
								if (modelYear == modelStartYear) {
									// In first model year, household may already have decided earlier
									int movingDecisionYear = Common.getMovingDecisionMin() + r.nextInt(modelYear - Common.getMovingDecisionMin() + 1);
									household.setMovingDecisionYear((short) movingDecisionYear);
								} else {
									household.setMovingDecisionYear((short) modelYear);
								}
							}
							// TODO: add potential mover to the indicators
						}
					}
					
					// Add household for moving together processing
					movingTogether.addHousehold(household);
					
					// Add children leaving parents
					leavingParents.addHousehold(household);
				}
				j++;
			}
			// Update rent prices for each spatial unit from last years data (from the movers indicators)
			// from the second year on
//			CostEffectiveness costEffectiveness = (CostEffectiveness)ResidentialSatisfactionManager.COSTEFFECTIVENESS.getComponent();
			// TODO: shouldn't this be done after the movings??? But we need fresh movers indicators...
			// TODO: configurable number of units
			ArrayList<Integer> cheapestSpatialUnits = rentPerSpatialUnit.getCheapestSpatialUnits(0);
			if (modelYear > modelStartYear)
				rentPerSpatialUnit.updateRentPerSpatialUnit(spatialUnits, modelYear);
			int lowestYearlyRentPer100Sqm = rentPerSpatialUnit.getLowestYearlyRentPer100Sqm();
			int highestYearlyRentPer100Sqm = rentPerSpatialUnit.getHighestYearlyRentPer100Sqm();
			System.out.println(printInfo(modelRun) + ": lowest rent (€/100m²/yr.): " + lowestYearlyRentPer100Sqm + ", highest rent: " + highestYearlyRentPer100Sqm);
			// Reset the movers indicators
			MoversIndicatorManager.resetIndicators();
			outputFreeDwellings(modelYear, "before moving households + after demographic changes");
			// Loop through potential movers
			int hhFoundNoDwellings = 0, hhNoSatisfaction = 0, hhNoAspiration = 0, hhZeroIncome = 0, hhLowIncome = 0, hhMovedAway = 0;
			int hhNotMoving = 0, hhMovedAwayMemberCount = 0;
			j = 0;
	        System.out.println(printInfo(modelRun) + ": free dwellings before moving: " + dwellingsOnMarket.getFreeDwellingsCount());
	        int maxOutMigrationNational = sampleMigratingHouseholds.getOutMigrationNational(modelYear);
			for (HouseholdRow household : potentialMovers) {
				boolean notMoving = false;
				if (j % 10000 == 0) {
					System.out.println(printInfo(modelRun) + ": Processing potential mover " + j + " of " + potentialMovers.size() + " in year " + modelYear);
				}
				// for breakpoints
				if ((household.getHouseholdType() == HouseholdType.LARGE_FAMILY) && (household.getSpatialunitId() == 91905)) {
					@SuppressWarnings("unused")
					int xy = 0;
				}
				// 1) estimate the aspiration region: 
				//    a) needed living space - by household size 
				//    b) maximum costs - by current costs, household income
				// lower limit: commonly defined by the current dwelling; upper limit: set by the standards to
				// which the household can reasonably aspire (Knox/Pinch 2010, p.263)
				residentialMobility.estimateAspirationRegion(household, modelYear, modelStartYear, highestYearlyRentPer100Sqm);
				
				// 2) define the search area
				// a) get all spatial units with costs within the aspiration region of the household
				ArrayList<Integer> potentialTargetSpatialUnitIds = rentPerSpatialUnit.getSpatialUnitsBelowGivenPrice(household.getAspirationRegionMaxCosts());
				if (potentialTargetSpatialUnitIds.size() == 0) {
					hhNoAspiration++;
					if (household.getAspirationRegionMaxCosts() <= 0)
						hhZeroIncome++;
					if (household.getAspirationRegionMaxCosts() <= 63)
						hhLowIncome++;
				} else {
					DwellingRow dwelling = null;
					ArrayList<SpatialUnitRow> potentialTargetSpatialUnits = spatialUnits.getSpatialUnits(potentialTargetSpatialUnitIds);
					household.estimateResidentialSatisfaction(potentialTargetSpatialUnits, modelYear, Common.getResidentialSatisfactionEstimateRange());
					ArrayList<Integer> preferredSpatialUnits = household.getPreferredSpatialUnits();
					NoDwellingFoundReason noDwellingFoundReason = NoDwellingFoundReason.NO_REASON;
					for (int spatialUnitId : preferredSpatialUnits) {
						SpatialUnitRow spatialUnit = spatialUnits.getSpatialUnit(spatialUnitId);
						if (spatialUnit.isFreeDwellingsAlwaysAvailable()) {
							// Check if the household can afford to live there
							long yearlyRentPer100Sqm = rentPerSpatialUnit.getYearlyAverageRentPer100Sqm(spatialUnitId);
							if (yearlyRentPer100Sqm / 100 <= household.getAspirationRegionMaxCosts()) {
								// Household moves to the surroundings
								if (Common.getMovingOutProbability().occurs()) {
									// Maximum number of moving out households in demographic forecast already reached?
									if (hhMovedAwayMemberCount <  maxOutMigrationNational) {
										hhMovedAway++;
										hhMovedAwayMemberCount += household.getMemberCount();
										household.emigrate(dwellingsOnMarket, MigrationRealm.NATIONAL);
										dwelling = null;
										break;
									}
									// let the household find another alternative (?)
								} else {
									notMoving = true;
									break; // don't continue after moving probability check
								}
//							} else {
//								notMoving = true;
//								noDwellingFoundReason = NoDwellingFoundReason.NO_SUITABLE_DWELLING;
							}
						} else {
							dwelling = dwellingsOnMarket.getFirstMatchingDwelling(spatialUnitId, household, true, modelYear);
							if (dwelling != null) {
								if (Common.getMovingProbability().occurs()) {
									break;
								} else {
									notMoving = true;
									dwelling = null;
									break;	// don't continue after moving probability check
								}
							} else {
								if (dwellingsOnMarket.getNoDwellingFoundReason() != NoDwellingFoundReason.NO_SUITABLE_DWELLING) {
									noDwellingFoundReason = dwellingsOnMarket.getNoDwellingFoundReason();
								}
							}
//							int dwellingsConsideredPerCell = Common.getDwellingsConsideredPerCell();
//							for (int i = 0; i != dwellingsConsideredPerCell; i++) {
//								if (dwelling != null) {
//									if (Common.getMovingProbability().occurs()) {
//										break;
//									} else {
//										notMoving = true;
//										dwelling = null;
//										break;	// don't continue after moving probability check
//									}
//								} else {
//									dwelling = dwellingsOnMarket.getNextMatchingDwelling();
//									if (dwellingsOnMarket.getNoDwellingFoundReason() != NoDwellingFoundReason.NO_SUITABLE_DWELLING) {
//										noDwellingFoundReason = dwellingsOnMarket.getNoDwellingFoundReason();
//									}
//								}
//							}
						}
					}
					household.clearResidentialSatisfactionEstimate();
					if (dwelling != null) {
						household.relocate(dwellingsOnMarket, dwelling, MigrationRealm.LOCAL);
					} else {
						if (notMoving) {
							hhNotMoving++;
						} else {
							switch (noDwellingFoundReason) {
							case NO_SATISFACTION:
								hhNoSatisfaction++;
								break;
							default:
								hhFoundNoDwellings++;
								break;
							}
						}
					}
				}
				j++;
			}
			System.out.println(printInfo(modelRun) + ": " + hhNoAspiration + " out of " + potentialMovers.size() + " potential moving households found no spatial unit within their aspiration region. " + 
					"Out of these " + hhZeroIncome + " households can afford a dwelling with costs <= 0 " +
					"and " + hhLowIncome + " households can afford a dwelling with costs <= 63 per m²/year only");
			System.out.println(printInfo(modelRun) + ": " + hhNoSatisfaction + " out of " + potentialMovers.size() + " potential moving households would not improve their estimated residential satisfaction");
			System.out.println(printInfo(modelRun) + ": " + hhFoundNoDwellings + " out of " + potentialMovers.size() + " potential moving households found no dwelling");
			System.out.println(printInfo(modelRun) + ": " + hhMovedAway + " out of " + potentialMovers.size() + " potential moving households moved away (" + hhMovedAwayMemberCount + " persons)");
			System.out.println(printInfo(modelRun) + ": " + hhNotMoving + " out of " + potentialMovers.size() + " potential moving households decided not to move to a dwelling/an area that suited their needs");
	        System.out.println(printInfo(modelRun) + ": free dwellings after moving: " + dwellingsOnMarket.getFreeDwellingsCount());
			outputFreeDwellings(modelYear, "after moving households, before immigration");

			// households moving together
			int numMovingTogether = movingTogether.randomJoinHouseholds();
			System.out.println(printInfo(modelRun) + ": " + numMovingTogether + " of "+ movingTogetherCount + " projected household move-togethers/marriages took place");
	        System.out.println(printInfo(modelRun) + ": free dwellings after moving together: " + dwellingsOnMarket.getFreeDwellingsCount());
			
			// Out-Migration: randomly remove households
			int numOutMigrationInternational = sampleMigratingHouseholds.getOutMigrationInternational(modelYear) + sampleMigratingHouseholds.getOutMigrationNational(modelYear) - hhMovedAwayMemberCount;
			if (numOutMigrationInternational > 0) {
				int numOutMigrationIntlHouseholds = households.randomRemoveHouseholds(dwellingsOnMarket, numOutMigrationInternational, MigrationRealm.INTERNATIONAL);
				System.out.println(printInfo(modelRun) + ": " + numOutMigrationInternational + " persons (" + numOutMigrationIntlHouseholds + " households) out-migrated internationally");
			}
	        System.out.println(printInfo(modelRun) + ": free dwellings after out-migration: " + dwellingsOnMarket.getFreeDwellingsCount());
			
			// Immigrating households + Children moving out from home
			System.out.println(printInfo(modelRun) + ": generating immigrating households and new single households form children moving out of parents homes");
			ArrayList<HouseholdRow> childrenHouseholds = null;
			if (Common.isDemographyOnly() == false) {
				childrenHouseholds = leavingParents.getNewSingleHouseholds();
			}
			ArrayList<HouseholdRow> immigratingHouseholdsNational = sampleMigratingHouseholds.sample(modelYear, MigrationRealm.NATIONAL);
			ArrayList<HouseholdRow> immigratingHouseholdsIntl = sampleMigratingHouseholds.sample(modelYear, MigrationRealm.INTERNATIONAL);

			int dwellingExcessShare = Common.getDwellingsOnMarketAutoAdjust();
			if (dwellingExcessShare >= 0) {
				// Auto-adjust dwellings if necessary
				int dwellingsDemandCount = 0;
				if (Common.isDemographyOnly() == false) {
					dwellingsDemandCount = childrenHouseholds.size() + immigratingHouseholdsNational.size() + immigratingHouseholdsIntl.size();
				} else {
					dwellingsDemandCount = immigratingHouseholdsNational.size() + immigratingHouseholdsIntl.size();
				}
				int dwellingsAvailableCount = dwellingsOnMarket.getFreeDwellingsCount();
				int dwellingsExcessSupplyCount = (dwellingsDemandCount * (100 + dwellingExcessShare)) / 100;
				System.out.println(printInfo() + ": number of dwellings needed for immigration + children leaving parents: " + dwellingsDemandCount);
				System.out.println(printInfo() + ": number of total dwellings needed incl. excess: " + dwellingsExcessSupplyCount);
				System.out.println(printInfo() + ": number of dwellings available on the market: " + dwellingsAvailableCount);
				if (dwellingsExcessSupplyCount > dwellingsAvailableCount) {
					int dwellingsMissingCount = dwellingsExcessSupplyCount - dwellingsAvailableCount;
					newDwellings = sampleBuildingProjects.sampleRandomDwellings(dwellingsMissingCount);
			        dwellings.addAll(newDwellings);
			        dwellingsOnMarket.addAll(newDwellings);
				}
		        System.out.println(printInfo(modelRun) + ": free dwellings after auto-adjustment: " + dwellingsOnMarket.getFreeDwellingsCount());
			}

			// Now move the immigrating households + children leaving parents
			if (Common.isDemographyOnly() == false) {
				forcedMoves(childrenHouseholds, modelYear, modelStartYear, highestYearlyRentPer100Sqm, residentialMobility, MigrationRealm.LEAVING_PARENTS, cheapestSpatialUnits);
		        System.out.println(printInfo(modelRun) + ": free dwellings after " + childrenHouseholds.size() + " children leaving parents homes: " + dwellingsOnMarket.getFreeDwellingsCount());
			}
			forcedMoves(immigratingHouseholdsNational, modelYear, modelStartYear, highestYearlyRentPer100Sqm, residentialMobility, MigrationRealm.NATIONAL, cheapestSpatialUnits);
	        System.out.println(printInfo(modelRun) + ": free dwellings after " + immigratingHouseholdsNational.size() + " immigrating households (national): " + dwellingsOnMarket.getFreeDwellingsCount());
			forcedMoves(immigratingHouseholdsIntl, modelYear, modelStartYear, highestYearlyRentPer100Sqm, residentialMobility, MigrationRealm.INTERNATIONAL, cheapestSpatialUnits);
	        System.out.println(printInfo(modelRun) + ": free dwellings after " + immigratingHouseholdsIntl.size() + " immigrating households (international): " + dwellingsOnMarket.getFreeDwellingsCount());
		
			//if (modelYear == modelEndYear - 1)
			outputFreeDwellings(modelYear, "after immigration");
			outputMigrationCount(modelYear);
			outputDemographicMovementCount(modelYear);
			outputMigrationDetailsCount(modelYear);
			
			// Aging of persons (household-wise)
			households.aging();
		} // model year
	}
	public static void forcedMoves(ArrayList<HouseholdRow> dwellingSeekers, int modelYear, int modelStartYear, int highestYearlyRentPer100Sqm, ResidentialMobility residentialMobility, MigrationRealm migrationRealm, ArrayList<Integer> cheapestSpatialUnits) {
		for (HouseholdRow household : dwellingSeekers) {
			// Count potential movers (TODO: should be implemented over an interface too!)
			switch (migrationRealm) {
			case LEAVING_PARENTS:
				migrationPerSpatialUnit.addPotentialLeftParentsOriginCount(household.getDwelling().getSpatialunit().getSpatialUnitId(), 1);
				break;
			case NATIONAL:
			case INTERNATIONAL:
				// TODO: doesn't work because spatialUnit is not known in advance for an immigrant household
//				migrationPerSpatialUnit.addPotentialImmigrationCounters(household.getDwelling().getSpatialunit().getSpatialUnitId(), 1, household.getMemberCount(), migrationRealm);
				break;
			default:
				throw new IllegalArgumentException("Unexpected migration realm " + migrationRealm.toString());
			}
			residentialMobility.estimateAspirationRegion(household, modelYear, modelStartYear, highestYearlyRentPer100Sqm);
			DwellingRow dwelling = dwellingsOnMarket.getFirstMatchingDwelling((short) 0, household.getAspirationRegionLivingSpaceMax(), household.getAspirationRegionMaxCosts());
			if (dwelling != null) {
				// 1) add household to common lists
				assert household.getMembers() != null : "No household members found";
				households.add(household);
				for (PersonRow member : household.getMembers()) {
					assert member.getHousehold() != null : "No household for person found";
					persons.add(member);
				}
				// 2 move household
				switch (migrationRealm) {
				case LEAVING_PARENTS:
					household.leaveParentsHome(dwellingsOnMarket, dwelling);
					break;
				case NATIONAL:
				case INTERNATIONAL:
					household.relocate(dwellingsOnMarket, dwelling, migrationRealm);
					break;
				default:
					throw new IllegalArgumentException("Unexpected migration realm " + migrationRealm.toString());
				}
				assert household.getDwelling() != null : "No dwelling for household found";
			} else {
				for (long spatialUnitId : cheapestSpatialUnits) {
					dwelling = dwellingsOnMarket.getFirstMatchingDwelling(spatialUnitId, (short) 0, household.getAspirationRegionLivingSpaceMax());
					if (dwelling != null)
						break;
				}
				if (dwelling != null) {
					// 1) add household to common lists
					assert household.getMembers() != null : "No household members found";
					households.add(household);
					for (PersonRow member : household.getMembers()) {
						assert member.getHousehold() != null : "No household for person found";
						persons.add(member);
					}
					// 2 move household
					switch (migrationRealm) {
					case LEAVING_PARENTS:
						household.leaveParentsHome(dwellingsOnMarket, dwelling);
						break;
					case NATIONAL:
					case INTERNATIONAL:
						household.relocate(dwellingsOnMarket, dwelling, migrationRealm);
						break;
					default:
						throw new IllegalArgumentException("Unexpected migration realm " + migrationRealm.toString());
					}
					assert household.getDwelling() != null : "No dwelling for household found";
				}
			}
		}
	}
	/**
	 * Rebuild Household Indicators (AllHouseholdsIndicatorManager) 
	 */
	public static void buildIndicators() {
		AllHouseholdsIndicatorManager.resetIndicators();
		PercentileIndicatorManager.resetIndicators();
		for (HouseholdRow household : households) {
			AllHouseholdsIndicatorManager.addHousehold(household);
			PercentileIndicatorManager.addHousehold(household);
		}
	}

	public static String createPathName(String fileName) {
		String path = Common.getPathOutput();
		if (path == null) path = "";
		String pathName;
		if (path.endsWith("\\") || (path == ""))
			pathName = path + fileName;
		else
			pathName = path + "\\" + fileName;
		return pathName;
	}
	
	private static String freeDwellingsFileName = "FreeDwellings.csv";
	private static String migrationCountFileName = "Migrations.csv";
	private static String migrationDetailsCountFileName = "MigrationDetails.csv";
	private static String demographicMovementsFileName = "DemographicMovements.csv";
	
	public static void initSimpleOutputFiles() {
		String pathName = createPathName(freeDwellingsFileName);
		// TODO: put this in an extra class and delete the file once per model run
		FileUtil.rotateFile(pathName);
		pathName = createPathName(migrationCountFileName);
		FileUtil.rotateFile(pathName);
		pathName = createPathName(demographicMovementsFileName);
		FileUtil.rotateFile(pathName);
		pathName = createPathName(migrationDetailsCountFileName);
		FileUtil.rotateFile(pathName);
	}
	
	public static void outputFreeDwellings(int modelYear, String label) throws FileNotFoundException {
		String pathName = createPathName(freeDwellingsFileName);
		// TODO: put this in an extra class and delete the file once per model run
		FileOutputStream freeDwellingsFile = new FileOutputStream(pathName, true);
		PrintStream ps = new PrintStream(freeDwellingsFile);
//		ps.println("======= " + label + "=======" + modelYear + "=======");
		dwellingsOnMarket.outputDwellingsPerSize(ps, modelYear, label);
	}
	
	/**
	 * Output the migration count - this can't be done within the FileOutput class
	 * unless this class supports the specification of when the output shall happen.
	 * The migration counts shall be output at the end of each model year, which is different to
	 * the timing of the other outputs...
	 * 
	 * @param modelYear
	 * @throws FileNotFoundException
	 */
	public static void outputMigrationCount(int modelYear) throws FileNotFoundException {
		String pathName = createPathName(migrationCountFileName);
		FileOutputStream migrationCountFile= new FileOutputStream(pathName, true);
		PrintStream ps = new PrintStream(migrationCountFile);
		migrationPerSpatialUnit.output(ps, modelYear);
	}
	public static void outputDemographicMovementCount(int modelYear) throws FileNotFoundException {
		String pathName = createPathName(demographicMovementsFileName);
		FileOutputStream demographicMovementCountFile= new FileOutputStream(pathName, true);
		PrintStream ps = new PrintStream(demographicMovementCountFile);
		demographicMovementsPerSpatialUnit.output(ps, modelYear);
	}
	public static void outputMigrationDetailsCount(int modelYear) throws FileNotFoundException {
		String pathName = createPathName(migrationDetailsCountFileName);
		FileOutputStream outputFile = new FileOutputStream(pathName, true);
		PrintStream ps = new PrintStream(outputFile);
		migrationDetails.output(ps, modelYear);
	}
}
