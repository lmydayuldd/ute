/**
 * 
 */
package at.sume.db_wrapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.generate_population.Database;

/**
 * @author ar
 *
 */
public abstract class DatabaseRecord {
	protected PreparedStatement ps;
	protected Database db;
	
	public DatabaseRecord(Database pdb) throws SQLException {
		db = pdb;
	}
	
	public abstract void populate(ResultSet rs) throws SQLException;
	
	public void prepareStatement(String sqlx) throws SQLException
	{
		ps = db.con.prepareStatement(sqlx);
	}

	private void executeUpdate() throws SQLException
	{
		ps.executeUpdate();
	}
	
	public void dbInsert() throws SQLException {
		ps.executeUpdate();
	}
}
