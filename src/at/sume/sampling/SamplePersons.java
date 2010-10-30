package at.sume.sampling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.remesch.db.Database;
import at.sume.sampling.distributions.PersonsPerAgeSexHouseholdsizePersonnr;

public class SamplePersons {
	private Distribution<PersonsPerAgeSexHouseholdsizePersonnr> persons;
	
	/**
	 * Load distribution of households per spatial unit from database
	 * @throws SQLException 
	 */
	public void LoadDistribution(Database db, short personNr, short householdSize, long spatialUnit) throws SQLException
	{
		int rowcount = 0;
		PreparedStatement ps = db.con.prepareStatement("SELECT distr.AgeGroupId, Sex, PersonCount, MinAge, MaxAge FROM [_DM_Persons per age, sex, hh-size, personnr, spatial unit] AS distr " +
				"INNER JOIN MZ_AgeGroups ON (distr.AgeGroupId = MZ_AgeGroups.AgeGroupId) " +
				"WHERE PersonNr = ? AND HouseholdSize = ? AND SpatialunitId = ? AND PersonCount <> 0 " +
				"ORDER BY distr.AgeGroupId, Sex;");
		ps.setString(1, Short.toString(personNr));
		ps.setString(2, Short.toString(householdSize));
		ps.setString(3, Long.toString(spatialUnit));
		ResultSet rs = ps.executeQuery();
		
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
			p.setMinAge(rs.getShort("MinAge"));
			p.setMaxAge(rs.getShort("MaxAge"));
			persons.add(Math.round(p.getPersonCountRunningTotal()), p);
		}
		rs.close();
	}

	public void FreeDistribution() {
		if (persons != null)
			persons.clear();
	}
	
	/**
	 * Return index of household-location based on given distribution
	 * @return index of list of spatial units with household-numbers
	 */
	public int determinePersonDataIndex()
	{
		return persons.randomSample();
	}
	
	public PersonsPerAgeSexHouseholdsizePersonnr getPersonData(int index)
	{
		return persons.get(index);
	}
	
	public long getNrHouseholdsTotalSum() {
		return persons.getMaxThreshold();
	}
}
