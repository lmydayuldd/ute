/**
 * 
 */
package net.remesch.obsolete;

import java.util.Collections;


import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.indicators.base.IndicatorBase;
import at.sume.dm.types.IncomeGroup;

/**
 * @author Alexander Remesch
 *
 */
public class ImmigratingHouseholds extends IndicatorBase<ImmigratingHouseholdsRow> {

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.base.IndicatorBase#lookup(at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public int lookup(HouseholdRow household) {
		ImmigratingHouseholdsRow lookup = new ImmigratingHouseholdsRow();
		lookup.setSpatialUnitId(household.getDwelling().getSpatialunitId());
		lookup.setIncomeGroupId(IncomeGroup.getIncomeGroupId(household.getYearlyIncome()));
		lookup.setHouseholdSize((byte) Math.max(4, household.getMembers().size()));
		return Collections.binarySearch(indicatorList, lookup);
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.base.IndicatorBase#insert(int, at.sume.dm.entities.HouseholdRow)
	 */
	@Override
	public void insert(int pos, HouseholdRow household) {
		ImmigratingHouseholdsRow b = new ImmigratingHouseholdsRow();
		b.setSpatialUnitId(household.getDwelling().getSpatialunitId());
		b.setIncomeGroupId(IncomeGroup.getIncomeGroupId(household.getYearlyIncome()));
		b.setHouseholdSize((byte) Math.max(4, household.getMembers().size()));
		b.setHouseholdCount(1);
		b.setPersonCount(household.getMembers().size());
		indicatorList.add(pos, b);
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.base.IndicatorBase#update(int, at.sume.dm.entities.HouseholdRow, boolean)
	 */
	@Override
	public void update(int pos, HouseholdRow household, boolean remove) {
		ImmigratingHouseholdsRow b = indicatorList.get(pos);
		if (remove) {
			b.setHouseholdCount(b.getHouseholdCount() - 1);
			b.setPersonCount(b.getPersonCount() - household.getMembers().size());
		} else {
			b.setHouseholdCount(b.getHouseholdCount() + 1);
			b.setPersonCount(b.getPersonCount() + household.getMembers().size());
		}
		assert b.getHouseholdCount() >= 0 : "Spatial unit " + household.getDwelling().getSpatialunitId() + " & income group " 
			+ IncomeGroup.getIncomeGroupNameDirect(IncomeGroup.getIncomeGroupId(household.getYearlyIncome())) + " & household size " 
			+ household.getMembers().size() + " - household count < 0";
		assert b.getPersonCount() >= 0 : "Spatial unit " + household.getDwelling().getSpatialunitId() + " & income group " 
			+ IncomeGroup.getIncomeGroupNameDirect(IncomeGroup.getIncomeGroupId(household.getYearlyIncome())) + " & household size " 
			+ household.getMembers().size() + " - person count < 0";
		indicatorList.set(pos, b);
	}

}
