/**
 * 
 */
package net.remesch.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.remesch.db.schema.DatabaseField;
import net.remesch.db.schema.DatabaseFieldMap;
import net.remesch.util.DateUtil;
import net.remesch.util.Reflection;

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
	 * Select data from a database table into a List of objects based on the matching of table to object field
	 * names
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
	public <T> ArrayList<T> select(Class<T> c, String sqlStatement) throws SQLException, InstantiationException, IllegalAccessException {
		boolean modifiedFieldAccessibility = false;
		ArrayList<T> result = new ArrayList<T>();
		ResultSet rs = executeQuery(sqlStatement);
		Field fields[] = Reflection.getFieldNames(c);
		assert fields.length > 0 : "No fields in class " + c.getName() + " or in its superclasses";
		while (rs.next()) {
			T item = c.newInstance();
			//for (Field field : c.getFields()) {
//			for (Field field : c.getDeclaredFields()) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(net.remesch.db.schema.Ignore.class))
					continue;
				String type = field.getType().getName();
				String fieldName = field.getName();
				if (!field.isAccessible()) {
					field.setAccessible(true);
					modifiedFieldAccessibility = true;
				}
				if (field.isAnnotationPresent(net.remesch.db.schema.DatabaseField.class)) {
					DatabaseField dbf = field.getAnnotation(net.remesch.db.schema.DatabaseField.class);
					fieldName = dbf.fieldName();
				}
				if (type.equals("java.lang.String")) {
					field.set(item, rs.getString(fieldName));
				} else if (type.equals("long") || type.equals("java.lang.Long")) {
					field.set(item, rs.getLong(fieldName));
				} else if (type.equals("int") || type.equals("java.lang.Integer")) {
					field.set(item, rs.getInt(fieldName));
				} else if (type.equals("short") || type.equals("java.lang.Short")) {
					field.set(item, rs.getShort(fieldName));
				} else if (type.equals("byte") || type.equals("java.lang.Byte")) {
					field.set(item, rs.getByte(fieldName));
				} else if (type.equals("double") || type.equals("java.lang.Double")) {
					field.set(item, rs.getDouble(fieldName));
				} else if (type.equals("float") || type.equals("java.lang.Float")) {
					field.set(item, rs.getFloat(fieldName));
				} else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
					field.set(item, rs.getBoolean(fieldName));
				} else {
					throw new AssertionError("fieldName = " + c.getName() + "." + fieldName + ", type = " + type);
				}
				if (modifiedFieldAccessibility)
					field.setAccessible(false);
			}
			result.add(item);
		}
		// TODO: reset field access
		rs.close();
		return result;
	}
	/**
	 * Insert data from a list of objects into a database table based on the matching of table to object field names
	 * @param <T>
	 * @param rowList
	 * @param sqlStatement
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public <T> void insert(List<T> rowList, String sqlStatement) throws SQLException, IllegalArgumentException, IllegalAccessException {
		boolean modifiedFieldAccessibility = false;
		assert rowList.size() > 0 : "No records in rowList";
		Class<? extends Object> c = rowList.get(0).getClass();
		Field fields[] = Reflection.getFieldNames(c);
		assert fields.length > 0 : "No fields in class " + c.getName() + " or in its superclasses";
		con.setAutoCommit(false); // if auto-commit is set to true, the connection has to be closed to really write the records into the table
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery(sqlStatement);
		int j = 0;
		for (T row : rowList) {
			if (j % 10000 == 0) {
				System.out.println(DateUtil.now() + ": Adding row " + j + " of " + rowList.size());
			}
			rs.moveToInsertRow();
			for (Field field : fields) {
				if (field.isAnnotationPresent(net.remesch.db.schema.Ignore.class))
					continue;
				String type = field.getType().getName();
				String fieldName = field.getName();
				if (!field.isAccessible()) {
					field.setAccessible(true);
					modifiedFieldAccessibility = true;
				}
				if (field.isAnnotationPresent(net.remesch.db.schema.DatabaseField.class)) {
					DatabaseField dbf = field.getAnnotation(net.remesch.db.schema.DatabaseField.class);
					fieldName = dbf.fieldName();
				}
				if (type.equals("java.lang.String")) {
					rs.updateString(fieldName, field.get(row).toString());
				} else if (type.equals("long") || type.equals("java.lang.Long")) {
					rs.updateLong(fieldName, field.getLong(row));
				} else if (type.equals("int") || type.equals("java.lang.Integer")) {
					rs.updateInt(fieldName, field.getInt(row));
				} else if (type.equals("short") || type.equals("java.lang.Short")) {
					rs.updateShort(fieldName, field.getShort(row));
				} else if (type.equals("byte") || type.equals("java.lang.Byte")) {
					rs.updateByte(fieldName, field.getByte(row));
				} else if (type.equals("double") || type.equals("java.lang.Double")) {
					rs.updateDouble(fieldName, field.getDouble(row));
				} else if (type.equals("float") || type.equals("java.lang.Float")) {
					rs.updateFloat(fieldName, field.getFloat(row));
				} else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
					rs.updateBoolean(fieldName, field.getBoolean(row));
				} else {
					throw new AssertionError("fieldName = " + c.getName() + "." + fieldName + ", type = " + type);
				}
				if (modifiedFieldAccessibility)
					field.setAccessible(false);
			}
			rs.insertRow();
			j++;
		}
		con.commit();
		rs.close();
		stmt.close();
//		con.close();
	}
	/**
	 * Insert data from a list of objects into a database table based on the matching of table to object field names
	 * @param <T>
	 * @param rowList
	 * @param sqlStatement
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public <T> void insertFieldMap(List<T> rowList, String sqlStatement) throws SQLException, IllegalArgumentException, IllegalAccessException {
		assert rowList.size() > 0 : "No records in rowList";
		Class<? extends Object> c = rowList.get(0).getClass();
		ArrayList<DatabaseFieldMap> fields = Reflection.getFields(c);
		assert fields.size() > 0 : "No fields in class " + c.getName() + " or in its superclasses";
		con.setAutoCommit(false); // if auto-commit is set to true, the connection has to be closed to really write the records into the table
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery(sqlStatement);
		// Prepare field accessibility
		for (DatabaseFieldMap fieldMap : fields) {
			Field field = fieldMap.getField();
			if (fieldMap.isIgnore())
				continue;
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
		}
		int j = 0;
		for (T row : rowList) {
			if (j % 10000 == 0) {
				System.out.println(DateUtil.now() + ": Adding row " + j + " of " + rowList.size());
			}
			rs.moveToInsertRow();
			for (DatabaseFieldMap fieldMap : fields) {
				Field field = fieldMap.getField();
				if (fieldMap.isIgnore())
					continue;
				String type = fieldMap.getFieldType();
				String fieldName = fieldMap.getDbFieldName();
				if (type.equals("java.lang.String")) {
					rs.updateString(fieldName, field.get(row).toString());
				} else if (type.equals("long") || type.equals("java.lang.Long")) {
					rs.updateLong(fieldName, field.getLong(row));
				} else if (type.equals("int") || type.equals("java.lang.Integer")) {
					rs.updateInt(fieldName, field.getInt(row));
				} else if (type.equals("short") || type.equals("java.lang.Short")) {
					rs.updateShort(fieldName, field.getShort(row));
				} else if (type.equals("byte") || type.equals("java.lang.Byte")) {
					rs.updateByte(fieldName, field.getByte(row));
				} else if (type.equals("double") || type.equals("java.lang.Double")) {
					rs.updateDouble(fieldName, field.getDouble(row));
				} else if (type.equals("float") || type.equals("java.lang.Float")) {
					rs.updateFloat(fieldName, field.getFloat(row));
				} else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
					rs.updateBoolean(fieldName, field.getBoolean(row));
				} else {
					throw new AssertionError("fieldName = " + c.getName() + "." + fieldName + ", type = " + type);
				}
			}
			rs.insertRow();
			j++;
		}
		con.commit();
		// Reset accessibility
		for (DatabaseFieldMap fieldMap : fields) {
			Field field = fieldMap.getField();
			if (fieldMap.isIgnore())
				continue;
			if (!field.isAccessible()) {
				field.setAccessible(false);
			}
		}
		rs.close();
		stmt.close();
//		con.close();
	}
	public <T> String buildInsertStatement(ArrayList<DatabaseFieldMap> fieldMap, String tableName) {
		int i = 0;
		StringBuilder s = new StringBuilder();
		s.append("INSERT INTO " + tableName + " (");
		for (DatabaseFieldMap field : fieldMap) {
			if (field.isIgnore())
				continue;
			if (i++ != 0)
				s.append(", ");
			s.append(field.getDbFieldName());
		}
		s.append(") VALUES (");
		for (i = 0; i != fieldMap.size(); i++) {
			if (fieldMap.get(i).isIgnore())
				continue;
			if (i != 0)
				s.append(", ");
			s.append("?");
		}
		s.append(")");
		return s.toString();
	}
	/**
	 * Insert data from a list of objects into a database table based on the matching of table to object field names
	 * @param <T>
	 * @param rowList
	 * @param sqlStatement
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public <T> void insertSql(List<T> rowList, String tableName) throws SQLException, IllegalArgumentException, IllegalAccessException {
		assert rowList.size() > 0 : "No records in rowList";
		Class<? extends Object> c = rowList.get(0).getClass();
		ArrayList<DatabaseFieldMap> fields = Reflection.getFields(c);
		assert fields.size() > 0 : "No fields in class " + c.getName() + " or in its superclasses";
		String insertStatement = buildInsertStatement(fields, tableName);
		PreparedStatement ps = con.prepareStatement(insertStatement);
		// Prepare field accessibility
		for (DatabaseFieldMap fieldMap : fields) {
			Field field = fieldMap.getField();
			if (fieldMap.isIgnore())
				continue;
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
		}
		int j = 0;
		for (T row : rowList) {
			if (j++ % 10000 == 0) {
				System.out.println(DateUtil.now() + ": Inserting row " + j + " of " + rowList.size());
			}
			int i = 1;
			for (DatabaseFieldMap fieldMap : fields) {
				Field field = fieldMap.getField();
				if (fieldMap.isIgnore())
					continue;
				ps.setString(i, field.get(row).toString());
				i++;
			}
			ps.executeUpdate();
		}
		// Reset accessibility
		for (DatabaseFieldMap fieldMap : fields) {
			Field field = fieldMap.getField();
			if (fieldMap.isIgnore())
				continue;
			if (!field.isAccessible()) {
				field.setAccessible(false);
			}
		}
//		con.commit();
//		con.close();
	}
}
