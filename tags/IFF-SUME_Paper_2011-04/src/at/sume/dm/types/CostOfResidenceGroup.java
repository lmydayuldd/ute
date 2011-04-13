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
public class CostOfResidenceGroup {
	public static class CostOfResidenceGroupRow implements Comparable<CostOfResidenceGroupRow> {
		public short costOfResidenceGroupId;
		public String costOfResidenceGroup;
		public double minCosts;
		public double maxCosts;
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(CostOfResidenceGroupRow arg0) {
			return ((Short)costOfResidenceGroupId).compareTo(arg0.costOfResidenceGroupId);		}
	}

	static ArrayList<CostOfResidenceGroupRow> costOfResidenceGroups;
	
	static {
		String selectStatement = "select costOfResidenceGroupId, costOfResidenceGroup, minCosts, maxCosts from ISIS_CostOfResidenceGroups order by costOfResidenceGroupId";
		try {
			costOfResidenceGroups = Common.db.select(CostOfResidenceGroupRow.class, selectStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Get the cost of residence group for a certain cost of residence 
	 * @param costOfResidence
	 * @return
	 */
	public static short getCostOfResidenceGroupId(double costOfResidence) {
		for (CostOfResidenceGroupRow i : costOfResidenceGroups) {
			if ((i.minCosts <= costOfResidence) && (costOfResidence <= i.maxCosts)) {
				return i.costOfResidenceGroupId;
			}
		}
		throw new AssertionError("no cost of residence group found for " + costOfResidence);
	}
	/**
	 * Get the printable name of a certain cost of residence group by direct access to ArrayList
	 * NOTE: Requires continuously ascending costOfResidenceGroupId
	 * @param costOfResidenceGroupId
	 * @return
	 */
	public static String getCostOfResidenceGroupName(short costOfResidenceGroupId) {
		assert costOfResidenceGroupId > 0 : "costOfResidenceGroupId <= 0";
		assert costOfResidenceGroupId <= costOfResidenceGroups.size() : "costOfResidenceGroupId > " + costOfResidenceGroups.size();
		return costOfResidenceGroups.get(costOfResidenceGroupId - 1).costOfResidenceGroup;
	}
	public static double sampleCostOfResidence(short costOfResidenceGroupId) {
		assert costOfResidenceGroupId > 0 : "costOfResidenceGroupId <= 0";
		assert costOfResidenceGroupId <= costOfResidenceGroups.size() : "costOfResidenceGroupId > " + costOfResidenceGroups.size();
		Random r = new Random();
		CostOfResidenceGroupRow sample = costOfResidenceGroups.get(costOfResidenceGroupId - 1);
		return (short) (sample.minCosts + (r.nextDouble() * (sample.maxCosts - sample.minCosts)));
	}
}
