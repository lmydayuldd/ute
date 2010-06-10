package at.sume.db_wrapper;

import java.sql.SQLException;

import at.sume.generate_population.Database;

/**
 * Database wrapper class for one record of table _DM_Households
 * 
 * @author Alexander Remesch
 * 
 */
public class Household extends DatabaseRecord {
	
	private long HouseholdId;
	private long SpatialunitId;
	private short HouseholdSize;
	private long DwellingId;

	public Household(Database pdb) throws SQLException {
		super(pdb);
		db = pdb;
		String sqlx = "insert into _DM_Households (HouseholdId, SpatialunitId, HouseholdSize) values (?, ?, ?)";
		prepareStatement(sqlx);
	}

	public long getHouseholdId() {
		return HouseholdId;
	}
	public void setHouseholdId(long householdId) throws SQLException {
		HouseholdId = householdId;
		ps.setString(1, Long.toString(householdId));
	}
	public long getSpatialunitId() {
		return SpatialunitId;
	}
	public void setSpatialunitId(long spatialunitId) throws SQLException {
		SpatialunitId = spatialunitId;
		ps.setString(2, Long.toString(spatialunitId));
}
	public short getHouseholdSize() {
		return HouseholdSize;
	}
	public void setHouseholdSize(short householdSize) throws IllegalArgumentException, SQLException {
		if (householdSize < 1 || householdSize > 4)
			throw new IllegalArgumentException("householdSize must be in the range from 1 to 4");
		HouseholdSize = householdSize;
		ps.setString(3, Long.toString(householdSize));
	}
	public long getDwellingId() {
		return DwellingId;
	}
	public void setDwellingId(long dwellingId) {
		DwellingId = dwellingId;
		// TODO: set internal representation
	}
	
	@Override
	public void dbInsert() throws SQLException {
		ps.executeUpdate();
	}
	
	public void dbInsert(long householdId, long spatialunitId, short householdSize) throws SQLException {
		setHouseholdId(householdId);
		setSpatialunitId(spatialunitId);
		setHouseholdSize(householdSize);
		dbInsert();
	}
}
