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
	public double calc(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		// TODO: add household-specific desiredLivingSpace modifier here
		double desiredLivingSpace = IndicatorsPerHouseholdTypeAndIncome.getAvgLivingSpacePerHousehold(hh.getHouseholdType(), IncomeGroup.getIncomeGroupId(hh.getYearlyIncome()));
		double currentLivingSpace = hh.getLivingSpace();
		if (desiredLivingSpace >= currentLivingSpace)
			return 1;
		else
			return desiredLivingSpace / currentLivingSpace;
	}
}
