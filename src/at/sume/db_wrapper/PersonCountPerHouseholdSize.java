/**
 * 
 */
package at.sume.db_wrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.remesch.util.Database;


/**
 * @author ar
 *
 */
public class PersonCountPerHouseholdSize extends DatabaseRecord {

	private long spatialunitId;
	private long householdSize;
	private long householdCount;
	private long personCount;

	/**
	 * @return the spatialunitId
	 */
	public long getSpatialunitId() {
		return spatialunitId;
	}

	/**
	 * @param spatialunitId the spatialunitId to set
	 */
	public void setSpatialunitId(long spatialunitId) {
		this.spatialunitId = spatialunitId;
	}

	/**
	 * @return the householdSize
	 */
	public long getHouseholdSize() {
		return householdSize;
	}

	/**
	 * @param householdSize the householdSize to set
	 */
	public void setHouseholdSize(long householdSize) {
		this.householdSize = householdSize;
	}

	/**
	 * @return the householdCount
	 */
	public long getHouseholdCount() {
		return householdCount;
	}

	/**
	 * @param householdCount the householdCount to set
	 */
	public void setHouseholdCount(long householdCount) {
		this.householdCount = householdCount;
	}

	/**
	 * @return the personCount
	 */
	public long getPersonCount() {
		return personCount;
	}

	/**
	 * @param personCount the personCount to set
	 */
	public void setPersonCount(long personCount) {
		this.personCount = personCount;
	}

	public PersonCountPerHouseholdSize(Database pdb) throws SQLException {
		super(pdb);
	}

	/* (non-Javadoc)
	 * @see at.sume.db_wrapper.DatabaseRecord#populate(java.sql.ResultSet)
	 */
	@Override
	public void populate(ResultSet rs) throws SQLException {
		setSpatialunitId(rs.getLong("SpatialUnitId"));
		setHouseholdSize(rs.getLong("HouseholdSize"));
		setHouseholdCount(rs.getLong("HouseholdCount"));
		setPersonCount(rs.getLong("PersonCount"));
	}

}
