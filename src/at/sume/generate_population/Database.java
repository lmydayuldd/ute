/**
 * 
 */
package at.sume.generate_population;

import java.sql.*;

/**
 * Common database handling routines
 * 
 * @author Alexander Remesch
 *
 */
public class Database {
	private String url;
	public Connection con;

	/**
	 * Construct class and open a database
	 * @param pathname
	 */
	public Database(String pathname)
	{
		open(pathname);
	}

	/**
	 * Open a database
	 * @param pathname
	 */
	public void open(String pathname)
	{
        url = "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=" + pathname + ";";

        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        } catch(java.lang.ClassNotFoundException e) {
            System.err.println("Treiber-Klasse " + e + " konnte nicht geladen werden!");
            System.err.println(e.getMessage());
            return;
        }

        try {
            con = DriverManager.getConnection(url);
        } catch(SQLException e) {
            System.err.println("Datenbank Verbindungsfehler!\n" + e);
            return;
        }
	}
	
	/**
	 * Close the current database
	 */
	public void close()
	{
		try {
	        con.close();
		} catch(SQLException e) {
			System.err.println("Error closing database " + url + "\n" + e);
		}
	}
	
	/**
	 * Execute a query in the database
	 * @param query
	 * @return
	 */
	public ResultSet executeQuery(String query)
	{
	    Statement stmt;
		try {
			stmt = con.createStatement();
			// TODO: open resultset not in forward only mode
    		//stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			System.err.println("Error in Connection.createStatement()\n" + e);
			//e.printStackTrace();
			return null;
		}
        
        try {
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			System.err.println("Error in Statement.executeQuery(" + query + ")\n" + e);
			return null;
		}
	}
	
	/**
	 * Execute a SQL-statement
	 * @param statement
	 */
	public void execute(String statement)
	{
		Statement stmt;

		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			System.err.println("Error in Connection.createStatement()\n" + e);
			//e.printStackTrace();
			return;
		}
        
        try {
			stmt.execute(statement);
		} catch (SQLException e) {
			System.err.println("Error in Statement.execute(" + statement + ")\n" + e);
			return;
		}
	}
}
