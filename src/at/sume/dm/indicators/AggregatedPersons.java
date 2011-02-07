package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.indicators.base.Indicator;
import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.AgeGroup;
import at.sume.dm.types.IncomeGroup;

/**
 * 
 * @author Alexander Remesch
 */
public class AggregatedPersons implements Indicator<HouseholdRow> {
	private ArrayList<AggregatedPersonRow> indicatorList;

	public AggregatedPersons() {
		indicatorList = new ArrayList<AggregatedPersonRow>();
	}

	@Override
	public void add(HouseholdRow household) {
		for (PersonRow person : household.getMembers()) {
			int pos = lookupIndicator(household.getSpatialunitId(), IncomeGroup.getIncomeGroupId(person.getYearlyIncome()), person.getSex(), 
					AgeGroup.getAgeGroupId(person.getAge()));
			if (pos < 0) {
				// insert at position pos
				pos = (pos + 1) * -1;
				AggregatedPersonRow b = new AggregatedPersonRow();
				b.setSpatialUnitId(household.getSpatialunitId());
				b.setIncomeGroupId(IncomeGroup.getIncomeGroupId(person.getYearlyIncome()));
				b.setSex(person.getSex());
				b.setAgeGroupId(AgeGroup.getAgeGroupId(person.getAge()));
				b.setPersonCount(1);
				indicatorList.add(pos, b);
			} else {
				// available at position pos
				AggregatedPersonRow b = indicatorList.get(pos);
				b.setPersonCount(b.getPersonCount() + 1);
				indicatorList.set(pos, b);
			}
		}
	}

	@Override
	public void remove(HouseholdRow hh) {
		// Nothing to do here since this indicator is only for output
	}

	@Override
	public void clear() {
		indicatorList.clear();
	}
	
	private int lookupIndicator(int spatialUnitId, byte incomeGroupId, byte sex, byte ageGroupId) {
		AggregatedPersonRow lookup = new AggregatedPersonRow();
		lookup.setSpatialUnitId(spatialUnitId);
		lookup.setIncomeGroupId(incomeGroupId);
		lookup.setSex(sex);
		lookup.setAgeGroupId(ageGroupId);
		return Collections.binarySearch(indicatorList, lookup);
	}

	@Override
	public List<? extends Fileable> getIndicatorList() {
		return indicatorList;
	}
}
