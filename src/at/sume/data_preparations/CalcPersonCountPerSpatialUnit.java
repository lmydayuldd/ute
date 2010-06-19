package at.sume.data_preparations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.util.LeastSquareAdjustment;

import at.sume.distributions.PersonsPerAgeSexHouseholdsizePersonnr;
import at.sume.generate_population.Common;
import at.sume.generate_population.Database;
import at.sume.sampling.Distribution;

public class CalcPersonCountPerSpatialUnit {

	private static Distribution<PersonsPerAgeSexHouseholdsizePersonnr> personCount;
	
	/**
	 * Load distribution of households per spatial unit from database
	 * @throws SQLException 
	 */
	public static void LoadDistribution(Database db) throws SQLException
	{
		int rowcount = 288;
		ResultSet rs = db.executeQuery("SELECT HouseholdSize, PersonNr, Sex, AgeGroup, PersonCount " +
				"FROM [MZ_2006-2008_Persons per age, sex, hh-size, personnr recoded] " +
				"ORDER BY HouseholdSize, PersonNr, Sex, AgeGroup;");
		
		// Transfer ResultSet to Distribution
		personCount = new Distribution<PersonsPerAgeSexHouseholdsizePersonnr>(rowcount);
				
		while (rs.next())
		{
			PersonsPerAgeSexHouseholdsizePersonnr p = new PersonsPerAgeSexHouseholdsizePersonnr();
			p.setId(rs.getLong("ID"));
			p.setSex(rs.getInt("Sex"));
			p.setAgeGroup(rs.getInt("AgeGroup"));
			p.setHouseholdSize(rs.getInt("HouseholdSize"));
			p.setPersonNrInHousehold(rs.getInt("PersonNr"));
			p.setPersonCount(rs.getDouble("PersonCount"));
			personCount.add((long) p.getPersonCount(), p);
		}
	}

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) {
		Database db = new Database(Common.GetDbLocation());

		// a) determine array size
		// TODO: get this from db!
		int xmt = 10, ymt = 33;
		int yl = ymt - 1, xl = xmt - 1;
		double n[][] = new double[xmt][ymt];
		double m[][] = new double[xmt][ymt];

		// 1) Daten laden
		try {
			// a) Verteilung
			LoadDistribution(db);
			// b) Verteilung von ArrayList -> java array
			for (int x = 0; x != xl; x++) {
				for (int y = 0; y != yl; y++) {
					m[x][y] = personCount.get(x + y).getPersonCount();
				}
			}
			// c) fill in marginal totals
			LeastSquareAdjustment.calc_marginal_totals(m);
			// d) get target marginal totals from database
			//    - number of persons per spatial unit, sex and age-group
			try {
				// TODO: transformiere Tabelle in relationale Form und schreibe die passende Funktion für Array-Überträge!
				db.executeQuery("SELECT Geschlecht, [0-4]+[5-9]+[10-14] AS [0-14], [15-19], [20-24], [25-29], [30-34], [35-39], [40-44], " +
						"[45-49], [50-54], [55-59], [60-64], [65-69], [70-74], [75-79], [80-84], [85-] " +
						"FROM [VZ_2001_Alter_Geschlecht_Familienstand (ZB)] " +
						"WHERE GKZ='901 01' AND Geschlecht='weiblich';", n[xmt][0], n[xmt][1]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//    - number of persons per spatial unit, household-size and person-nr. in household
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 2) Least Square Adjustment
		
		// 3) Daten in Tabelle schreiben
		

	}

}
