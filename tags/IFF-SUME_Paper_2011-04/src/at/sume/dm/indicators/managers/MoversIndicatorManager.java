/**
 * 
 */
package at.sume.dm.indicators.managers;

import java.util.ArrayList;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.indicators.MoversIndicatorsPerSpatialUnit;
import at.sume.dm.indicators.base.Indicator;

/**
 * This class is used mainly as a base for the yearly recalculation of rents per spatial unit.
 * 
 * @author Alexander Remesch
 */
public class MoversIndicatorManager {
	public static final MoversIndicatorManager INDICATORS_PER_SPATIALUNIT = new MoversIndicatorManager("Indicators per spatial unit", new MoversIndicatorsPerSpatialUnit());

	private static ArrayList<MoversIndicatorManager> values; 

//	private String label;
	private Indicator<HouseholdRow> indicator;

	MoversIndicatorManager(String label, Indicator<HouseholdRow> indicator) {
//		this.label = label;
		this.indicator = indicator;
		if (values == null) {
			values = new ArrayList<MoversIndicatorManager>();
		}
		values.add(this);
	}
	
	public static void addHousehold(HouseholdRow household) {
		for (MoversIndicatorManager indicatorManager : values) {
			indicatorManager.indicator.add(household);
		}
	}
	
	public static void removeHousehold(HouseholdRow household) {
		for (MoversIndicatorManager indicatorManager : values) {
			indicatorManager.indicator.remove(household);
		}
	}
	
	public static void resetIndicators() {
		for (MoversIndicatorManager indicatorManager : values) {
			indicatorManager.indicator.clear();
		}
	}
	
	public Indicator<HouseholdRow> getIndicator() {
		return indicator;
	}
}
