package at.sume.sampling;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import net.remesch.util.Database;

import at.sume.distributions.PersonsPerAgeSexHouseholdsizePersonnr;

public class SamplePersons {
	private static Distribution<PersonsPerAgeSexHouseholdsizePersonnr> persons;
	
	/**
	 * Load distribution of households per spatial unit from database
	 * @throws SQLException 
	 */
	public static void LoadDistribution(Database db, short personNr, short householdSize, long spatialUnit) throws SQLException
	{
		int rowcount = 0;

		ResultSet rs = db.executeQuery("SELECT AgeGroupId, Sex, PersonCount FROM [_DM_Persons per age, sex, hh-size, personnr, spatial unit] " +
				"WHERE PersonNr = " + personNr + " AND HouseholdSize = " + householdSize + " AND SpatialunitId = " + spatialUnit + " AND PersonCount <> 0 " +
				"ORDER BY AgeGroupId, Sex;");
		
		//		try {
//			rs.last();
//			rowcount = rs.getRow();
//			rs.first();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// Transfer ResultSet to ArrayList
		persons = new Distribution<PersonsPerAgeSexHouseholdsizePersonnr>(rowcount);
				
		while (rs.next())
		{
			PersonsPerAgeSexHouseholdsizePersonnr p = new PersonsPerAgeSexHouseholdsizePersonnr();
			p.setPersonCountRunningTotal(rs.getDouble("PersonCount"));
			p.setSex(rs.getShort("Sex"));
			p.setAgeGroupId(rs.getShort("AgeGroupId"));
			persons.add(Math.round(p.getPersonCountRunningTotal()), p);
		}
	}

	public static void FreeDistribution() {
		if (persons != null)
			persons.clear();
	}
	
	/**
	 * Return index of household-location based on given distribution
	 * @return index of list of spatial units with household-numbers
	 */
	public static int determineLocationIndex()
	{
		return persons.randomSample();
	}
	
	public static PersonsPerAgeSexHouseholdsizePersonnr GetPersonData(int index)
	{
		return persons.get(index);
	}
	
	public static long getNrHouseholdsTotalSum() {
		return persons.getMaxThreshold();
	}
}
