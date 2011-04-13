/**
 * 
 */
package at.sume.dm.indicators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.indicators.base.Indicator;
import at.sume.dm.model.output.Fileable;

/**
 * @author Alexander Remesch
 *
 */
public class IncomePercentiles implements Indicator<HouseholdRow> {
	ArrayList<Integer> householdIncomes = new ArrayList<Integer>();
	ArrayList<Integer> personIncomes = new ArrayList<Integer>();
	boolean householdIncomesSorted = false;
	boolean personIncomesSorted = false;
	
	/* (non-Javadoc)
	 * @see at.sume.dm.indicators.base.Indicator#add(at.sume.db.RecordSetRow)
	 */
	@Override
	public void add(HouseholdRow household) {
		Integer householdIncome = household.getYearlyIncome();
		householdIncomes.add(householdIncome);
		
		for (PersonRow person : household.getMembers()) {
			Integer personIncome = person.getYearlyIncome();
			personIncomes.add(personIncome);
		}
		householdIncomesSorted = false;
		personIncomesSorted = false;
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
		personIncomes.clear();
		householdIncomesSorted = false;
		personIncomesSorted = false;
	}
	/**
	 * Get the lowest household income that is higher than the lowest given percentage of all household incomes
	 * 
	 * @param precentage
	 * @return
	 */
	public int getHouseholdIncomePercentile(byte percentage) {
		assert percentage <= 100 : "Percentage is higher than 100 (" + percentage + ")";
		assert percentage >= 0 : "Percentage is lower than 0 (" + percentage + ")";
		if (!householdIncomesSorted) {
			Collections.sort(householdIncomes);
			householdIncomesSorted = true;
		}
		int index = (int)(((long) householdIncomes.size() - 1) * percentage / 100);
		return householdIncomes.get(index);
	}
	/**
	 * Get the lowest person income that is higher than the lowest given percentage of all person incomes
	 * 
	 * @param precentage
	 * @return
	 */
	public int getPersonIncomePercentile(byte percentage) {
		assert percentage <= 100 : "Percentage is higher than 100 (" + percentage + ")";
		assert percentage >= 0 : "Percentage is lower than 0 (" + percentage + ")";
		if (!personIncomesSorted) {
			Collections.sort(personIncomes);
			personIncomesSorted = true;
		}
		int index = (int)(((long) personIncomes.size() - 1) * percentage / 100);
		return personIncomes.get(index);
	}
	@Override
	public List<? extends Fileable> getIndicatorList() {
		throw new AssertionError("not applicable here");
	}
}
