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


/***
 * General handling of database tables/views
 * @author Alexander Remesch
 * TODO: improve implementation of Iterable/Iterator (see head-first book on this issue!)
*/
public abstract class RecordSet<E extends RecordSetRow> implements Iterable<E> {
//	protected ArrayList<? extends RecordSetRow> rowList;
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
	 * Factory for the SQL select statement to retrieve the database records 
	 * @return SQL select string
	 */
	// TODO: create default from fieldnames, tablename + pk-fieldnames
	public abstract String selectStatement();
	
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
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<E> iterator() {
		return rowList.iterator();
	}
}
