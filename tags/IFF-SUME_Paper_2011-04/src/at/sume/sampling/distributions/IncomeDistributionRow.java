/**
 * 
 */
package at.sume.sampling.distributions;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.db.RecordSetRow;
import at.sume.sampling.SamplePersonIncome;

/**
 * Income distribution for Monte Carlo-sampling of synthetic persons, data from table "_DM_Income per age, sex, spatial unit"
 *
 * @author Alexander Remesch
 */
public class IncomeDistributionRow extends RecordSetRow<SamplePersonIncome> {
	private long spatialUnitId;
	private short sex;
	private short ageGroupId;
	private short incomeGroup;
	private long minIncome;
	private long maxIncome;
	
	/**
	 * @return the spatialUnitId
	 */
	public long getSpatialUnitId() {
		return spatialUnitId;
	}

	/**
	 * @param spatialUnitId the spatialUnitId to set
	 */
	public void setSpatialUnitId(long spatialUnitId) {
		this.spatialUnitId = spatialUnitId;
	}

	/**
	 * @return the sex
	 */
	public short getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(short sex) {
		this.sex = sex;
	}

	/**
	 * @return the ageGroupId
	 */
	public short getAgeGroupId() {
		return ageGroupId;
	}

	/**
	 * @param ageGroupId the ageGroupId to set
	 */
	public void setAgeGroupId(short ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	/**
	 * @return the incomeGroup
	 */
	public short getIncomeGroup() {
		return incomeGroup;
	}

	/**
	 * @param incomeGroup the incomeGroup to set
	 */
	public void setIncomeGroup(short incomeGroup) {
		this.incomeGroup = incomeGroup;
	}

	/**
	 * @return the minIncome
	 */
	public long getMinIncome() {
		return minIncome;
	}

	/**
	 * @param minIncome the minIncome to set
	 */
	public void setMinIncome(long minIncome) {
		this.minIncome = minIncome;
	}

	/**
	 * @return the maxIncome
	 */
	public long getMaxIncome() {
		return maxIncome;
	}

	/**
	 * @param maxIncome the maxIncome to set
	 */
	public void setMaxIncome(long maxIncome) {
		this.maxIncome = maxIncome;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#loadFromDatabase(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("SpatialunitId_AD")) {
			setSpatialUnitId(rs.getLong(name));
		} else if (name.equals("Sex")) {
			setSex(rs.getShort(name));
		} else if (name.equals("AgeGroupId")) {
			setAgeGroupId(rs.getShort(name));
		} else if (name.equals("IncomeGroupId")) {
			setIncomeGroup(rs.getShort(name));
		} else if (name.equals("MinIncome")) {
			setMinIncome(rs.getLong(name));
		} else if (name.equals("MaxIncome")) {
			setMaxIncome(rs.getLong(name));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}
}
