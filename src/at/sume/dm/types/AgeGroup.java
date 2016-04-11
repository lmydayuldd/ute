/**
 * 
 */
package at.sume.dm.types;

import java.sql.SQLException;
import java.util.ArrayList;
import net.remesch.util.Random;

import at.sume.dm.Common;

/**
 * This class is used to convert between age and age-groups through the data given in table
 * MZ_AgeGroups
 *
 * @author Alexander Remesch
 */
public class AgeGroup {
	public static class AgeGroupRow implements Comparable<AgeGroupRow> {
		public byte ageGroupId;
		public String ageGroup;
		public short minage;
		public short maxage;
		
		public String toString() {
			return ageGroup;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(AgeGroupRow arg0) {
			return ((Byte)ageGroupId).compareTo(arg0.ageGroupId);
		}
	}
	static ArrayList<AgeGroupRow> ageGroups;
	static {
		String selectStatement = "select ageGroupId, ageGroup, minAge, maxAge from MZ_AgeGroups order by ageGroupId";
		try {
			ageGroups = Common.db.select(AgeGroupRow.class, selectStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Get the age group for a certain age 
	 * @param age
	 * @return
	 */
	public static byte getAgeGroupId(short age) {
		for (AgeGroupRow i : ageGroups) {
			if ((i.minage <= age) && (age <= i.maxage)) {
				return i.ageGroupId;
			}
		}
		throw new AssertionError("Unable to find age group for age " + age);
	}
	/**
	 * Get the printable name of a certain age group by binarySearch
	 * @param ageGroupId
	 * @return
	 */
	public static String getAgeGroupName(byte ageGroupId) {
//		assert ageGroupId > 0 : "ageGroupId <= 0";
//		assert ageGroupId <= ageGroups.size() : "ageGroupId > " + ageGroups.size();
//		AgeGroupRow lookup = new AgeGroupRow();
//		lookup.ageGroupId = ageGroupId;
//		int pos = Collections.binarySearch(ageGroups, lookup);
//		return ageGroups.get(pos).ageGroup;
		return getAgeGroupNameDirect(ageGroupId);
	}
	/**
	 * Get the printable name of a certain age group by direct access to ArrayList
	 * NOTE: Requires continuously ascending ageGroupId
	 * @param ageGroupId
	 * @return
	 */
	public static String getAgeGroupNameDirect(byte ageGroupId) {
		assert ageGroupId > 0 : "ageGroupId <= 0";
		assert ageGroupId <= ageGroups.size() : "ageGroupId > " + ageGroups.size();
		return ageGroups.get(ageGroupId - 1).ageGroup;
	}
	/**
	 * Sample an actual age for a given age group by random
	 * @param ageGroupId
	 * @return
	 */
	public static short sampleAge(byte ageGroupId) {
		assert ageGroupId > 0 : "ageGroupId <= 0";
		assert ageGroupId <= ageGroups.size() : "ageGroupId > " + ageGroups.size();
		Random r = new Random();
		AgeGroupRow sample = ageGroups.get(ageGroupId - 1);
		if (ageGroupId == ageGroups.size()) {
			// sample the lowest possible age in the highest age group
			return sample.minage;
		} else {
			return (short) (sample.minage + r.nextInt(sample.maxage - sample.minage));
		}
	}
	public static short sampleAge(byte ageGroupId, short minAge) {
		assert ageGroupId > 0 : "ageGroupId <= 0";
		assert ageGroupId <= ageGroups.size() : "ageGroupId > " + ageGroups.size();
		Random r = new Random();
		AgeGroupRow sample = ageGroups.get(ageGroupId - 1);
		if (ageGroupId == ageGroups.size()) {
			// sample the lowest possible age in the highest age group
			return sample.minage;
		} else {
			if (minAge < sample.minage)
				minAge = sample.minage;
			if (minAge > sample.maxage)
				return (short) sample.maxage;
			return (short) (minAge + r.nextInt(sample.maxage - minAge));
		}
	}
	public static short getMinAge(byte ageGroupId) {
		return ageGroups.get(ageGroupId - 1).minage;
	}
	public static short getMaxAge(byte ageGroupId) {
		return ageGroups.get(ageGroupId - 1).maxage;
	}
}
