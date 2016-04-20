/**
 * 
 */
package at.sume.dm.types;

import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.util.Random;
import at.sume.dm.Common;

/**
 * This class is used to convert between income and income-groups through the data given in table
 * _DM_IncomeGroup
 * 
 * @author Alexander Remesch
 */
public class IncomeGroup {
	public static class IncomeGroupRow {
		public byte id;
		public String incomeGroup;
		public int minincome;
		public int maxincome;
	}
	static ArrayList<IncomeGroupRow> incomeGroups;
	private static Random r = new Random();
	static {
//		incomeGroups = new ArrayList<IncomeGroupRow>();
		String selectStatement = "select id, incomeGroup, minincome, maxincome from _DM_IncomeGroup order by id";
		try {
			incomeGroups = Common.db.select(IncomeGroupRow.class, selectStatement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Get the income group for a certain income 
	 * @param income
	 * @return
	 */
	public static byte getIncomeGroupId(int income) {
		if (income > incomeGroups.get(incomeGroups.size() - 1).maxincome)
			return (byte) (incomeGroups.size() - 1);
		if (income < incomeGroups.get(0).minincome)
			return 0;
		for (IncomeGroupRow i : incomeGroups) {
			if ((i.minincome <= income) && (income <= i.maxincome)) {
				return i.id;
			}
		}
		throw new AssertionError("No income group for income " + income + " found");
//		return 0;
	}
	/**
	 * Get the printable name of a certain income group by direct access to ArrayList
	 * NOTE: Requires continuously ascending incomeGroupId
	 * @param incomeGroupId
	 * @return
	 */
	public static String getIncomeGroupNameDirect(byte incomeGroupId) {
		assert incomeGroupId > 0 : "incomeGroupId <= 0";
		assert incomeGroupId <= incomeGroups.size() : "incomeGroupId > " + incomeGroups.size();
		return incomeGroups.get(incomeGroupId - 1).incomeGroup;
	}
	private static IncomeGroupRow lookupIncomeGroup(byte incomeGroupId) {
		for (IncomeGroupRow i : incomeGroups) {
			if (i.id == incomeGroupId)
				return i;
		}
		return null;
	}
	/**
	 * Sample a income for a given income group id
	 * @param incomeGroupId
	 * @return
	 */
	public static int sampleIncome(byte incomeGroupId) {
		IncomeGroupRow incomeGroupRow = lookupIncomeGroup(incomeGroupId);
		int result = 0;
		int index = incomeGroups.indexOf(incomeGroupRow);
		if (index == incomeGroups.size() - 1) {
			// Last element
			result = (int) r.triangular(incomeGroupRow.minincome, incomeGroupRow.minincome * 5, incomeGroupRow.minincome);
		} else if (index == 0) {
			// First element
			result = (int) r.triangular(incomeGroupRow.minincome, incomeGroupRow.maxincome, incomeGroupRow.maxincome);
		} else {
			result = incomeGroupRow.minincome + r.nextInt(incomeGroupRow.maxincome - incomeGroupRow.minincome);
		}
		return result;
	}
}
