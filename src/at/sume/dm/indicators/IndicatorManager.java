/**
 * 
 */
package at.sume.dm.indicators;

import at.sume.dm.entities.HouseholdRow;

/**
 * @author Alexander Remesch
 *
 */
public enum IndicatorManager {
	// TODO: implement a means to list the indicator-getters included in one of the Indicator implementations
	//       to be able to build menus directly from the indicator classes
//	PERSONCOUNT_PER_SPATIALUNIT("Number of persons per spatial unit", PersonCountPerSpatialUnit.class),
//	HOUSEHOLDCOUNT_PER_SPATIALUNIT("Number of households per spatial unit", HouseholdCountPerSpatialUnit.class),
	INCOME_PER_SPATIALUNIT("Income per spatial unit", new IndicatorsPerSpatialUnit());
	
	private String label;
	private Indicator indicator;

	IndicatorManager(String label, Indicator indicator) {
		this.label = label;
		this.indicator = indicator;
	}
	
	public static void addHousehold(HouseholdRow household) {
		for (IndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.add(household);
		}
	}
	
	public static void removeHousehold(HouseholdRow household) {
		for (IndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.remove(household);
		}
	}
	
	public static void resetIndicators() {
		for (IndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.clear();
		}
	}
	
	public Indicator getIndicator() {
		return indicator;
	}
}
