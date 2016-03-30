package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.indicators.base.Indicator;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.IncomeGroup;
import at.sume.dm.types.IncomeGroup3;

/**
 * 
 * @author Alexander Remesch
 */
public class AggregatedHouseholds implements Indicator<HouseholdRow> {
	private ArrayList<AggregatedHouseholdRow> indicatorList;

	public AggregatedHouseholds() {
		indicatorList = new ArrayList<AggregatedHouseholdRow>();
	}
	
	@Override
	public void add(HouseholdRow household) {
		byte incomeGroup = 0;
		switch(Common.getOutputIncomeGroups()) {
		case 3:
			incomeGroup = IncomeGroup3.getIncomeGroupId(household.getYearlyIncome());
			break;
		case 18:
			incomeGroup = IncomeGroup.getIncomeGroupId(household.getYearlyIncome());
			break;
		default:
			throw new IllegalArgumentException("Unknown number of income groups - only 3 and 18 allowed!");
		}
		int pos = lookupIndicator(household.getSpatialunitId(), incomeGroup, household.getLivingSpaceGroupId(), 
				household.getHouseholdSize(), household.getHouseholdType());
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			AggregatedHouseholdRow b = new AggregatedHouseholdRow();
			b.setSpatialUnitId(household.getSpatialunitId());
			b.setIncomeGroupId(incomeGroup);
			b.setLivingSpaceGroupId(household.getLivingSpaceGroupId());
			b.setHouseholdSize(household.getHouseholdSize());
			b.setHouseholdType(household.getHouseholdType());
			b.setHouseholdCount(1);
			b.setPersonCount(household.getMemberCount());
			indicatorList.add(pos, b);
		} else {
			// available at position pos
			AggregatedHouseholdRow b = indicatorList.get(pos);
			b.setHouseholdCount(b.getHouseholdCount() + 1);
			b.setPersonCount(b.getPersonCount() + household.getMemberCount());
			indicatorList.set(pos, b);
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

	private int lookupIndicator(int spatialUnitId, byte incomeGroupId, byte livingSpaceGroupId, short householdSize, HouseholdType householdType) {
		AggregatedHouseholdRow lookup = new AggregatedHouseholdRow();
		lookup.setSpatialUnitId(spatialUnitId);
		lookup.setIncomeGroupId(incomeGroupId);
		lookup.setLivingSpaceGroupId(livingSpaceGroupId);
		lookup.setHouseholdSize(householdSize);
		lookup.setHouseholdType(householdType);
		return Collections.binarySearch(indicatorList, lookup);
	}

	@Override
	public List<AggregatedHouseholdRow> getIndicatorList() {
		return indicatorList;
	}
}
