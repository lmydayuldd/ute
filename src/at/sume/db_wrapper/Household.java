package at.sume.db_wrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.util.Database;


/**
 * Database wrapper class for one record of table _DM_Households
 * 
 * @author Alexander Remesch
 * 
 */
public class Household extends DatabaseRecord {
	
	private long householdId;
	private long spatialunitId;
	private short householdSize;
	private long dwellingId;
	private ArrayList<Person> personList;

	public Household(Database pdb) throws SQLException {
		super(pdb);
		String sqlx = "insert into _DM_Households (HouseholdId, SpatialunitId, HouseholdSize) values (?, ?, ?)";
		prepareStatement(sqlx);
	}

	public long getHouseholdId() {
		return householdId;
	}
	public void setHouseholdId(long householdId) throws SQLException {
		this.householdId = householdId;
		ps.setString(1, Long.toString(householdId));
	}
	public long getSpatialunitId() {
		return spatialunitId;
	}
	public void setSpatialunitId(long spatialunitId) throws SQLException {
		this.spatialunitId = spatialunitId;
		ps.setString(2, Long.toString(spatialunitId));
	}
	public short getHouseholdSize() {
		return householdSize;
	}
	public void setHouseholdSize(short householdSize) throws IllegalArgumentException, SQLException {
		if (householdSize < 1 || householdSize > 4)
			throw new IllegalArgumentException("householdSize must be in the range from 1 to 4");
		this.householdSize = householdSize;
		ps.setString(3, Long.toString(householdSize));
	}
	public long getDwellingId() {
		return dwellingId;
	}
	public void setDwellingId(long dwellingId) {
		this.dwellingId = dwellingId;
		// TODO: set internal representation
	}
	
	/**
	 * @param personList the personList to set
	 */
	public void setPersonList(ArrayList<Person> personList) {
		this.personList = personList;
	}

	/**
	 * @return the personList
	 */
	public ArrayList<Person> getPersonList() {
		return personList;
	}

	public void createPersonList() {
		this.personList = new ArrayList<Person>();
	}
	
	public void addHouseholdMember(Person member) {
		personList.add(member);
	}
	
	public void dbInsert(long householdId, long spatialunitId, short householdSize) throws SQLException {
		setHouseholdId(householdId);
		setSpatialunitId(spatialunitId);
		setHouseholdSize(householdSize);
		dbInsert();
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
