/**
 * 
 */
package at.sume.sampling.distributions;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.db.RecordSetRow;
import at.sume.sampling.SampleHouseholdLivingSpace;

/**
 * @author Alexander Remesch
 *
 */
public class LivingSpaceDistributionRow extends RecordSetRow<SampleHouseholdLivingSpace> {
	private short householdSize;
	private short livingSpaceGroup;
	private int minLivingSpace;
	private int maxLivingSpace;

	/**
	 * @param rowList
	 */
	public LivingSpaceDistributionRow(SampleHouseholdLivingSpace rowList) {
		super(rowList);
	}

	/**
	 * @return the householdSize
	 */
	public short getHouseholdSize() {
		return householdSize;
	}

	/**
	 * @param householdSize the householdSize to set
	 */
	public void setHouseholdSize(short householdSize) {
		this.householdSize = householdSize;
	}

	/**
	 * @return the livingSpaceGroup
	 */
	public short getLivingSpaceGroup() {
		return livingSpaceGroup;
	}

	/**
	 * @param livingSpaceGroup the livingSpaceGroup to set
	 */
	public void setLivingSpaceGroup(short livingSpaceGroup) {
		this.livingSpaceGroup = livingSpaceGroup;
	}

	/**
	 * @return the minLivingSpace
	 */
	public int getMinLivingSpace() {
		return minLivingSpace;
	}

	/**
	 * @param minLivingSpace the minLivingSpace to set
	 */
	public void setMinLivingSpace(int minLivingSpace) {
		this.minLivingSpace = minLivingSpace;
	}

	/**
	 * @return the maxLivingSpace
	 */
	public int getMaxLivingSpace() {
		return maxLivingSpace;
	}

	/**
	 * @param maxLivingSpace the maxLivingSpace to set
	 */
	public void setMaxLivingSpace(int maxLivingSpace) {
		this.maxLivingSpace = maxLivingSpace;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#loadFromDatabase(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("HouseholdSize")) {
			setHouseholdSize(rs.getShort(name));
		} else if (name.equals("LivingSpaceGroup")) {
			setLivingSpaceGroup(rs.getShort(name));
		} else if (name.equals("MinSpace")) {
			setMinLivingSpace(rs.getInt(name));
		} else if (name.equals("MaxSpace")) {
			setMaxLivingSpace(rs.getInt(name));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}
}
