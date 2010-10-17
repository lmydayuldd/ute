/**
 * 
 */
package net.remesch.util;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;

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
	
	public void executeQuery(String query, Object ... variables) throws SQLException, Exception {
		ResultSet rs = executeQuery(query);
		for (int i = 0; i != variables.length; i++) {
			if (variables[i].getClass() == String.class)
				variables[i] = rs.getString(i);
			else if (variables[i].getClass() == Integer.class)
				variables[i] = rs.getInt(i);
			else if (variables[i].getClass() == Long.class)
				variables[i] = rs.getLong(i);
			else
				throw new Exception("Can't use type " + variables[i].getClass().toString());
		}
	}
	
//	public ArrayList<DatabaseRecord> queryToArray(String query) throws SQLException {
//		ResultSet rs = executeQuery(query);
//		ArrayList<DatabaseRecord> al = new ArrayList<DatabaseRecord>();
//		while (rs.next()) {
//
//		}
//		return null;
//	}
	
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
	
	/**
	 * Lookup values from first matching database record 
	 * @param sql
	 * @param returnValues
	 * @throws SQLException 
	 */
	//public void lookupSql(String sql, Object... returnValues) throws SQLException {
	public void lookupSql(String sql, Object[] returnValues) throws SQLException {
		ResultSet rs = executeQuery(sql);
		// check if field count & return values comply
		ResultSetMetaData rsmd = rs.getMetaData();
		if (rsmd.getColumnCount() != returnValues.length) {
			throw new IllegalArgumentException("lookupSql: query retrieved " + rsmd.getColumnCount() + " columns while function got " + returnValues.length + " return value parameters");
		}
//		rs.last();
//		int count = rs.getRow();
//		rs.beforeFirst();
		if (rs.next()) {
			int i = 1;
			for(Object rv : returnValues) {
				if (rv instanceof Long) {
					rv = rs.getLong(i);
				} else if (rv instanceof Integer) {
					rv = rs.getInt(i);
				} else if (rv instanceof Double) {
					// TODO: das hier geht nicht, wegen call by reference und weil die Referenz auf das Double-Objekt neu erzeugt wird
					// muss per array passieren????
					rv = rs.getDouble(i);
				} else if (rv instanceof java.util.Date) {
					rv = rs.getDate(i);
				} else if (rv instanceof StringBuffer) {
					StringBuffer h = (StringBuffer) rv;
					h.setLength(0);
					h.append(rs.getString(i));
				} else if (rv instanceof String) {
					// TODO: don't know if this works
					StringBuffer h = new StringBuffer((String) rv);
					h.setLength(0);
					h.append(rs.getString(i));
				} else {
					throw new IllegalArgumentException("lookupSql: can't handle type of return value parameter " + i);
				}
				i++;
			}
		} else {
			for (@SuppressWarnings("unused") Object rv : returnValues) {
				rv = null;
			}
		}
	}
	
	/**
	 * Lookup first value from first matching database record
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public Object lookupSql(String sql) throws SQLException {
//		String [] returnValue = { };
//		lookupSql(sql, returnValue);
//		return returnValue[0];
		ResultSet rs = executeQuery(sql);
		// check if field count & return values comply
		ResultSetMetaData rsmd = rs.getMetaData();
		if (rs.next()) {
			int i = 1;
			switch (rsmd.getColumnType(i)) {
			case Types.DECIMAL:
				return rs.getLong(i);
			case Types.FLOAT:
				return rs.getFloat(i);
			case Types.DOUBLE:
				return rs.getDouble(i);
			case Types.VARCHAR:
				return rs.getString(i);
			case Types.DATE:
				return rs.getDate(i);
			case Types.INTEGER:
				return rs.getInt(i);
			default:
				throw new IllegalArgumentException("lookupSql: can't handle type " + rsmd.getColumnType(i) + " on query " + sql);
			}
		} else {
			return null;
		}
	}

	/**
	 * Based on a function taken from generics tutorial @ http://java.sun.com/j2se/1.5/pdf/generics-tutorial.pdf (p.17)
	 * This function supports only static (= "nested") inner classes!
	 * @param <T>
	 * @param c
	 * @param sqlStatement
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 */
	public <T> ArrayList<T> select(Class<T>c, String sqlStatement) throws SQLException, InstantiationException, IllegalAccessException {
		// TODO: Private fields can't be modified - use the appropriate setter or find another solution for this
		ArrayList<T> result = new ArrayList<T>();
		ResultSet rs = executeQuery(sqlStatement);
		while (rs.next()) {
			T item = c.newInstance();
			assert c.getFields().length > 0 : "No fields in class " + c.getName();
			for (Field field : c.getFields()) {
				// TODO: use annotations for diverging field names
//				field.getDeclaredAnnotations()
				String type = field.getType().getName();
				String fieldName = field.getName();
				if (type.equals("java.lang.String")) {
					field.set(item, rs.getString(fieldName));
				} else if (type.equals("long") || type.equals("java.lang.Long")) {
					field.set(item, rs.getLong(fieldName));
				} else if (type.equals("int") || type.equals("java.lang.Integer")) {
					field.set(item, rs.getInt(fieldName));
				} else if (type.equals("short") || type.equals("java.lang.Short")) {
					field.set(item, rs.getShort(fieldName));
				} else if (type.equals("double") || type.equals("java.lang.Double")) {
					field.set(item, rs.getDouble(fieldName));
				} else {
					throw new AssertionError("fieldName = " + fieldName + ", type = " + type);
				}
			}
			result.add(item);
		}
		rs.close();
		return result;
	}
}
