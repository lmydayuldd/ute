/**
 * 
 */
package at.sume.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import net.remesch.util.Database;
import net.remesch.util.StringUtil;


/***
 * General handling of database tables/views
 * @author Alexander Remesch
*/
public abstract class RecordSet<E extends RecordSetRow> implements Iterable<E> {
	protected ArrayList<E> rowList;

	/**
	 * Construct class and load probabilities from the database. Variable parts have to be implemented in implementation
	 * classes ("Factories")'
	 * 
	 * @param db Database to load rows from
	 * @throws SQLException
	 */
	public RecordSet(Database db) throws SQLException {
		int rowcount = 0; // TODO: get correct row count

		ResultSet rs = db.executeQuery(selectStatement());
		
		rowList = new ArrayList<E>(rowcount);
		ArrayList<String> fields = new ArrayList<String>(Arrays.asList(fieldnames()));
				
		while (rs.next())
		{
			E row = createDatabaseRecord(this);
			for (String field : fields) {
				row.set(rs, field);
			}
			rowList.add(row);
		}
		rs.close();
	}
	
	/**
	 * Factory for the SQL select statement to retrieve the database records. Default implementation returns a
	 * SELECT statement with all fields ordered by the primary key fields
	 * @return SQL select string
	 */
	public String selectStatement() {
		return "SELECT " + StringUtil.arrayToString(fieldnames(), ", ") + " FROM " + tablename() 
			+ " ORDER BY " + StringUtil.arrayToString(primaryKeyFieldnames(), ", ");
	}
	
	/**
	 * Factory for the database table name
	 * @return
	 */
	public abstract String tablename();
	
	/**
	 * Factory for the field names of the primary key fields
	 * @return Array of field names retrieved by the SQL select statement
	 */
	public abstract String[] primaryKeyFieldnames();
	
	/**
	 * Factory for the field names in the RecordSet
	 * @return Field name retrieved by the SQL select statement
	 */
	public abstract String[] fieldnames();
	
	/**
	 * Factory to create the specific instantiation of DatabaseRecord
	 * @param recordSet Link to the RecordSet the RecordSetRow belongs to
	 * @return
	 */
	public abstract E createDatabaseRecord(RecordSet<E> recordSet);

	/**
	 * Look up a row from a RecordSet matching the key values given 
	 * @param lookupKeys Key values to search
	 * @return
	 */
	public E lookup(Object... lookupKeys) {
		for (E row : rowList) {
			if (row.primaryKeyEquals(lookupKeys))
				return row;
		}
		return null;
	}

	/**
	 * Look up a row from a RecordSet matching the id value given. The lookup will be performed by binary search
	 * algorithm.
	 * @param id
	 * @return
	 */
	public E lookup(Long id) {
		E lookupKey = createDatabaseRecord(this);
		lookupKey.setId(id);
		int i = Collections.binarySearch(rowList, lookupKey);
		return rowList.get(i);
	}
	
	/**
	 * Remove a row from the RecordSet (in memory, NOT in the database)
	 * @param row
	 */
	public void remove(E row) {
		int i = rowList.indexOf(row);
		if (i >= 0) {
			rowList.remove(i);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<E> iterator() {
		return rowList.iterator();
	}
	
	/**
	 * Returns number of elements in the recordset
	 * @return
	 */
	public int size() {
		return rowList.size();
	}
}
