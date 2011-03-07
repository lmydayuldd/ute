/**
 * 
 */
package at.sume.dm.demography.events;

import java.util.ArrayList;

import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.model.residential_mobility.DwellingsOnMarket;
import at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionManager;

/**
 * This is a very simple implementation of a strategy to cohabitate households. It just builds a list of 
 * male and female single/single parent households and then picks a given number of them randomly pairwise 
 * to join them.
 * 
 * In this simple version, no age or other household properties are considered in the selection process
 * (apart from the sex).
 *  
 * @author Alexander Remesch
 */
public class Cohabitation {
	private int numCohabitations;
	private int modelYear;
	private DwellingsOnMarket dwellingsOnMarket;
	private ArrayList<HouseholdRow> maleHouseholds, femaleHouseholds;
	
	public Cohabitation(int numCohabitations, int modelYear, DwellingsOnMarket dwellingsOnMarket) {
		this.numCohabitations = numCohabitations;
		this.modelYear = modelYear;
		this.dwellingsOnMarket = dwellingsOnMarket;
		maleHouseholds = new ArrayList<HouseholdRow>();
		femaleHouseholds = new ArrayList<HouseholdRow>();
	}
	/**
	 * Add a household to the internal lists of male/female households if it is a single adult household
	 * (= a candidate for cohabitation)
	 * 
	 * @param household
	 */
	public void addHousehold(HouseholdRow household) {
		switch (household.getHouseholdType()) {
		case SINGLE_OLD:
		case SINGLE_YOUNG:
		case SINGLE_PARENT:
			household.countAdults();
			if (household.isAdultFemale())
				femaleHouseholds.add(household);
			else
				maleHouseholds.add(household);
		}
	}
	/**
	 * Join the given number of households from the potential candidates for cohabitation found earlier.
	 * Choose the dwelling kept by comparison of residential satisfaction. Put the dwelling left on the
	 * housing market.
	 * 
	 * @return the number of households joined
	 */
	public int randomJoinHouseholds() {
		int index = 0;
		for (int i = 0; i != numCohabitations; i++) {
			index = (int) (Math.random() * maleHouseholds.size());
			HouseholdRow maleHousehold = maleHouseholds.get(index);
			maleHouseholds.remove(index);
			index = (int) (Math.random() * femaleHouseholds.size());
			HouseholdRow femaleHousehold = femaleHouseholds.get(index);
			femaleHouseholds.remove(index);
			maleHousehold.join(femaleHousehold);
			// Choose dwelling by comparison of residential satisfaction
			int currentResidentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(maleHousehold, modelYear);
			int alternativeResidentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(maleHousehold, femaleHousehold.getDwelling(), modelYear);
			DwellingRow oldDwelling = null;
			if (alternativeResidentialSatisfaction > currentResidentialSatisfaction) {
				oldDwelling = maleHousehold.getDwelling();
				maleHousehold.setDwelling(femaleHousehold.getDwelling());
			} else {
				oldDwelling = femaleHousehold.getDwelling();
			}
			maleHousehold.getDwelling().setHousehold(maleHousehold);
			femaleHousehold.setDwelling(null);
			dwellingsOnMarket.putDwellingOnMarket(oldDwelling);
			// Force calculation of dwelling costs to current market values for the old dwelling
			oldDwelling.calcTotalYearlyDwellingCosts(true);
			femaleHousehold.remove(dwellingsOnMarket);
		}
		return numCohabitations;
	}
}
