/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.indicators.AllHouseholdsIndicatorsPerSpatialUnit;

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
	public long calc(HouseholdRow household, DwellingRow dwelling, SpatialUnitRow spatialUnit, int modelYear) {
		// Calculate household income per member
		long hhIncome = household.getYearlyIncomePerMemberWeighted();
		// Calculate average household income per member
		long avgIncome = AllHouseholdsIndicatorsPerSpatialUnit.getAvgHouseholdIncomePerMemberWeighted(spatialUnit.getId());
		
		// income only influences residential satisfaction if the neighborhood income is lower
		// TODO: individual threshold for this comparison?!?
		if ((hhIncome > avgIncome) && (hhIncome > 0)) {
			// satisfaction = percentage of avg. income to household income
			return (avgIncome * 1000) / hhIncome;
		} else {
			// 100% satisfaction here
			return 1000;
		}
	}
}
