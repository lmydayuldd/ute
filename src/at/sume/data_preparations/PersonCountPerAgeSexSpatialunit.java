/**
 * 
 */
package at.sume.data_preparations;

import java.sql.ResultSet;
import java.sql.SQLException;


import net.remesch.util.Database;


/**
 * @author Alexander Remesch
 *
 */
public class PersonCountPerAgeSexSpatialunit extends DatabaseRecord {

	private long spatialunitId;
	private short sex;
	private short ageGroupId;
	private long personCount;
	
	public long getSpatialunitId() {
		return spatialunitId;
	}

	public void setSpatialunitId(long spatialunitId) {
		this.spatialunitId = spatialunitId;
	}

	public short getSex() {
		return sex;
	}

	public void setSex(short sex) {
		if (sex < 1 || sex > 2)
			throw new IllegalArgumentException("sex must be in the range from 1 to 2");
		this.sex = sex;
	}

	public short getAgeGroupId() {
		return ageGroupId;
	}

	public void setAgeGroupId(short ageGroupId) {
		if (ageGroupId < 1 || ageGroupId > 16)
			throw new IllegalArgumentException("age-group must be in the range from 1 to 16");
		this.ageGroupId = ageGroupId;
	}

	public long getPersonCount() {
		return personCount;
	}

	public void setPersonCount(long personCount) {
		this.personCount = personCount;
	}

	public PersonCountPerAgeSexSpatialunit(Database pdb) throws SQLException {
		super(pdb);
	}

	/* (non-Javadoc)
	 * @see at.sume.db_wrapper.DatabaseRecord#populate(java.sql.ResultSet)
	 */
	@Override
	public void populate(ResultSet rs) throws SQLException {
		setSpatialunitId(rs.getLong("SpatialUnitId"));
		setSex(rs.getShort("Sex"));
		setPersonCount(rs.getLong("PersonCount"));
		setAgeGroupId((short) rs.getLong("AgeGroupId"));
	}

}
