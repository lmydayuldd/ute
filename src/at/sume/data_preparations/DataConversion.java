/**
 * 
 */
package at.sume.data_preparations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.remesch.util.Database;

import at.sume.dm.Common;

/**
 * Routines for data conversion
 * @author ar
 *
 */
public class DataConversion {

	/***
	 * Conversion of table "VZ_2001_Alter_Geschlecht_Familienstand (ZB)" to "VZ_2001_Alter_Geschlecht_Familienstand (ZB) relational" 
	 * @throws SQLException 
	 */
	private static void Convert_VZ2001_HH_Age() throws SQLException {
		Database db = new Database(Common.getDbLocation());
		ResultSet res = db.executeQuery("select *, val([GKZ]) AS GKZnum from [VZ_2001_Alter_Geschlecht_Familienstand (ZB)] where len(gkz) = 6 and Geschlecht <> 'gesamt'");
		String sqlx = "insert into [VZ_2001_Alter_Geschlecht_Familienstand (ZB) relational] (ID, SpatialunitId, Sex, AgeGroupId, PersonCount) " + 
			" values (?, ?, ?, ?, ?)";
		PreparedStatement ps = db.con.prepareStatement(sqlx);
		String fieldnames[] = { "15-19", "20-24", "25-29", "30-34", "35-39", "40-44", "45-49", "50-54", "55-59", "60-64", "65-69", "70-74", "75-79", "80-84", "85-" };
		String sex;
		Long gkzNum;
		
		int i = 1;
		while (res.next()) {
			gkzNum = res.getLong("GKZnum");
			sex = res.getString("Geschlecht");
			ps.setString(1, Integer.toString(i++));
			ps.setString(2, Long.toString(gkzNum));
			if (sex.equals("weiblich"))
				ps.setString(3, "1");
			else
				ps.setString(3, "2");
			// Age 0-14
			ps.setString(4, "1");
			ps.setString(5, Long.toString(res.getLong("0-4") + res.getLong("5-9") + res.getLong("10-14")));
			ps.executeUpdate();
			// Age 15-19 to 85-
			for (int j = 0; j != fieldnames.length; j++) {
				ps.setString(1, Integer.toString(i++));
//				ps.setString(2, gkzNum);
//				if (sex == "weiblich")
//					ps.setString(3, "1");
//				else
//					ps.setString(3, "2");
				ps.setString(4, Integer.toString(j + 2));
				ps.setString(5, Long.toString(res.getLong(fieldnames[j])));
				ps.executeUpdate();
			}
		}
	}
	
	public static void Convert_VZ2001_HH_Size() throws SQLException {
		Database db = new Database(Common.getDbLocation());
		ResultSet res = db.executeQuery("select *, val([Oestat]) AS GKZnum from [VZ_2001_Haushalte (ZB)] where len(Oestat) = 6");
		String sqlx = "insert into [VZ_2001_Haushalte (ZB) relational] (ID, SpatialunitId, HouseholdSize, HouseholdCount, PersonCount) " + 
			" values (?, ?, ?, ?, ?)";
		PreparedStatement ps = db.con.prepareStatement(sqlx);
		String fieldnames[] = { "HH_P1", "HH_P2", "HH_P3", "HH_P4", "HH_P5", "HH_P6", "Personen_HH_P6", "HH_Einrichtungen", "Personen_Einrichtungen" };
		Long gkzNum;
		
		int i = 1;
		while (res.next()) {
			long hh_count = 0, pers_count = 0;
			gkzNum = res.getLong("GKZnum");
			ps.setString(2, Long.toString(gkzNum));
			for (int j = 0; j != fieldnames.length; j++) {
				long val = res.getLong(fieldnames[j]);
				// HH_P1 to HH_P3 -> can be added almost directly
				if (fieldnames[j].equals("HH_P1") || fieldnames[j].equals("HH_P2") || fieldnames[j].equals("HH_P3")) { 
					ps.setString(1, Integer.toString(i++));
					ps.setString(3, Integer.toString(j + 1)); // Household-Size
					ps.setString(4, Long.toString(val)); // Household-Count
					ps.setString(5, Long.toString(val * (j + 1))); // Person-Count = Household-Size * Household-Count
					ps.executeUpdate();
				} else if (fieldnames[j].equals("HH_P4") || fieldnames[j].equals("HH_P5")) { 
					// sum up HH_P3 + HH_P4 + HH_P5 + HH_P6 (Persons + Households)
					hh_count += val;
					pers_count += val * (j + 1);
				} else if (fieldnames[j].equals("HH_P6") || fieldnames[j].equals("HH_Einrichtungen")) { 
					// HH_P6 has an extra sum for persons!
					hh_count += val;
				} else if (fieldnames[j].equals("Personen_HH_P6")) {
					pers_count += val;
				} else if (fieldnames[j].equals("Personen_Einrichtungen")) {
					pers_count += val;
					ps.setString(1, Integer.toString(i++));
					ps.setString(3, "4"); // Household-Size = 4+
					ps.setString(4, Long.toString(hh_count)); // Household-Count
					ps.setString(5, Long.toString(pers_count)); // Person-Count
					ps.executeUpdate();
				}
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//Convert_VZ2001_HH_Age();
			Convert_VZ2001_HH_Size();
			JOptionPane.showMessageDialog(null, "Erstellung abgeschlossen", "VZ2001 Age, Sex", JOptionPane.OK_OPTION);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
