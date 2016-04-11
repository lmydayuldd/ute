/**
 * 
 */
package at.sume.dm.demography.events;

import java.util.ArrayList;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.model.residential_mobility.DwellingsOnMarket;
import at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionManager;
import at.sume.dm.tracing.ObjectSource;
import net.remesch.util.Random;

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
public class MovingTogether {
	private int numMovesTogether;
	private int modelYear;
	private DwellingsOnMarket dwellingsOnMarket;
	private ArrayList<HouseholdRow> maleHouseholds, femaleHouseholds;
	
	public MovingTogether(int numMovesTogether, int modelYear, DwellingsOnMarket dwellingsOnMarket) {
		this.numMovesTogether = numMovesTogether;
		this.modelYear = modelYear;
		this.dwellingsOnMarket = dwellingsOnMarket;
		maleHouseholds = new ArrayList<HouseholdRow>();
		femaleHouseholds = new ArrayList<HouseholdRow>();
	}
	/**
	 * Add a household to the internal lists of male/female households if it is a single adult household
	 * (= a candidate for moving together)
	 * 
	 * @param household
	 */
	public void addHousehold(HouseholdRow household) {
		switch (household.getHouseholdType()) {
		case SINGLE_OLD:
		case SINGLE_YOUNG:
		case SINGLE_PARENT:
			household.countAdults();
			if (household.hasAdultFemale())
				femaleHouseholds.add(household);
			else
				maleHouseholds.add(household);
			break;
		default: // no single household -> do nothing
		}
	}
	/**
	 * Join the given number of households from the potential candidates for moving together found earlier.
	 * Choose the dwelling kept by comparison of residential satisfaction. Put the dwelling left on the
	 * housing market.
	 * 
	 * @return the number of households joined
	 */
	public int randomJoinHouseholds() {
		Random r = new Random();
		int index = 0;
		numMovesTogether = Math.min(numMovesTogether, maleHouseholds.size());
		numMovesTogether = Math.min(numMovesTogether, femaleHouseholds.size());
		for (int i = 0; i != numMovesTogether; i++) {
			HouseholdRow maleHousehold; 
			do {
				index = (int) (r.nextDouble() * maleHouseholds.size());
				maleHousehold = maleHouseholds.get(index);
				maleHouseholds.remove(index);
			} while (!maleHousehold.hasDwelling() && maleHouseholds.size() > 0);
			if (!maleHousehold.hasDwelling())
				break;
			HouseholdRow femaleHousehold;
			do {
				index = (int) (r.nextDouble() * femaleHouseholds.size());
				femaleHousehold = femaleHouseholds.get(index);
				femaleHouseholds.remove(index);
			} while (!femaleHousehold.hasDwelling() && femaleHouseholds.size() > 0);
			if (!femaleHousehold.hasDwelling())
				break;
			// Create a fictive household to determine the direction of the move
			HouseholdRow temp = new HouseholdRow(ObjectSource.MOVING_TOGETHER_TEMP);
			temp.addMembers(maleHousehold.getMembers());
			temp.addMembers(femaleHousehold.getMembers());
			temp.determineInitialHouseholdType(false);
			// Choose dwelling by comparison of residential satisfaction
			int maleDwellingResidentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(temp, maleHousehold.getDwelling(), modelYear);
			int femaleDwellingResidentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(temp, femaleHousehold.getDwelling(), modelYear);
			temp = null;
			// Reset household pointed to by the persons (was changed by temp.addMembers() above!
			// TODO: this is a mess!
			for (PersonRow person : maleHousehold.getMembers())
				person.setHousehold(maleHousehold);
			for (PersonRow person : femaleHousehold.getMembers())
				person.setHousehold(femaleHousehold);
			if (femaleDwellingResidentialSatisfaction > maleDwellingResidentialSatisfaction) {
				femaleHousehold.join(maleHousehold);
				maleHousehold.remove(dwellingsOnMarket, ObjectSource.MOVED_TOGETHER);
				assert femaleHousehold.hasDwelling() == true : "New household has no dwelling!";
			} else {
				maleHousehold.join(femaleHousehold);
				femaleHousehold.remove(dwellingsOnMarket, ObjectSource.MOVED_TOGETHER);
				assert maleHousehold.hasDwelling() == true : "New household has no dwelling!";
			}
		}
		return numMovesTogether;
	}
}
