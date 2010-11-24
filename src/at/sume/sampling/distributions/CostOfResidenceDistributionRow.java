/**
 * 
 */
package at.sume.sampling.distributions;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.db.RecordSetRow;
import at.sume.sampling.SampleHouseholdCostOfResidence;

/**
 * @author Alexander Remesch
 *
 */
public class CostOfResidenceDistributionRow extends RecordSetRow<SampleHouseholdCostOfResidence> {
	private short livingSpaceGroupId;
	private short costOfResidenceGroupId;
	private int minCostOfResidence;
	private int maxCostOfResidence;
	
	/**
	 * @return the livingSpaceGroupId
	 */
	public short getLivingSpaceGroupId() {
		return livingSpaceGroupId;
	}

	/**
	 * @param livingSpaceGroupId the livingSpaceGroupId to set
	 */
	public void setLivingSpaceGroupId(short livingSpaceGroupId) {
		this.livingSpaceGroupId = livingSpaceGroupId;
	}

	/**
	 * @return the costOfResidenceGroupId
	 */
	public short getCostOfResidenceGroupId() {
		return costOfResidenceGroupId;
	}

	/**
	 * @param costOfResidenceGroupId the costOfResidenceGroupId to set
	 */
	public void setCostOfResidenceGroupId(short costOfResidenceGroupId) {
		this.costOfResidenceGroupId = costOfResidenceGroupId;
	}

	/**
	 * @return the minCostOfResidence
	 */
	public int getMinCostOfResidence() {
		return minCostOfResidence;
	}

	/**
	 * @param minCostOfResidence the minCostOfResidence to set
	 */
	public void setMinCostOfResidence(int minCostOfResidence) {
		this.minCostOfResidence = minCostOfResidence;
	}

	/**
	 * @return the maxCostOfResidence
	 */
	public int getMaxCostOfResidence() {
		return maxCostOfResidence;
	}

	/**
	 * @param maxCostOfResidence the maxCostOfResidence to set
	 */
	public void setMaxCostOfResidence(int maxCostOfResidence) {
		this.maxCostOfResidence = maxCostOfResidence;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#loadFromDatabase(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("LivingSpaceGroupId")) {
			setLivingSpaceGroupId(rs.getShort(name));
		} else if (name.equals("CostOfResidenceGroupId")) {
			setCostOfResidenceGroupId(rs.getShort(name));
		} else if (name.equals("MinCosts")) {
			setMinCostOfResidence(rs.getInt(name));
		} else if (name.equals("MaxCosts")) {
			setMaxCostOfResidence(rs.getInt(name));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}
}
