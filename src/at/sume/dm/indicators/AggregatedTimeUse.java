/**
 * 
 */
package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.sume.dm.Common;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.TimeUseRow;
import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.IncomeGroup;
import at.sume.dm.types.IncomeGroup3;

/**
 * Collect aggregated time use for model output
 * @author Alexander Remesch
 */
public class AggregatedTimeUse {
	private ArrayList<AggregatedTimeUseRow> indicatorList;
	
	public AggregatedTimeUse() {
		indicatorList = new ArrayList<AggregatedTimeUseRow>();
	}
	private AggregatedTimeUseRow setupIndicator(PersonRow person, TimeUseRow timeUse) {
		AggregatedTimeUseRow result = new AggregatedTimeUseRow();
		result.setSpatialUnitId(person.getHousehold().getDwelling().getSpatialunitId());
		result.setActivity(timeUse.activity);
		switch(Common.getOutputIncomeGroups()) {
		case 3:
			result.setIncomeGroupId(IncomeGroup3.getIncomeGroupId(person.getYearlyIncome()));
			break;
		case 18:
			result.setIncomeGroupId(IncomeGroup.getIncomeGroupId(person.getYearlyIncome()));
			break;
		default:
			throw new IllegalArgumentException("Unknown number of income groups - only 3 and 18 allowed!");
		}
		result.setHouseholdType(person.getHousehold().getHouseholdType());
		result.setTimeUseType(person.getTimeUseType());
		return result;
	}
	/**
	 * Build the time use indicators for all persons
	 * @param persons
	 */
	public void build(ArrayList<PersonRow> persons) {
		clear();
		for (PersonRow person : persons) {
			// some checks
			assert person.getHousehold() != null : "Person " + person.getPersonId() + " has no household assigned";
			assert person.getHousehold().getMembers() != null : "Household " + person.getHousehold().getHouseholdId() + " of person " + person.getPersonId() + " has no members!";
			if (!person.getHousehold().hasDwelling()) // AR 160411 - there might be left-over emigrated households at this stage, so ignore them here!
				continue;
			assert person.getHousehold().getMembers().contains(person) == true : "Household " + person.getHousehold().getHouseholdId() + " of person " + person.getPersonId() + " does not have this person in its member list!";
			build(person);
		}
	}
	/**
	 * Build the time use indicators for a single person
	 * @param person
	 */
	public void build(PersonRow person) {
		if (person.getTimeUse() != null) {
			for (TimeUseRow timeUse : person.getTimeUse()) {
				AggregatedTimeUseRow lookup = setupIndicator(person, timeUse);
				int pos = Collections.binarySearch(indicatorList, lookup);  
				if (pos < 0) {
					// insert at position pos
					pos = (pos + 1) * -1;
					lookup.setParticipatingPersonCount(1);
					lookup.setParticipatingHouseholdCount(1);
					lookup.setTimeUseSum(timeUse.avgTimeUse);
					indicatorList.add(pos, lookup);
				} else {
					// available at position pos
					AggregatedTimeUseRow b = indicatorList.get(pos);
					b.setParticipatingPersonCount(b.getParticipatingPersonCount() + 1);
					b.setParticipatingHouseholdCount(b.getParticipatingHouseholdCount() + 1);
					b.setTimeUseSum(b.getTimeUseSum() + timeUse.avgTimeUse);
					indicatorList.set(pos, b);
				}
			}
		}
	}
	
	public void clear() {
		indicatorList.clear();
	}

	public List<? extends Fileable> getIndicatorList() {
		return indicatorList;
	}
}
