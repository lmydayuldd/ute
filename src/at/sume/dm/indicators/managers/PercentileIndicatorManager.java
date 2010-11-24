/**
 * 
 */
package at.sume.dm.indicators.managers;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.indicators.IncomePercentiles;
import at.sume.dm.indicators.base.Indicator;

/**
 * @author Alexander Remesch
 *
 */
public enum PercentileIndicatorManager {
	INCOME_PERCENTILES("Income percentiles", new IncomePercentiles());
	
	private String label;
	private Indicator<HouseholdRow> indicator;

	PercentileIndicatorManager(String label, Indicator<HouseholdRow> indicator) {
		this.label = label;
		this.indicator = indicator;
	}
	
	public static void addHousehold(HouseholdRow household) {
		for (PercentileIndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.add(household);
		}
	}
	
	public static void resetIndicators() {
		for (PercentileIndicatorManager indicatorManager : values()) {
			indicatorManager.indicator.clear();
		}
	}
	
	public Indicator<HouseholdRow> getIndicator() {
		return indicator;
	}
}
