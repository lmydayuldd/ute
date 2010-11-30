/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;

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
	public long calc(HouseholdRow household, DwellingRow dwelling, SpatialUnitRow spatialUnit, int modelYear) {
		// TODO: add household-specific desiredLivingSpace modifier here
		household.estimateDesiredLivingSpace();
		long desiredLivingSpace = (household.getAspirationRegionLivingSpaceMin() + household.getAspirationRegionLivingSpaceMax()) / 2;
		long currentLivingSpace = dwelling.getDwellingSize();
		assert desiredLivingSpace > 0 : "Desired living space <= 0 (" + desiredLivingSpace + ") for household " + household.getId();
		assert currentLivingSpace > 0 : "Current living space <= 0 (" + currentLivingSpace + ") for household " + household.getId();
		if (desiredLivingSpace <= currentLivingSpace)
			return 1000;
		else
			return (desiredLivingSpace * 1000) / currentLivingSpace;
	}
}
