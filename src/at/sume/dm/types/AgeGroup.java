/**
 * 
 */
package at.sume.dm.types;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.Common;

/**
 * This class is used to convert between age and age-groups through the data given in table
 * MZ_AgeGroups
 *
 * @author Alexander Remesch
 */
public class AgeGroup {
	public static class AgeGroupRow implements Comparable<AgeGroupRow> {
		public short ageGroupId;
		public String ageGroup;
		public long minage;
		public long maxage;
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(AgeGroupRow arg0) {
			return ((Short)ageGroupId).compareTo(arg0.ageGroupId);
		}
	}
	static ArrayList<AgeGroupRow> ageGroups;
	static {
		String selectStatement = "select ageGroupId, ageGroup, minAge, maxAge from MZ_AgeGroups order by ageGroupId";
		try {
			ageGroups = Common.db.select(AgeGroupRow.class, selectStatement);
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
	 * Get the age group for a certain age 
	 * @param age
	 * @return
	 */
	public static short getAgeGroupId(long age) {
		for (AgeGroupRow i : ageGroups) {
			if ((i.minage <= age) && (age <= i.maxage)) {
				return i.ageGroupId;
			}
		}
		return 0;
	}
	/**
	 * Get the printable name of a certain age group by binarySearch
	 * @param ageGroupId
	 * @return
	 */
	public static String getAgeGroupName(short ageGroupId) {
		assert ageGroupId > 0 : "ageGroupId <= 0";
		assert ageGroupId <= ageGroups.size() : "ageGroupId > " + ageGroups.size();
		AgeGroupRow lookup = new AgeGroupRow();
		lookup.ageGroupId = ageGroupId;
		int pos = Collections.binarySearch(ageGroups, lookup);
		return ageGroups.get(pos).ageGroup; 
	}
	/**
	 * Get the printable name of a certain age group by direct access to ArrayList
	 * NOTE: Requires continuously ascending ageGroupId
	 * @param ageGroupId
	 * @return
	 */
	public static String getAgeGroupNameDirect(short ageGroupId) {
		assert ageGroupId > 0 : "ageGroupId <= 0";
		assert ageGroupId <= ageGroups.size() : "ageGroupId > " + ageGroups.size();
		return ageGroups.get(ageGroupId - 1).ageGroup;
	}
}
