/**
 * 
 */
package at.sume.dm.types;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import at.sume.dm.Common;


/**
 * @author Alexander Remesch
 *
 */
public class LivingSpaceGroup {
	public static class LivingSpaceGroupRow implements Comparable<LivingSpaceGroupRow> {
		public short livingSpaceGroupId;
		public String livingSpaceGroup;
		public int minSpace;
		public int maxSpace;
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(LivingSpaceGroupRow arg0) {
			return ((Short)livingSpaceGroupId).compareTo(arg0.livingSpaceGroupId);
		}
		
	}

	static ArrayList<LivingSpaceGroupRow> livingSpaceGroups;
	
	static {
		String selectStatement = "select livingSpaceGroupId, livingSpaceGroup, minSpace, maxSpace from _DM_LivingSpaceGroup6 order by livingSpaceGroupId";
		try {
			livingSpaceGroups = Common.db.select(LivingSpaceGroupRow.class, selectStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Get the living space group for a certain living space 
	 * @param livingSpace
	 * @return
	 */
	public static short getLivingSpaceGroupId(double livingSpace) {
		for (LivingSpaceGroupRow i : livingSpaceGroups) {
			if ((i.minSpace <= livingSpace) && (livingSpace <= i.maxSpace)) {
				return i.livingSpaceGroupId;
			}
		}
		return 0;
	}
	/**
	 * Get the printable name of a certain living space group by direct access to ArrayList
	 * NOTE: Requires continuously ascending livingSpaceGroupId
	 * @param livingSpaceGroupId
	 * @return
	 */
	public static String getLivingSpaceGroupName(short livingSpaceGroupId) {
		assert livingSpaceGroupId > 0 : "livingSpaceGroupId <= 0";
		assert livingSpaceGroupId <= livingSpaceGroups.size() : "livingSpaceGroupId > " + livingSpaceGroups.size();
		return livingSpaceGroups.get(livingSpaceGroupId - 1).livingSpaceGroup;
	}
	public static int sampleLivingSpace(short livingSpaceGroupId) {
		assert livingSpaceGroupId > 0 : "livingSpaceGroupId <= 0";
		assert livingSpaceGroupId <= livingSpaceGroups.size() : "livingSpaceGroupId > " + livingSpaceGroups.size();
		Random r = new Random();
		LivingSpaceGroupRow sample = livingSpaceGroups.get(livingSpaceGroupId - 1);
		return (short) (sample.minSpace + r.nextInt(sample.maxSpace - sample.minSpace));
	}
}
