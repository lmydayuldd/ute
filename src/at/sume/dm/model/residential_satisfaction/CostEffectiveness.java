/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.model.residential_mobility.RentPerSpatialUnit;

/**
 * Calculate the cost-effectiveness dimension of residential satisfaction based on the anticipated costs of a new dwelling with
 * the same characteristics (currently the living space only) in the target spatial unit.
 * 
 * The yearly average rent per spatial unit used here is derived from the table WKO_Mietpreise for the first
 * model year and later on the committed sales are used to determine the rents in each model year.
 * 
 * @author Alexander Remesch
 */
public class CostEffectiveness extends ResidentialSatisfactionComponent {

	/* (non-Javadoc)
	 * @see at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionComponent#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.DwellingRow, at.sume.dm.entities.SpatialUnitRow, int)
	 */
	@Override
	public short calc(HouseholdRow household, DwellingRow dwelling,
			SpatialUnitRow spatialUnitId, int ModelYear) {
		// TODO: add household-specific rentPerceptionModifier here, that may also increase over the years if the household
		// is unable to find a new residence for a long time (?)
		long potentialCostOfResidence = 0;
		long currentCostOfResidence = 0;
		short result = 0;
		if (!household.hasDwelling()) {
//			assert dwelling != null : "Household has no dwelling and no other dwelling was given for cost effectiveness calculation";
			if (dwelling == null) {
				// Household has no dwelling and no alternative dwelling was given
				// RS could be calculated by comparation with the aspiration region or could be simply set to 1000
				result = 1000;
			} else {
				// Household has no dwelling - CostEffectiveness must be calculated between cost of new dwelling considered and
				// potential cost of residence in the target area
				currentCostOfResidence = dwelling.getTotalYearlyDwellingCosts();
				potentialCostOfResidence = Math.round(dwelling.getDwellingSize() * RentPerSpatialUnit.getYearlyAverageRentPer100Sqm(spatialUnitId.getSpatialUnitId()));
				if (currentCostOfResidence <= 0)
					result = 1000;
				else
					result = (short) Math.round(potentialCostOfResidence * 1000 / currentCostOfResidence);
			}
		} else {
			if ((dwelling == null) || (dwelling == household.getDwelling())) {
				// Calculate cost effectiveness satisfaction for the household's own dwelling (no other dwelling was given)
				// compared to a dwelling with the current's size in another spatial unit
				currentCostOfResidence = household.getDwelling().getTotalYearlyDwellingCosts();
				potentialCostOfResidence = Math.round(dwelling.getDwellingSize() * RentPerSpatialUnit.getYearlyAverageRentPer100Sqm(spatialUnitId.getSpatialUnitId()));
				if (currentCostOfResidence <= 0)
					result = 1000;
				else
					result = (short) Math.round(potentialCostOfResidence * 1000 / currentCostOfResidence);
			} else {
				long costOfNewDwelling = dwelling.getTotalYearlyDwellingCosts();
				if (household.getDwelling() == null) {
					// Calculate cost effectiveness satisfaction for a given dwelling without considering the households current dwelling
					// There is no need to calculate this since new dwellings always get (about) the average price of the spatial unit
					// TODO: maybe we could calculate the price before and include some random part
					result = 1000;
				} else {
					// Calculate cost effectiveness satisfaction for a given dwelling with considering the households current dwelling
					currentCostOfResidence = household.getDwelling().getTotalYearlyDwellingCosts();
					result = (short) Math.round(currentCostOfResidence * 1000 / costOfNewDwelling);
				}
			}
		}
		if (result > 1000)
			return 1000;
		return result;
	}
}
