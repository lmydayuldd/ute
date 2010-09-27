/**
 * 
 */
package at.sume.data_preparations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;


import net.remesch.util.Database;


/**
 * Database wrapper class for one record of table _DM_Perrsons per age, sex, hh-size, personnr, spatial unit
 * 
 * @author ar
 *
 */
public class PersonCountPerAgeSexHouseholdsizePersonnr extends
		DatabaseRecord {
	// TODO: simplify naming!!!!
	// TODO: resolve very similar class name with at.sume.distributions!!!

	private long id;
	private long spatialunitId;
	private short sex;
	private long ageGroupId;
	private short householdSize;
	private short personNr;
	private double personCount;

	/**
	 * @param pdb
	 * @throws SQLException
	 */
	public PersonCountPerAgeSexHouseholdsizePersonnr(Database pdb)
			throws SQLException {
		super(pdb);
		String sqlx = "insert into [_DM_Persons per age, sex, hh-size, personnr, spatial unit] (Id, SpatialunitId, Sex, AgeGroupId, HouseholdSize, PersonNr, PersonCount) " +
			"values (?, ?, ?, ?, ?, ?, ?)";
		prepareStatement(sqlx);
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 * @throws SQLException 
	 */
	public void setId(long id) throws SQLException {
		this.id = id;
		ps.setString(1, Long.toString(id));
	}

	/**
	 * @return the spatialunitId
	 */
	public long getSpatialunitId() {
		return spatialunitId;
	}

	/**
	 * @param spatialunitId the spatialunitId to set
	 * @throws SQLException 
	 */
	public void setSpatialunitId(long spatialunitId) throws SQLException {
		this.spatialunitId = spatialunitId;
		ps.setString(2, Long.toString(spatialunitId));
	}

	/**
	 * @return the sex
	 */
	public short getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 * @throws SQLException 
	 */
	public void setSex(short sex) throws SQLException {
		if (sex < 1 || sex > 2)
			throw new IllegalArgumentException("sex must be in the range from 1 to 2");
		this.sex = sex;
		ps.setString(3, Short.toString(sex));
	}

	/**
	 * @return the ageGroupId
	 */
	public long getAgeGroupId() {
		return ageGroupId;
	}

	/**
	 * @param ageGroupId the ageGroupId to set
	 * @throws SQLException 
	 */
	public void setAgeGroupId(long ageGroupId) throws SQLException {
		if (ageGroupId < 1 || ageGroupId > 16)
			throw new IllegalArgumentException("ageGroupId must be in the range from 1 to 16");
		this.ageGroupId = ageGroupId;
		ps.setString(4, Long.toString(ageGroupId));
	}

	/**
	 * @return the householdSize
	 */
	public short getHouseholdSize() {
		return householdSize;
	}

	/**
	 * @param householdSize the householdSize to set
	 * @throws SQLException 
	 */
	public void setHouseholdSize(short householdSize) throws SQLException {
		if (householdSize < 1 || householdSize > 4)
			throw new IllegalArgumentException("householdSize must be in the range from 1 to 4");
		this.householdSize = householdSize;
		ps.setString(5, Short.toString(householdSize));
	}

	/**
	 * @return the personNr
	 */
	public short getPersonNr() {
		return personNr;
	}

	/**
	 * @param personNr the personNr to set
	 * @throws SQLException 
	 */
	public void setPersonNr(short personNr) throws SQLException {
		this.personNr = personNr;
		ps.setString(6, Short.toString(personNr));
	}

	/**
	 * @return the personCount
	 */
	public double getPersonCount() {
		return personCount;
	}

	/**
	 * @param personCount the personCount to set
	 * @throws SQLException 
	 */
	public void setPersonCount(double personCount) throws SQLException {
		this.personCount = personCount;
		//ps.setString(7, Double.toString(personCount));
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
		ps.setString(7, twoPlaces.format(personCount));
	}

	/* (non-Javadoc)
	 * @see at.sume.db_wrapper.DatabaseRecord#populate(java.sql.ResultSet)
	 */
	@Override
	public void populate(ResultSet rs) throws SQLException {
		setSpatialunitId(rs.getLong("SpatialUnitId"));
		setSex(rs.getShort("Sex"));
		setAgeGroupId(rs.getLong("AgeGroupId"));
		setHouseholdSize(rs.getShort("HouseholdSize"));
		setPersonNr(rs.getShort("PersonNr"));
		setPersonCount(rs.getLong("PersonCount"));
	}

}
