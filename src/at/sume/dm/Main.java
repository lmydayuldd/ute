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
import java.util.List;
import java.util.Random;

import net.remesch.db.Database;
import net.remesch.db.Sequence;
import net.remesch.util.DateUtil;
import net.remesch.util.FileUtil;
import at.sume.dm.buildingprojects.SampleBuildingProjects;
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
import at.sume.dm.indicators.simple.CountDemographicMovements;
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
	private static CountDemographicMovements demographicMovementsPerSpatialUnit;

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
			spatialUnits = new SpatialUnits(db, Common.getSpatialUnitLevel());
	        System.out.println(printInfo() + ": loaded " + spatialUnits.size() + " spatial units");
			dwellings = new Dwellings(db, Common.getSpatialUnitLevel());
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

        // create migration counter + register with households (once!)
        migrationPerSpatialUnit = new CountMigrationPerSpatialUnit();
        // TODO: find a way to make this registration out of the MigrationPerSpatialUnit() class directly (but only once!!!)
        households.get(0).registerMigrationObserver(migrationPerSpatialUnit);
        demographicMovementsPerSpatialUnit = new CountDemographicMovements();
        persons.get(0).registerDemographyObserver(demographicMovementsPerSpatialUnit);
        
        // get all dwellings on the housing market
        dwellingsOnMarket = new DwellingsOnMarket(dwellings, spatialUnits);
        System.out.println(printInfo() + ": determined all available dwellings on the housing market");
        // determine household-types
        households.determineHouseholdTypes();
        System.out.println(printInfo() + ": determined all household types");

        List<List<? extends Fileable>> fileableList = new ArrayList<List<? extends Fileable>>();
        List<String> fileNameList = new ArrayList<String>();
        fileableList.add(households.getRowList());
        fileNameList.add("Households");
        fileableList.add(dwellings.getRowList());
        fileNameList.add("Dwellings");
        fileableList.add(persons.getRowList());
        fileNameList.add("Persons");
        fileableList.add(RentPerSpatialUnit.getRentPerSpatialUnit());
        fileNameList.add("RentPerSpatialUnit");
        fileableList.add(AllHouseholdsIndicatorManager.INDICATORS_PER_HOUSEHOLDTYPE_AND_INCOME.getIndicator().getIndicatorList());
        fileNameList.add("IndicatorsPerHouseholdTypeAndIncome");
        fileableList.add(AllHouseholdsIndicatorManager.AGGREGATED_HOUSEHOLDS.getIndicator().getIndicatorList());
        fileNameList.add("AggregatedHouseholds");
        fileableList.add(AllHouseholdsIndicatorManager.AGGREGATED_PERSONS.getIndicator().getIndicatorList());
        fileNameList.add("AggregatedPersons");
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
		PersonDeath personDeath = new PersonDeath(db, personEventManager, Common.getPersonMaxAge());
		@SuppressWarnings("unused")
		ChildBirth childBirth = new ChildBirth(db, personEventManager);
		// TODO: include scenario handling!!!
		SampleMigratingHouseholds sampleMigratingHouseholds = new SampleMigratingHouseholds("STATA2010");
		SampleBuildingProjects sampleBuildingProjects = new SampleBuildingProjects("BASE", spatialUnits);
		
		EntityDecisionManager<HouseholdRow, Households> householdDecisionManager = new EntityDecisionManager<HouseholdRow, Households>();
		MinimumIncome minimumIncome = new MinimumIncome(db, householdDecisionManager, households);
		ResidentialMobility residentialMobility = new ResidentialMobility(minimumIncome);
		int modelStartYear = Common.getModelStartYear();
		int modelEndYear = modelStartYear + iterations;
		for (int modelYear = modelStartYear; modelYear != modelEndYear; modelYear++) {
			// Clear migration indicators
			migrationPerSpatialUnit.clearIndicatorList();
			demographicMovementsPerSpatialUnit.clearIndicatorList();
	        // (Re)build household indicators - this must be done each model year because with add/remove it is a problem when the age of a person changes
			buildIndicators();			
	        System.out.println(printInfo() + ": build of model indicators complete");
	        outputManager.output((short) modelYear);
	        System.out.println(printInfo() + ": model data output to database");
	        AllHouseholdsIndicatorManager.outputIndicators(modelYear);

	        // Create new-built dwellings
	        List<DwellingRow> newDwellings = sampleBuildingProjects.sample(modelYear);
	        dwellings.addAll(newDwellings);
	        dwellingsOnMarket.addAll(newDwellings);
	        
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
				
				j++;
			}
			// Update rent prices for each spatial unit from last years data (from the movers indicators)
			// from the second year on
