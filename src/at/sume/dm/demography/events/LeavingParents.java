/**
 * 
 */
package at.sume.dm.demography.events;

import java.util.ArrayList;

import net.remesch.probability.SingleProbability;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;

/**
 * This is a very simple implementation to make children leave their parental home. The (annual) rate of children
 * moving out is set upon class creation (which is done each model year).
 * 
 * @author Alexander Remesch
 */
public class LeavingParents {
	private SingleProbability leavingParentsProbability;
	private int modelYear;
	private ArrayList<PersonRow> childrenLeaving;
	private byte childrenMaxAge;

	public LeavingParents(SingleProbability leavingParentsProbability, byte childrenMaxAge, int modelYear) {
		this.leavingParentsProbability = leavingParentsProbability;
		this.modelYear = modelYear;
		childrenLeaving = new ArrayList<PersonRow>();
		this.childrenMaxAge = childrenMaxAge;
	}

	public void addHousehold(HouseholdRow existingHousehold) {
		switch (existingHousehold.getHouseholdType()) {
		case SINGLE_PARENT:
		case SMALL_FAMILY:
		case LARGE_FAMILY:
			for (PersonRow member : existingHousehold.getMembers()) {
				if (member.getAge() > childrenMaxAge) {
					if (member.isLivingWithParents()) {
						if (leavingParentsProbability.occurs()) {
							childrenLeaving.add(member);
						}
					}
				}
			}
		default: // no children in household -> do nothing
		}
	}
	
	public ArrayList<HouseholdRow> getNewSingleHouseholds() {
		ArrayList<HouseholdRow> result = new ArrayList<HouseholdRow>(childrenLeaving.size());
		for (PersonRow childLeaving : childrenLeaving) {
			if (childLeaving.getHousehold().getHouseholdSize() > 1) {
				// create new single household
				HouseholdRow parentHousehold = childLeaving.getHousehold();
				HouseholdRow newHousehold = createNewSingleHousehold(childLeaving);
				
				// recalculate remaining parental household
				parentHousehold.updateHouseholdTypeAfterDeathOrMemberLeaving();
				
				// add to results
				assert newHousehold.hasDwelling() == true : "Children household has no dwelling!";
				result.add(newHousehold);
			}
		}
		return result;
	}
	
	private HouseholdRow createNewSingleHousehold(PersonRow person) {
		HouseholdRow parentHousehold = person.getHousehold();
		int yearlyIncome = 0;
		if (Math.random() < 0.5) {
			// take lowest adult income as new household income
			yearlyIncome = parentHousehold.getLowestAdultIncome();
		} else {
			// take highest adult income as new household income
			yearlyIncome = parentHousehold.getHighestAdultIncome();
		}
		person.setLivingWithParents(false);
		HouseholdRow newHousehold = new HouseholdRow();
		newHousehold.setMovingDecisionYear((short) modelYear);
		newHousehold.addMember(person);
		parentHousehold.removeMember(person);
		person.setHousehold(newHousehold);
		newHousehold.determineInitialHouseholdType(false);	// countAdults() was already done in addMember()
		person.setYearlyIncome(yearlyIncome);
		newHousehold.setDwelling(parentHousehold.getDwelling()); // this is needed to be able to count leaving parent moves per spatial unit
		assert newHousehold.hasDwelling() == true : "Children household has no dwelling!";
		
		return newHousehold;
	}
}
