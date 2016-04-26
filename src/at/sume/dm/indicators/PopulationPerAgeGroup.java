/**
 * 
 */
package at.sume.dm.indicators;

import java.util.Collections;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.indicators.base.IndicatorBase;
import at.sume.dm.indicators.rows.PopulationPerAgeGroupRow;
import at.sume.dm.types.AgeGroup20;

/**
 * @author Alexander Remesch
 *
 */
public class PopulationPerAgeGroup extends IndicatorBase<PopulationPerAgeGroupRow> {

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.IndicatorBase#add(at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public void add(HouseholdRow household) {
		for (PersonRow person : household.getMembers()) {
			int pos = lookup(person);
			if (pos < 0) {
				// insert at position pos
				pos = (pos + 1) * -1;
				insert(pos, person);
			} else {
				// available at position pos
				update(pos, person, false);
			}
		}
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.Indicator2#remove(at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public void remove(HouseholdRow household) {
		for (PersonRow person : household.getMembers()) {
			int pos = lookup(person);
			if (pos < 0) {
				// not there, unable to remove - throw exception
				throw new AssertionError("PopulationPerAgeGroup.remove() - Spatial unit " + person.getHousehold().getDwelling().getSpatialunitId() + " & age group " + AgeGroup20.getAgeGroupId(person.getAge()) + " are not in the list of spatial units");
			} else {
				// available at position pos
				update(pos, person, true);
			}
		}
	}
	/**
	 * Lookup the (potential) indicator position for a given person
	 * @param person
	 * @return
	 */
	public int lookup(PersonRow person) {
		PopulationPerAgeGroupRow lookup = new PopulationPerAgeGroupRow();
		lookup.setSpatialUnitId(person.getHousehold().getDwelling().getSpatialunitId());
		lookup.setAgeGroupId(AgeGroup20.getAgeGroupId(person.getAge()));
		return Collections.binarySearch(indicatorList, lookup);
	}
	/**
	 * Insert the person given to the set of indicators
	 * 
	 * @param pos Position at which the person should be added to the indicator list
	 * @param person Person data to update the indicators with
	 */
	public void insert(int pos, PersonRow person) {
		PopulationPerAgeGroupRow b = new PopulationPerAgeGroupRow();
		b.setSpatialUnitId(person.getHousehold().getDwelling().getSpatialunitId());
		b.setAgeGroupId(AgeGroup20.getAgeGroupId(person.getAge()));
		b.setPersonCount(1);
		indicatorList.add(pos, b);
	}
	/**
	 * Update the indicators with the given person
	 * 
	 * @param pos Position of the person in the indicator list
	 * @param person Person data to update the indicators with
	 * @param remove If true, remove the person from the indicators. Otherwise add the person to the indicators. 
	 */
	public void update(int pos, PersonRow person, boolean remove) {
		PopulationPerAgeGroupRow b = indicatorList.get(pos);
		if (remove)
			b.setPersonCount(b.getPersonCount() - 1);
		else
			b.setPersonCount(b.getPersonCount() + 1);
		assert b.getPersonCount() >= 0 : "Spatial unit " + person.getHousehold().getDwelling().getSpatialunitId() + " & age group " + AgeGroup20.getAgeGroupId(person.getAge()) + " - person count < 0";
		indicatorList.set(pos, b);
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.IndicatorBase#outputHeadline()
	 */
	@Override
	public void outputHeadline() {
		output.println("ModelYear\tSpatialUnitId\tAgeGroup\tPersonCount");
	}
}