//			CostEffectiveness costEffectiveness = (CostEffectiveness)ResidentialSatisfactionManager.COSTEFFECTIVENESS.getComponent();
			// TODO: shouldn't this be done after the movings??? But we need fresh movers indicators...
			if (modelYear > modelStartYear)
				RentPerSpatialUnit.updateRentPerSpatialUnit(spatialUnits);
			// TODO: configurable number of units
			ArrayList<Integer> cheapestSpatialUnits = RentPerSpatialUnit.getCheapestSpatialUnits(0);
			int lowestYearlyRentPer100Sqm = RentPerSpatialUnit.getLowestYearlyRentPer100Sqm();
			int highestYearlyRentPer100Sqm = RentPerSpatialUnit.getHighestYearlyRentPer100Sqm();
			System.out.println(printInfo() + ": lowest rent (€/100m²/yr.): " + lowestYearlyRentPer100Sqm + ", highest rent: " + highestYearlyRentPer100Sqm);
			// Reset the movers indicators
			MoversIndicatorManager.resetIndicators();
			outputFreeDwellings(modelYear, "before moving households + after demographic changes");
			// Loop through potential movers
			int hhFoundNoDwellings = 0, hhNoSatisfaction = 0, hhNoAspiration = 0, hhZeroIncome = 0, hhLowIncome = 0, hhMovedAway = 0;
			int hhNotMoving = 0;
			j = 0;
			for (HouseholdRow household : potentialMovers) {
				boolean notMoving = false;
				if (j % 10000 == 0) {
					System.out.println(printInfo() + ": Processing potential mover " + j + " of " + potentialMovers.size() + " in year " + modelYear);
				}
				// 1) estimate the aspiration region: 
				//    a) needed living space - by household size 
				//    b) maximum costs - by current costs, household income
				// lower limit: commonly defined by the current dwelling; upper limit: set by the standards to
				// which the household can reasonably aspire (Knox/Pinch 2010, p.263)
				residentialMobility.estimateAspirationRegion(household, modelYear, modelStartYear, highestYearlyRentPer100Sqm);
				
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
					DwellingRow dwelling = null;
					ArrayList<SpatialUnitRow> potentialTargetSpatialUnits = spatialUnits.getSpatialUnits(potentialTargetSpatialUnitIds);
					household.estimateResidentialSatisfaction(potentialTargetSpatialUnits, modelYear, Common.getResidentialSatisfactionEstimateRange());
					ArrayList<Integer> preferredSpatialUnits = household.getPreferredSpatialUnits();
					NoDwellingFoundReason noDwellingFoundReason = NoDwellingFoundReason.NO_REASON;
					for (int spatialUnitId : preferredSpatialUnits) {
						SpatialUnitRow spatialUnit = spatialUnits.getSpatialUnit(spatialUnitId);
						if (spatialUnit.isFreeDwellingsAlwaysAvailable()) {
							// Household moves away
							if (Common.getMovingOutProbability().occurs()) {
								hhMovedAway++;
								household.emigrate(dwellingsOnMarket, MigrationRealm.NATIONAL);
								dwelling = null;
								break;
							} else {
								notMoving = true;
							}
						} else {
							dwelling = dwellingsOnMarket.getFirstMatchingDwelling(spatialUnitId, household, true, modelYear);
							if (dwelling != null) {
								if (Common.getMovingProbability().occurs()) {
									break;
								} else {
									notMoving = true;
									dwelling = null;
								}
							} else {
								if (dwellingsOnMarket.getNoDwellingFoundReason() != NoDwellingFoundReason.NO_SUITABLE_DWELLING) {
									noDwellingFoundReason = dwellingsOnMarket.getNoDwellingFoundReason();
								}
							}
						}
					}
					household.clearResidentialSatisfactionEstimate();
					if (dwelling != null) {
						household.relocate(dwellingsOnMarket, dwelling);
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
			System.out.println(printInfo() + ": " + hhNoAspiration + " out of " + potentialMovers.size() + " potential moving households found no spatial unit within their aspiration region. " + 
					"Out of these " + hhZeroIncome + " households can afford a dwelling with costs <= 0 " +
					"and " + hhLowIncome + " households can afford a dwelling with costs <= 63 per m²/year only");
			System.out.println(printInfo() + ": " + hhNoSatisfaction + " out of " + potentialMovers.size() + " potential moving households would not improve their estimated residential satisfaction");
			System.out.println(printInfo() + ": " + hhFoundNoDwellings + " out of " + potentialMovers.size() + " potential moving households found no dwelling");
			System.out.println(printInfo() + ": " + hhMovedAway + " out of " + potentialMovers.size() + " potential moving households moved away");
			System.out.println(printInfo() + ": " + hhNotMoving + " out of " + potentialMovers.size() + " potential moving households decided not to move to a dwelling/an area that suited their needs");
			outputFreeDwellings(modelYear, "after moving households, before immigration");
			// Immigrating Households
			ArrayList<HouseholdRow> immigratingHouseholds = sampleMigratingHouseholds.sample(modelYear);
			hhFoundNoDwellings = 0; hhNoSatisfaction = 0;
			int hhMovedToCheapest = 0, hhNoCheapDwelling = 0, migratingHouseholds = 0, migratingPersons = 0;
			for (HouseholdRow household : immigratingHouseholds) {
				residentialMobility.estimateAspirationRegion(household, modelYear, modelStartYear, highestYearlyRentPer100Sqm);
				if (household.getAspirationRegionMaxCosts() * 100 < lowestYearlyRentPer100Sqm) {
					// Household can't afford even the lowest rent -> we choose a dwelling in one of the cheapest spatial units available
					DwellingRow dwelling = null;
					for (long spatialUnitId : cheapestSpatialUnits) {
						dwelling = dwellingsOnMarket.getFirstMatchingDwelling(spatialUnitId, (short) 0, household.getAspirationRegionLivingSpaceMax());
						if (dwelling != null)
							break;
					}
//					assert dwelling != null : "No dwelling found";
					if (dwelling == null) {
						hhNoCheapDwelling++;
//						for (long spatialUnitId : cheapestSpatialUnits) {
//							dwelling = dwellingsOnMarket.getFirstMatchingDwelling(spatialUnitId, (short) 0, household.getAspirationRegionLivingSpaceMax());
//							if (dwelling != null)
//								break;
//						}
					} else {
						// 1) add household to common lists
						households.add(household);
						for (PersonRow member : household.getMembers()) {
							persons.add(member);
						}
						// 2 move household
						household.relocate(dwellingsOnMarket, dwelling);
						hhMovedToCheapest++;
						migratingHouseholds++;
						migratingPersons += household.getMemberCount();
						// TODO: update indicators
					}
				} else {
					DwellingRow dwelling = null;
					household.estimateResidentialSatisfaction(spatialUnits.getRowList(), modelYear, Common.getResidentialSatisfactionEstimateRange());
					ArrayList<Integer> preferredSpatialUnits = household.getPreferredSpatialUnits();
					NoDwellingFoundReason noDwellingFoundReason = NoDwellingFoundReason.NO_REASON;
					for (int spatialUnitId : preferredSpatialUnits) {
						SpatialUnitRow spatialUnit = spatialUnits.getSpatialUnit(spatialUnitId);
						if (!spatialUnit.isFreeDwellingsAlwaysAvailable()) {
//							dwelling = dwellingsOnMarket.getFirstMatchingDwelling(spatialUnitId, household, false, modelYear);
							dwelling = dwellingsOnMarket.getFirstMatchingDwelling(spatialUnitId, (short) 0, household.getAspirationRegionLivingSpaceMax());
							if (dwelling != null) {
								break;
							} else {
								if (dwellingsOnMarket.getNoDwellingFoundReason() != NoDwellingFoundReason.NO_SUITABLE_DWELLING) {
									noDwellingFoundReason = dwellingsOnMarket.getNoDwellingFoundReason();
								}
							}
						}
					}
					household.clearResidentialSatisfactionEstimate();
					if (dwelling == null) {
						// household didn't find a suitable dwelling -> join another household
						// TODO: update indicators
						switch (noDwellingFoundReason) {
						case NO_SATISFACTION:
							hhNoSatisfaction++;
							break;
						default:
							hhFoundNoDwellings++;
							break;
						}
					} else {
						// 1) add household to common lists
						households.add(household);
						for (PersonRow member : household.getMembers()) {
							persons.add(member);
						}
						// 2 move household
						household.relocate(dwellingsOnMarket, dwelling);
						migratingHouseholds++;
						migratingPersons += household.getMemberCount();
						// TODO: update indicators
					}
				}
			}
			System.out.println(printInfo() + ": a total of " + migratingPersons + " persons (" + migratingHouseholds + " households) immigrated in " + modelYear);
			System.out.println(printInfo() + ": " + hhFoundNoDwellings + " out of " + immigratingHouseholds.size() + " immigrating households found no dwelling");
			System.out.println(printInfo() + ": " + hhNoCheapDwelling + " out of " + immigratingHouseholds.size() + " immigrating households found no low-cost dwelling");
			System.out.println(printInfo() + ": " + hhNoSatisfaction + " out of " + immigratingHouseholds.size() + " immigrating households could not find spatial units matching their aspirations");
			System.out.println(printInfo() + ": " + hhMovedToCheapest + " out of " + immigratingHouseholds.size() + " immigrating households moved to the cheapest possible spatial units");
			
			// Out-Migration: randomly remove households
			int numOutMigrationInternational = sampleMigratingHouseholds.getOutMigrationInternational(modelYear) + sampleMigratingHouseholds.getOutMigrationNational(modelYear) - hhMovedAway;
			if (numOutMigrationInternational > 0) {
				int numOutMigrationIntlHouseholds = households.randomRemoveHouseholds(dwellingsOnMarket, numOutMigrationInternational, MigrationRealm.INTERNATIONAL);
				System.out.println(printInfo() + ": " + numOutMigrationInternational + " persons (" + numOutMigrationIntlHouseholds + " households) out-migrated internationally");
			}
			
			//if (modelYear == modelEndYear - 1)
			outputFreeDwellings(modelYear, "after immigration");
			outputMigrationCount(modelYear);
			outputDemographicMovementCount(modelYear);
			
			// Aging of persons (household-wise)
			households.aging();
		} // model year
	}
	/**
	 * Rebuild Household Indicators (AllHouseholdsIndicatorManager) 
	 */
	public static void buildIndicators() {
		AllHouseholdsIndicatorManager.resetIndicators();
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
	private static String demographicMovementsFileName = "DemographicMovements.csv";
	
	public static void initSimpleOutputFiles() {
		String pathName = createPathName(freeDwellingsFileName);
		// TODO: put this in an extra class and delete the file once per model run
		FileUtil.rotateFile(pathName);
		pathName = createPathName(migrationCountFileName);
		FileUtil.rotateFile(pathName);
		pathName = createPathName(demographicMovementsFileName);
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
}
