/**
 * 
 */
package at.sume.dm.indicators.managers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.indicators.AggregatedHouseholds;
import at.sume.dm.indicators.AggregatedPersons;
import at.sume.dm.indicators.AllHouseholdsIndicatorsPerHouseholdTypeAndIncome;
import at.sume.dm.indicators.AllHouseholdsIndicatorsPerSpatialUnit;
import at.sume.dm.indicators.PopulationPerAgeGroup;
import at.sume.dm.indicators.base.Indicator;
import at.sume.dm.indicators.base.IndicatorBase;

/**
 * This class represents indicators that include all households in the model 
 *  
 * @author Alexander Remesch
 */
public class AllHouseholdsIndicatorManager {
	// TODO: implement a means to list the indicator-getters included in one of the Indicator implementations
	//       to be able to build menus directly from the indicator classes
	public static final AllHouseholdsIndicatorManager INDICATORS_PER_SPATIALUNIT = new AllHouseholdsIndicatorManager("Indicators per spatial unit", new AllHouseholdsIndicatorsPerSpatialUnit());
	public static final AllHouseholdsIndicatorManager INDICATORS_PER_HOUSEHOLDTYPE_AND_INCOME = new AllHouseholdsIndicatorManager("Indicators per household type and income class", new AllHouseholdsIndicatorsPerHouseholdTypeAndIncome());
	public static final AllHouseholdsIndicatorManager POPULATION_PER_AGEGROUP = new AllHouseholdsIndicatorManager("Population per age group and spatial unit", new PopulationPerAgeGroup(), Common.getPathOutput() + "population.txt");
	public static final AllHouseholdsIndicatorManager AGGREGATED_HOUSEHOLDS = new AllHouseholdsIndicatorManager("Aggregated Households", new AggregatedHouseholds());
	public static final AllHouseholdsIndicatorManager AGGREGATED_PERSONS = new AllHouseholdsIndicatorManager("Aggregated Persons", new AggregatedPersons());
	
	private static ArrayList<AllHouseholdsIndicatorManager> values; 
	
//	private String label;
	private Indicator<HouseholdRow> indicator;
	private IndicatorBase<?> indicatorBase;

	AllHouseholdsIndicatorManager(String label, Indicator<HouseholdRow> indicator) {
//		this.label = label;
		this.indicator = indicator;
		if (values == null) {
			values = new ArrayList<AllHouseholdsIndicatorManager>();
		}
		values.add(this);
	}
	
	AllHouseholdsIndicatorManager(String label, IndicatorBase<?> indicatorBase, String outputFileName) {
//		this.label = label;
		this.indicator = null;
		this.indicatorBase = indicatorBase;
		indicatorBase.setOutputFileName(outputFileName);
		try {
			indicatorBase.initOutputFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		values.add(this);
	}
	
	public static void addHousehold(HouseholdRow household) {
		for (AllHouseholdsIndicatorManager indicatorManager : values) {
			if (indicatorManager.indicator == null)
				indicatorManager.indicatorBase.add(household);
			else
				indicatorManager.indicator.add(household);
		}
	}
	
	public static void removeHousehold(HouseholdRow household) {
		for (AllHouseholdsIndicatorManager indicatorManager : values) {
			if (indicatorManager.indicator == null)
				indicatorManager.indicatorBase.remove(household);
			else
				indicatorManager.indicator.remove(household);
		}
	}
	
	public static void resetIndicators() {
		for (AllHouseholdsIndicatorManager indicatorManager : values) {
			if (indicatorManager.indicator == null)
				indicatorManager.indicatorBase.clear();
			else
				indicatorManager.indicator.clear();
		}
	}
	
	public Indicator<HouseholdRow> getIndicator() {
		return indicator;
	}
	
	public static void outputIndicators(int modelYear) throws FileNotFoundException, IOException {
		for (AllHouseholdsIndicatorManager indicatorManager : values) {
			if (indicatorManager.indicatorBase != null)
				indicatorManager.indicatorBase.outputIndicatorData(modelYear);
		}
	}
}
