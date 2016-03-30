package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.indicators.base.Indicator;
import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.AgeGroup;
import at.sume.dm.types.IncomeGroup;
import at.sume.dm.types.IncomeGroup3;

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
			short householdSize6 = person.getHousehold().getHouseholdSize();
			byte incomeGroup = 0;
			switch(Common.getOutputIncomeGroups()) {
			case 3:
				incomeGroup = IncomeGroup3.getIncomeGroupId(person.getYearlyIncome());
				break;
			case 18:
				incomeGroup = IncomeGroup.getIncomeGroupId(person.getYearlyIncome());
				break;
			default:
				throw new IllegalArgumentException("Unknown number of income groups - only 3 and 18 allowed!");
			}
			if (householdSize6 > 6) householdSize6 = 6;
			int pos = lookupIndicator(household.getSpatialunitId(), incomeGroup, person.getSex(), 
					AgeGroup.getAgeGroupId(person.getAge()), person.isLivingWithParents(), householdSize6);
			if (pos < 0) {
				// insert at position pos
				pos = (pos + 1) * -1;
				AggregatedPersonRow b = new AggregatedPersonRow();
				b.setSpatialUnitId(household.getSpatialunitId());
				b.setIncomeGroupId(incomeGroup);
				b.setSex(person.getSex());
				b.setAgeGroupId(AgeGroup.getAgeGroupId(person.getAge()));
				b.setLivingWithParents(person.isLivingWithParents());
				b.setHouseholdSize6(householdSize6);
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
	
	private int lookupIndicator(int spatialUnitId, byte incomeGroupId, byte sex, byte ageGroupId, boolean livingWithParents, short householdSize6) {
		AggregatedPersonRow lookup = new AggregatedPersonRow();
		lookup.setSpatialUnitId(spatialUnitId);
		lookup.setIncomeGroupId(incomeGroupId);
		lookup.setSex(sex);
		lookup.setAgeGroupId(ageGroupId);
		lookup.setLivingWithParents(livingWithParents);
		lookup.setHouseholdSize6(householdSize6);
		return Collections.binarySearch(indicatorList, lookup);
	}

	@Override
	public List<? extends Fileable> getIndicatorList() {
		return indicatorList;
	}
}
