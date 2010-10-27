/**
 * 
 */
package at.sume.dm.indicators;

import at.sume.dm.entities.HouseholdRow;

/**
 * @author Alexander Remesch
 *
 */
public enum MoversIndicatorManager {
	INDICATORS_PER_SPATIALUNIT("Indicators per spatial unit", new MoversIndicatorsPerSpatialUnit());

	private String label;
	private Indicator<HouseholdRow> indicator;

	MoversIndicatorManager(String label, Indicator<HouseholdRow> indicator) {
		this.label = label;
		this.indicator = indicator;
	}
	
	public static void addHousehold(HouseholdRow household) {
		for (MoversIndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.add(household);
		}
	}
	
	public static void removeHousehold(HouseholdRow household) {
		for (MoversIndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.remove(household);
		}
	}
	
	public static void resetIndicators() {
		for (MoversIndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.clear();
		}
	}
	
	public Indicator<HouseholdRow> getIndicator() {
		return indicator;
	}
}
