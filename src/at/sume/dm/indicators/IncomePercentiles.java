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
	boolean householdIncomesSorted = false;
	boolean personIncomesSorted = false;
	
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.base.Indicator#add(at.sume.db.RecordSetRow)
	 */
	@Override
	public void add(HouseholdRow household) {
		Long householdIncome = household.getYearlyIncome();
		householdIncomes.add(householdIncome);
		
		for (PersonRow person : household.getMembers()) {
			Long personIncome = person.getYearlyIncome();
			personIncomes.add(personIncome);
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
		if (!householdIncomesSorted) {
			Collections.sort(householdIncomes);
			householdIncomesSorted = true;
		}
		int index = Math.round((householdIncomes.size() - 1) * percentage / 100);
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
		if (!personIncomesSorted) {
			Collections.sort(personIncomes);
			personIncomesSorted = true;
		}
		int index = Math.round((personIncomes.size() - 1) * percentage / 100);
		return personIncomes.get(index);
	}
}
