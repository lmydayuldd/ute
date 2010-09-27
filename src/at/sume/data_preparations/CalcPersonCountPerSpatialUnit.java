package at.sume.data_preparations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import net.remesch.util.Database;
import net.remesch.util.LeastSquareAdjustment;

import at.sume.dm.Common;
import at.sume.sampling.Distribution;
import at.sume.sampling.distributions.PersonsPerAgeSexHouseholdsizePersonnr;

public class CalcPersonCountPerSpatialUnit {

	private static Distribution<PersonsPerAgeSexHouseholdsizePersonnr> personCount;
	
	/**
	 * Load distribution of households per spatial unit from database
	 * @throws SQLException 
	 */
	public static void LoadDistribution(Database db) throws SQLException
	{
		int rowcount = 320;
		ResultSet rs = db.executeQuery("SELECT Id, HouseholdSize, PersonNr, Sex, AgeGroup, PersonCount " +
				"FROM [MZ_2006-2008_Persons per age, sex, hh-size, personnr recoded] " +
				"ORDER BY HouseholdSize, PersonNr, Sex, AgeGroup;");
		
		// Transfer ResultSet to Distribution
		personCount = new Distribution<PersonsPerAgeSexHouseholdsizePersonnr>(rowcount);
				
		while (rs.next()) {
			PersonsPerAgeSexHouseholdsizePersonnr p = new PersonsPerAgeSexHouseholdsizePersonnr();
			p.setId(rs.getLong("ID"));
			p.setSex(rs.getShort("Sex"));
			p.setAgeGroupId(rs.getShort("AgeGroup"));
			p.setHouseholdSize(rs.getShort("HouseholdSize"));
			p.setPersonNrInHousehold(rs.getShort("PersonNr"));
			p.setPersonCount(rs.getDouble("PersonCount"));
			personCount.add((long) p.getPersonCount(), p);
		}
	}

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) {
		Database db = new Database(Common.getDbLocation());

		// a) determine array size
		// TODO: get this from db!
		int xmt = 10, ymt = 32; // 10 * 32 Matrix (starting at 0) + 1 row/column for totals (below!)
		int yl = ymt, xl = xmt;
		double n[][] = new double[xmt + 1][ymt + 1];
		double m[][] = new double[xmt + 1][ymt + 1];

		// TODO: do for all spatial units!
		ResultSet spatialunits = db.executeQuery("SELECT SpatialunitId FROM [VZ_2001_Haushalte (ZB) relational] " +
													"GROUP BY [VZ_2001_Haushalte (ZB) relational].SpatialunitId;");
		//											"where spatialunitid = 91001 GROUP BY [VZ_2001_Haushalte (ZB) relational].SpatialunitId;");
		try {
			db.execute("delete * from [_DM_Persons per age, sex, hh-size, personnr, spatial unit]");
			long reccount = 1;
			while (spatialunits.next()) {
				long spatialunitid = spatialunits.getLong("SpatialUnitId");
				System.out.println(spatialunitid);
				// 1) Daten laden
				// a) Verteilung
				LoadDistribution(db);
				// b) Verteilung von ArrayList -> java array
				for (int x = 0; x != xl; x++) {
					for (int y = 0; y != yl; y++) {
						m[x][y] = personCount.get(x * yl + y).getPersonCount();
					}
				}
				// c) fill in marginal totals
				LeastSquareAdjustment.calc_marginal_totals(m);
				// d) get target marginal totals from database
				//    - number of persons per spatial unit, sex and age-group
				// TODO: Wie kann man das Folgende allgemeiner (für div. Tabellen und Queries) ausdrücken????
				// Vielleicht doch ein ORM - Tool???
				ResultSet rs = db.executeQuery("SELECT * from [VZ_2001_Alter_Geschlecht_Familienstand (ZB) relational] " +
						"where SpatialUnitId = " + spatialunitid +
						" order by Sex, AgeGroupId");
				ArrayList<PersonCountPerAgeSexSpatialunit> al = new ArrayList<PersonCountPerAgeSexSpatialunit>();
				while (rs.next()) {
					PersonCountPerAgeSexSpatialunit p = new PersonCountPerAgeSexSpatialunit(db);
					p.populate(rs);
					al.add(p);
				}
				// Fill into n[][]
				for (int y = 0; y != yl; y++) {
					n[xmt][y] = al.get(y).getPersonCount();
				}
				rs.close();
				//    - number of persons per spatial unit, household-size and person-nr. in household
				// TODO: Wie kann man das Folgende allgemeiner (für div. Tabellen und Queries) ausdrücken????
				// Vielleicht doch ein ORM - Tool???
				rs = db.executeQuery("SELECT * from [VZ_2001_Haushalte (ZB) relational] " +
						"where SpatialUnitId = " + spatialunitid +
						" order by HouseholdSize");
				ArrayList<PersonCountPerHouseholdSize> alpc = new ArrayList<PersonCountPerHouseholdSize>();
				while (rs.next()) {
					PersonCountPerHouseholdSize p = new PersonCountPerHouseholdSize(db);
					p.populate(rs);
					alpc.add(p);
				}
				// Fill into n[][]
				int x = 0;
				for (int i = 0; i != alpc.size(); i++) {
					for (int j = 0; j != i + 1; j++) {
						if ((i == alpc.size() - 1) && (j == i)) {
							// for hh-size 4+ and person-nr 4+: use person-count instead of household-count
							n[x][ymt] = alpc.get(alpc.size() - 1).getPersonCount() - alpc.get(i).getHouseholdCount() * (alpc.size() - 1);
						} else {
							n[x][ymt] = alpc.get(i).getHouseholdCount();
						}
						x++;
					}
				}
				
				// 2) Least Square Adjustment
//				System.out.println("Table n");
//				LeastSquareAdjustment.dump_table(n);
//				System.out.println("Table m");
//				LeastSquareAdjustment.dump_table(m);
				LeastSquareAdjustment.adjust_cells(n, m, 10);
//				LeastSquareAdjustment.dump_table(m);
						
				// 3) Daten in Tabelle schreiben
				PersonCountPerAgeSexHouseholdsizePersonnr pc_out = new PersonCountPerAgeSexHouseholdsizePersonnr(db);
				PersonsPerAgeSexHouseholdsizePersonnr pc_in;
				long maxid = 0;
				for (x = 0; x != xl; x++) {
					for (int y = 0; y != yl; y++) {
						pc_in = personCount.get(x * yl + y);
						maxid = x * yl + y + reccount;
						pc_out.setId(maxid);
						pc_out.setSpatialunitId(spatialunitid);
						pc_out.setSex(pc_in.getSex());
						pc_out.setAgeGroupId(pc_in.getAgeGroupId());
						pc_out.setHouseholdSize(pc_in.getHouseholdSize());
						pc_out.setPersonNr(pc_in.getPersonNrInHousehold());
						pc_out.setPersonCount(m[x][y]);
						pc_out.dbInsert();
					}
				}
				reccount = maxid + 1; 
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Erstellung abgeschlossen", "Persons per age, sex, hh-size, personnr, spatial unit", JOptionPane.OK_OPTION);
	}
}
