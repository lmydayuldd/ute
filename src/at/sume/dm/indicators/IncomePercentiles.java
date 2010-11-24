/**
 * 
 */
package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.indicators.base.Indicator;

/**
 * @author Alexander Remesch
 *
 */
public class IncomePercentiles implements Indicator<HouseholdRow> {
	ArrayList<Long> householdIncomes = new ArrayList<Long>();
	ArrayList<Long> personIncomes = new ArrayList<Long>();
	
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.base.Indicator#add(at.sume.db.RecordSetRow)
	 */
	@Override
	public void add(HouseholdRow household) {
		Long householdIncome = household.getYearlyIncome();
		int index = Collections.binarySearch(householdIncomes, householdIncome);
		if (index < 0) {
			// insert at position pos
			index = (index + 1) * -1;
		}
		householdIncomes.add(index, householdIncome);
		
		for (PersonRow person : household.getMembers()) {
			Long personIncome = person.getYearlyIncome();
			index = Collections.binarySearch(personIncomes, personIncome);
			if (index < 0) {
				// insert at position pos
				index = (index + 1) * -1;
			}
			personIncomes.add(index, personIncome);
		}
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.base.Indicator#remove(at.sume.db.RecordSetRow)
	 */
	@Override
	public void remove(HouseholdRow household) {
		throw new AssertionError("IncomePercentiles.remove() is not implemented - this indicator has to be cleared!");
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.base.Indicator#clear()
	 */
	@Override
	public void clear() {
		householdIncomes.clear();
	}
	/**
	 * Get the lowest household income that is higher than the lowest given percentage of all household incomes
	 * 
	 * @param precentage
	 * @return
	 */
	public long getHouseholdIncomePercentile(short percentage) {
		assert percentage <= 100 : "Percentage is higher than 100 (" + percentage + ")";
		assert percentage >= 0 : "Percentage is lower than 0 (" + percentage + ")";
		int index = Math.round(householdIncomes.size() * percentage / percentage);
		return householdIncomes.get(index);
	}
	/**
	 * Get the lowest person income that is higher than the lowest given percentage of all person incomes
	 * 
	 * @param precentage
	 * @return
	 */
	public long getPersonIncomePercentile(short percentage) {
		assert percentage <= 100 : "Percentage is higher than 100 (" + percentage + ")";
		assert percentage >= 0 : "Percentage is lower than 0 (" + percentage + ")";
		int index = Math.round(personIncomes.size() * percentage / percentage);
		return personIncomes.get(index);
	}
}
