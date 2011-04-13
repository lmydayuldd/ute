/**
 * 
 */
package at.sume.dm.model.residential_mobility;

import java.util.ArrayList;

import at.sume.dm.Common;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionManager;

/**
 * @author Alexander Remesch
 *
 */
public class ResidentialMobility {
	private MinimumIncome minimumIncome;	// TODO: get rid of MinimumIncome here by changing estimateMinIncomeLeftForLiving() to an indicator
	
	public ResidentialMobility(MinimumIncome minimumIncome) {
		this.minimumIncome = minimumIncome;
	}
	public void estimateAspirationRegion(HouseholdRow household, int modelYear, int modelStartYear, int highestYearlyRentPer100Sqm) {
		// 1) estimate the aspiration region: 
		//    a) needed living space - by household size 
		//    b) maximum costs - by current costs, household income
		// lower limit: commonly defined by the current dwelling; upper limit: set by the standards to
		// which the household can reasonably aspire (Knox/Pinch 2010, p.263)
		int maxYearlyCostOfResidenceTotal = Math.max(0, household.getYearlyIncome() - minimumIncome.estimateMinIncomeLeftForLiving(household));
		if ((household.getMovingDecisionYear() == modelYear) ||
				((household.getMovingDecisionYear() <= modelYear) && (modelYear == modelStartYear))
				|| (household.getAspirationRegionLivingSpaceMin() == 0) || (household.getAspirationRegionLivingSpaceMax() == 0)) {
			// Household just began searching (or the number of household members changed since the household began searching) - set initial values
			household.estimateDesiredLivingSpace();
			// lower value from income share and current dwelling costs (per m²)
			int maxYearlyCostOfResidencePerSqm = maxYearlyCostOfResidenceTotal / household.getAspirationRegionLivingSpaceMin();
			// AR 201221 - don't consider current cost of residence here because otherwise a household
			// will never look for a more expensive dwelling than the current one
//			int currentCostOfResidencePerSqm = 0;
//			if (household.hasDwelling()) {
//				currentCostOfResidencePerSqm = household.getCostOfResidence() / household.getLivingSpace();
//			} else {
//				currentCostOfResidencePerSqm = maxCostOfResidencePerSqm;
//			}
//			household.setAspirationRegionMaxCosts(Math.min(maxCostOfResidencePerSqm, currentCostOfResidencePerSqm));
			household.setAspirationRegionMaxCosts(maxYearlyCostOfResidencePerSqm);
			// unlimited maximum living space for households that are rich enough
			if (maxYearlyCostOfResidencePerSqm >= (household.getAspirationRegionLivingSpaceMax() * highestYearlyRentPer100Sqm / 100)) {
				household.setAspirationRegionLivingSpaceMax((short) 999);
			}
		} else {
			// Household continues searching - modify values from previous year
			// if maximum costs are already at the maximum for the household then reduce minimum
			// living space
			int maxCostOfResidencePerSqm = maxYearlyCostOfResidenceTotal / household.getAspirationRegionLivingSpaceMin();
			short currentLivingSpace = 0;
			if (household.hasDwelling()) {
				currentLivingSpace = household.getLivingSpace();
			}
			if (maxCostOfResidencePerSqm <= household.getAspirationRegionMaxCosts()) {
				// reduce aspired minimum living space until it drops below the current living space
				// TODO: maybe do this over a configurable number of years
				if (household.getAspirationRegionLivingSpaceMin() > currentLivingSpace) {
					household.setAspirationRegionLivingSpaceMin(currentLivingSpace);
				}
			} else {
				// increase aspired max costs of residence
				// TODO: maybe do this over a configurable number of years
				household.setAspirationRegionMaxCosts(maxCostOfResidencePerSqm);
			}
		}
	}
	/**
	 * Search a dwelling for a given household in the list of the dwellings on the market for the preferred spatial units of that household.
	 * 
	 * @param household
	 * @param modelYear
	 * @param dwellingsOnMarket
	 * @param considerDwellingCosts True, if the dwellings costs shall be considered to decide whether a dwelling is suitable or not
	 * @return the first suitable dwelling for the given household
	 */
	public DwellingRow searchDwelling(HouseholdRow household, int modelYear, DwellingsOnMarket dwellingsOnMarket, boolean considerDwellingCosts) {
		// Adjust the search area size to the number of years the household has been looking for a new dwelling
		int searchAreaSize = Common.getSearchAreaSize() + (modelYear - household.getMovingDecisionYear()) * Common.getSearchAreaSizeIncement();
		ArrayList<Integer> potentialTargetSpatialUnitIds = household.getPreferredSpatialUnits(searchAreaSize);
		assert potentialTargetSpatialUnitIds.size() > 0 : "no potential target spatial units found";
		int suitableDwellingCount = 0;
		if (considerDwellingCosts) {
			suitableDwellingCount = dwellingsOnMarket.selectSuitableDwellingsOnMarket(potentialTargetSpatialUnitIds, household.getAspirationRegionLivingSpaceMin(), household.getAspirationRegionLivingSpaceMax(), household.getAspirationRegionMaxCosts());
		} else {
			suitableDwellingCount = dwellingsOnMarket.selectSuitableDwellingsOnMarket(potentialTargetSpatialUnitIds, household.getAspirationRegionLivingSpaceMin(), household.getAspirationRegionLivingSpaceMax());
		}
//		boolean householdMoved = false;
		if (suitableDwellingCount > 0) {
			DwellingRow suitableDwelling;
			for (int i = 0; i != Common.getDwellingsConsideredPerYear(); i++) {
				suitableDwelling = dwellingsOnMarket.pickRandomSuitableDwelling();
				if (!household.hasDwelling()) {
					return suitableDwelling;
				} else {
					int potentialResidentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(household, suitableDwelling, modelYear);
					if (potentialResidentialSatisfaction > household.getCurrentResidentialSatisfaction()) {
						// we have the dwelling - move there!
						return suitableDwelling;
	//					household.relocate(dwellingsOnMarket, suitableDwelling);
	//					householdMoved = true;
	//					break;
					}
				}
			}
		} else {
//			throw new AssertionError("No suitable dwelling found");
//			if (household.getAspirationRegionMaxCosts() > 0)
//				System.out.println("No suitable dwelling found - minSize = " + household.getAspirationRegionLivingSpaceMin() + ", maxSize = " + household.getAspirationRegionLivingSpaceMax() + ", max costs = " + household.getAspirationRegionMaxCosts());
		}
//		return householdMoved;
		return null;
	}
//	/**
//	 * Search a dwelling for a given household in a certain spatial unit in the list of the dwellings on the market.
//	 *  
//	 * @param household
//	 * @param spatialUnit
//	 * @param modelYear
//	 * @param dwellingsOnMarket
//	 * @param considerDwellingCosts
//	 * @return
//	 */
//	public DwellingRow searchDwelling(HouseholdRow household, SpatialUnitRow spatialUnit, int modelYear, DwellingsOnMarket dwellingsOnMarket, boolean considerDwellingCosts) {
//		int suitableDwellingCount = 0;
//		if (considerDwellingCosts) {
//			suitableDwellingCount = dwellingsOnMarket.selectSuitableDwellingsOnMarket(spatialUnit.getSpatialUnitId(), household.getAspirationRegionLivingSpaceMin(), household.getAspirationRegionLivingSpaceMax(), household.getAspirationRegionMaxCosts());
//		} else {
//			suitableDwellingCount = dwellingsOnMarket.selectSuitableDwellingsOnMarket(potentialTargetSpatialUnitIds, household.getAspirationRegionLivingSpaceMin(), household.getAspirationRegionLivingSpaceMax());
//		}
////		boolean householdMoved = false;
//		if (suitableDwellingCount > 0) {
//			DwellingRow suitableDwelling;
//			for (int i = 0; i != Common.getDwellingsConsideredPerYear(); i++) {
//				suitableDwelling = dwellingsOnMarket.pickRandomSuitableDwelling();
//				if (!household.hasDwelling()) {
//					return suitableDwelling;
//				} else {
//					int potentialResidentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(household, suitableDwelling, modelYear);
//					if (potentialResidentialSatisfaction > household.getCurrentResidentialSatisfaction()) {
//						// we have the dwelling - move there!
//						return suitableDwelling;
//	//					household.relocate(dwellingsOnMarket, suitableDwelling);
//	//					householdMoved = true;
//	//					break;
//					}
//				}
//			}
//		} else {
////			throw new AssertionError("No suitable dwelling found");
////			if (household.getAspirationRegionMaxCosts() > 0)
////				System.out.println("No suitable dwelling found - minSize = " + household.getAspirationRegionLivingSpaceMin() + ", maxSize = " + household.getAspirationRegionLivingSpaceMax() + ", max costs = " + household.getAspirationRegionMaxCosts());
//		}
////		return householdMoved;
//		return null;
//	}
}
