/**
 * 
 */
package at.sume.db_wrapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import at.sume.generate_population.Database;

/**
 * @author ar
 *
 */
public abstract class DatabaseRecord {
	static protected PreparedStatement ps;
	static protected Database db;
	
	public DatabaseRecord(Database pdb) throws SQLException {}{
		//db = pdb;
	}
	
	public static void prepareStatement(String sqlx) throws SQLException
	{
		ps = db.con.prepareStatement(sqlx);
	}

	private static void executeUpdate() throws SQLException
	{
		ps.executeUpdate();
	}
	
	public abstract void dbInsert() throws SQLException;
}
