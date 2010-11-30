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
	public void estimateAspirationRegion(HouseholdRow household, int modelYear) {
		// 1) estimate the aspiration region: 
		//    a) needed living space - by household size 
		//    b) maximum costs - by current costs, household income
		// lower limit: commonly defined by the current dwelling; upper limit: set by the standards to
		// which the household can reasonably aspire (Knox/Pinch 2010, p.263)
		long maxCostOfResidence = household.getYearlyIncome() - minimumIncome.estimateMinIncomeLeftForLiving(household);
		if (household.getMovingDecisionYear() == modelYear) {
			// Household just began searching - set initial values
			household.estimateDesiredLivingSpace();
			// lower value from income share and current dwelling costs (per m�)
			long maxCostOfResidencePerSqm = maxCostOfResidence / household.getAspirationRegionLivingSpaceMin();
			long currentCostOfResidencePerSqm = household.getCostOfResidence() / household.getLivingSpace();
			household.setAspirationRegionMaxCosts(Math.min(maxCostOfResidencePerSqm, currentCostOfResidencePerSqm));
		} else {
			// Household continues searching - modify values from previous year
			// if maximum costs are already at the maximum for the household then reduce minimum
			// living space
			long maxCostOfResidencePerSqm = maxCostOfResidence / household.getAspirationRegionLivingSpaceMin();
			if (maxCostOfResidencePerSqm <= household.getAspirationRegionMaxCosts()) {
				// reduce aspired minimum living space until it drops below the current living space
				// TODO: maybe do this over a configurable number of years
				if (household.getAspirationRegionLivingSpaceMin() > household.getLivingSpace()) {
					household.setAspirationRegionLivingSpaceMin(household.getLivingSpace());
				}
			} else {
				// increase aspired max costs of residence
				// TODO: maybe do this over a configurable number of years
				household.setAspirationRegionMaxCosts(maxCostOfResidencePerSqm);
			}
		}
	}
	public boolean searchDwelling(HouseholdRow household, int modelYear, DwellingsOnMarket dwellingsOnMarket) {
		ArrayList<Long> potentialTargetSpatialUnitIds = household.getPreferredSpatialUnits(Common.getSearchAreaSize());
		dwellingsOnMarket.selectSuitableDwellingsOnMarket(potentialTargetSpatialUnitIds, household.getAspirationRegionLivingSpaceMin(), household.getAspirationRegionLivingSpaceMax(), household.getAspirationRegionMaxCosts());
		DwellingRow suitableDwelling;
		boolean householdMoved = false;
		for (int i = 0; i != Common.getDwellingsConsideredPerYear(); i++) {
			suitableDwelling = dwellingsOnMarket.pickRandomSuitableDwelling();
			int potentialResidentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(household, suitableDwelling, modelYear);
			if (potentialResidentialSatisfaction > household.getCurrentResidentialSatisfaction()) {
				// we have the dwelling - move there!
				household.relocate(suitableDwelling);
				householdMoved = true;
				break;
			}
		}
		return householdMoved;
	}
}