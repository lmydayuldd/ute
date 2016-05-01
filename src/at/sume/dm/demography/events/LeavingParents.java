/**
 * 
 */
package at.sume.dm.demography.events;

import java.sql.SQLException;
import java.util.ArrayList;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.tracing.ObjectSource;
import at.sume.sampling.SampleWorkplaces;
import net.remesch.db.Database;
import net.remesch.probability.SingleProbability;
import net.remesch.util.Random;

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
	private SampleWorkplaces sampleWorkplaces;
	private int minIncomeForWorkplace;
	private Random r = new Random();

	public LeavingParents(Database db, SingleProbability leavingParentsProbability, byte childrenMaxAge, int modelYear) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException, SQLException {
		this.leavingParentsProbability = leavingParentsProbability;
		this.modelYear = modelYear;
		childrenLeaving = new ArrayList<PersonRow>();
		this.childrenMaxAge = childrenMaxAge;
		// Sampling of workplaces
		sampleWorkplaces = new SampleWorkplaces(db);
		minIncomeForWorkplace = Integer.parseInt(Common.getSysParamDataPreparation("MinIncomeForWorkplace"));
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
	
	public ArrayList<HouseholdRow> getNewSingleHouseholds() throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException, SQLException {
		ArrayList<HouseholdRow> result = new ArrayList<HouseholdRow>(childrenLeaving.size());
		for (PersonRow childLeaving : childrenLeaving) {
			if (childLeaving.getHousehold() == null) // Person emigrated already this year! AR 160501
				continue;
			if (childLeaving.getHousehold().getHouseholdSize() > 1 &&
					childLeaving.getHousehold().hasDwelling()) {
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
	
	private HouseholdRow createNewSingleHousehold(PersonRow person) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException, SQLException {
		HouseholdRow parentHousehold = person.getHousehold();
		int yearlyIncome = 0;
		if (r.nextDouble() < 0.5) {
			// take lowest adult income as new household income
			yearlyIncome = parentHousehold.getLowestAdultIncome();
		} else {
			// take highest adult income as new household income
			yearlyIncome = parentHousehold.getHighestAdultIncome();
		}
		person.setLivingWithParents(false);
		HouseholdRow newHousehold = new HouseholdRow(ObjectSource.LEAVING_PARENTS);
		newHousehold.setMovingDecisionYear((short) modelYear);
		newHousehold.addMember(person);
		parentHousehold.removeMember(person);
		person.setHousehold(newHousehold);
		newHousehold.determineInitialHouseholdType(false);	// countAdults() was already done in addMember()
		person.setYearlyIncome(yearlyIncome);
		newHousehold.setDwelling(parentHousehold.getDwelling()); // this is needed to be able to count leaving parent moves per spatial unit
		assert newHousehold.hasDwelling() == true : "Children household has no dwelling!";

		// Put workplace sampling here similar to SampleDbPersons.randomSample()
		// TODO: Work place (matching income & age!) - same as in SampleDbPersons.randomSample()
		// TODO: this must be more elaborated: persons age and are either looking for a job or not and therefore taking up a workplace
		// TODO: initial work place sampling doesn't make sense at this stage since the final place of residence is not assigned to the
		//       household at this point
		if (person.getAge() >= 15 && person.getAge() <= 64) {
			if (yearlyIncome >= minIncomeForWorkplace) { 
				// set workplace
				sampleWorkplaces.loadCommuterMatrix(newHousehold.getDwelling().getSpatialunit().getSpatialUnitId());
				person.setWorkplaceCellId(sampleWorkplaces.randomSample());
			}
		}
		return newHousehold;
	}
}
