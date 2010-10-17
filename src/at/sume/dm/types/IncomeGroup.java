/**
 * 
 */
package at.sume.dm.types;

import java.sql.SQLException;
import java.util.ArrayList;

import at.sume.dm.Common;

/**
 * @author Alexander Remesch
 *
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
	
	static short getIncomeGroupId(long income) {
		for (IncomeGroupRow i : incomeGroups) {
			if ((i.minincome <= income) && (income <= i.maxincome)) {
				return i.id;
			}
		}
		return 0;
	}
}
