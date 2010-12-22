/**
 * 
 */
package at.sume.dm.types;

import java.sql.SQLException;
import java.util.ArrayList;

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
		for (IncomeGroupRow i : incomeGroups) {
			if ((i.minincome <= income) && (income <= i.maxincome)) {
				return i.id;
			}
		}
		return 0;
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
}
