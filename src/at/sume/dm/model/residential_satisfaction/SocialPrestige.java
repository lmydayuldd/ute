/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.indicators.IndicatorsPerSpatialUnit;

/**
 * Calculate the conformity level of the social prestige of a spatial unit and a household
 * by using the average household income per household member vs. the actual household income per member
 * as the indicator
 * 
 * @author Alexander Remesch
 *
 */
public class SocialPrestige extends ResidentialSatisfactionComponent {
	/* (non-Javadoc)
	 * @see at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionComponent#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.SpatialUnitRow)
	 */
	@Override
	public double calc(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		// Calculate household income per member
		long hhIncome = hh.getYearlyIncomePerMemberWeighted();
		// Calculate average household income per member
		long avgIncome = IndicatorsPerSpatialUnit.getAvgHouseholdIncomePerMemberWeighted(su.getId());
		
		// income only influences residential satisfaction if the neighborhood income is lower
		// TODO: individual threshold for this comparison?!?
		if (hhIncome > avgIncome) {
			// satisfaction = percentage of avg. income to household income
			return avgIncome / hhIncome;
		} else {
			// 100% satisfaction here
			return 1;
		}
	}
}
