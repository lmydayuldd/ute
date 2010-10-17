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
		public short id;
		public long minincome;
		public long maxincome;
	}
	static ArrayList<IncomeGroupRow> incomeGroups;
	static {
//		incomeGroups = new ArrayList<IncomeGroupRow>();
		String selectStatement = "select id, minincome, maxincome from _DM_IncomeGroup order by id";
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
	public static short getIncomeGroupId(long income) {
		for (IncomeGroupRow i : incomeGroups) {
			if ((i.minincome <= income) && (income <= i.maxincome)) {
				return i.id;
			}
		}
		return 0;
	}
}
