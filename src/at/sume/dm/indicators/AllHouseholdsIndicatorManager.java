/**
 * 
 */
package at.sume.dm.indicators;

import at.sume.dm.entities.HouseholdRow;

/**
 * This class represents indicators that include all households in the model 
 *  
 * @author Alexander Remesch
 */
public enum AllHouseholdsIndicatorManager {
	// TODO: implement a means to list the indicator-getters included in one of the Indicator implementations
	//       to be able to build menus directly from the indicator classes
	INDICATORS_PER_SPATIALUNIT("Indicators per spatial unit", new AllHouseholdsIndicatorsPerSpatialUnit()),
	INDICATORS_PER_HOUSEHOLDTYPE_AND_INCOME("Indicators per household type and income class", new AllHouseholdsIndicatorsPerHouseholdTypeAndIncome());
	
	private String label;
	private Indicator<HouseholdRow> indicator;

	AllHouseholdsIndicatorManager(String label, Indicator<HouseholdRow> indicator) {
		this.label = label;
		this.indicator = indicator;
	}
	
	public static void addHousehold(HouseholdRow household) {
		for (AllHouseholdsIndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.add(household);
		}
	}
	
	public static void removeHousehold(HouseholdRow household) {
		for (AllHouseholdsIndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.remove(household);
		}
	}
	
	public static void resetIndicators() {
		for (AllHouseholdsIndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.clear();
		}
	}
	
	public Indicator<HouseholdRow> getIndicator() {
		return indicator;
	}
}
