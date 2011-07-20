/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

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
	public short calc(ResidentialSatisfactionHouseholdProperties household, ResidentialSatisfactionDwellingProperties dwelling, SpatialUnitRow spatialUnit, int modelYear) {
		long currentLivingSpace = 0;
		short result = 0;
		// TODO: add household-specific desiredLivingSpace modifier here
		household.estimateDesiredLivingSpace();
		long desiredLivingSpace = (household.getAspirationRegionLivingSpaceMin() + household.getAspirationRegionLivingSpaceMax()) / 2;
		assert desiredLivingSpace > 0 : "Desired living space <= 0 (" + desiredLivingSpace + ")";
		if (!household.hasDwelling()) {
			if (dwelling == null) {
				// Household has no dwelling and no alternative dwelling was given -> currentLivingSpace stays = 0
			} else {
				// Household has no dwelling but alternative dwelling was given that will be used for currentLivingSpace calculation
				currentLivingSpace = dwelling.getDwellingSize();
			}
		} else {
			if ((dwelling == null) || (dwelling == household.getDwelling())) {
				// Calculate living space satisfaction for the household's own dwelling (no other dwelling was given)
				// or for a dwelling with the current's size in another spatial unit
				currentLivingSpace = household.getDwelling().getDwellingSize();
			} else {
				currentLivingSpace = dwelling.getDwellingSize();
			}
		}
		assert currentLivingSpace >= 0 : "Current living space < 0 (" + currentLivingSpace + ")";
		// TODO: should the satisfaction with the living space go down again if the dwelling is much larger than desired?
		if (currentLivingSpace != 0) {
//			if (currentLivingSpace <= desiredLivingSpace) {
				result = (short) Math.round(currentLivingSpace * 1000 / desiredLivingSpace);
//			} else {
//				// Satisfaction goes down again if the dwelling is too large
//				// TODO: define region where the difference doesn't matter as sysparam
//				result = (short) Math.round(desiredLivingSpace * 1000 / currentLivingSpace);
//			}
		} else {
			result = 0;
		}
		if (result > 1000)
			return 1000;
		assert result >= 0 : "rsDesiredLivingSpace out of range (" + result + ")";
		household.setRsDesiredLivingSpace(result);
		return result;
	}
}
