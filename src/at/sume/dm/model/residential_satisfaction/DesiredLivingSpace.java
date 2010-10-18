/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.indicators.IndicatorsPerHouseholdTypeAndIncome;
import at.sume.dm.types.IncomeGroup;

/**
 * Calculate the living space dimension of residential satisfaction based on the average living space for the same
 * household type and income class
 * 
 * @author Alexander Remesch
 *
 */
public class DesiredLivingSpace extends ResidentialSatisfactionComponent {
	/* (non-Javadoc)
	 * @see at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionComponent#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.SpatialUnitRow, int)
	 */
	@Override
	public long calc(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		// TODO: add household-specific desiredLivingSpace modifier here
		long desiredLivingSpace = IndicatorsPerHouseholdTypeAndIncome.getAvgLivingSpacePerHousehold(hh.getHouseholdType(), IncomeGroup.getIncomeGroupId(hh.getYearlyIncome()));
		long currentLivingSpace = hh.getLivingSpace();
		assert desiredLivingSpace > 0 : "Desired living space <= 0 (" + desiredLivingSpace + ") for household " + hh.getId();
		assert currentLivingSpace > 0 : "Current living space <= 0 (" + currentLivingSpace + ") for household " + hh.getId();
		if (desiredLivingSpace >= currentLivingSpace)
			return 1000;
		else
			return (desiredLivingSpace * 1000) / currentLivingSpace;
	}
}
