/**
 * 
 */
package at.sume.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.remesch.db.Database;
import net.remesch.util.StringUtil;


/***
 * General handling of database tables/views
 * @author Alexander Remesch
*/
public abstract class RecordSet<E extends RecordSetRow<?>> implements Iterable<E>, Collection<E> {
	protected ArrayList<E> rowList;
	protected Database db;

	/**
	 * Necessary for cloning of RecordSet, and for superclasses that don't want to fill the
	 * recordset (e.g. ProbabilityDistribution)
	 */
	public RecordSet() {
		rowList = new ArrayList<E>();
	}
	
	/**
	 * Construct class and load probabilities from the database. Variable parts have to be implemented in implementation
	 * classes ("Factories")'
	 * 
	 * @param db Database to load rows from
	 * @throws SQLException
	 */
	public RecordSet(Database db) throws SQLException {
		int rowcount = 0; // TODO: get correct row count

		this.db = db;
		ResultSet rs = db.executeQuery(selectStatement());
		rowList = new ArrayList<E>(rowcount);
		while (rs.next())
		{
			E row = createRecordSetRow();
			row.loadFromDatabase(rs);
			preAddRow(row);
			this.add(row);
		}
		rs.close();
	}
	
	public Database getDb() {
		return db;
	}
	
	public void setDb(Database db) {
		this.db = db;
	}
	
	/**
	 * @return the rowList
	 */
	protected ArrayList<E> getRowList() {
		return rowList;
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
	 * Factory for the SQL select statement to insert a database record. Default implementation returns an
	 * INSERT statement with all fields
	 * @return SQL insert string
	 */
	public String insertStatement() {
		return "INSERT INTO " + tablename() + " (" + StringUtil.arrayToString(fieldnames(), ", ") + 
			") VALUES (" + StringUtil.repeat("?", fieldnames().length, ",") + ")";
	}
	
	public String updateStatement() {
		return "UPDATE " + tablename() + " SET " + StringUtil.arrayToString(nonPrimaryKeyFieldnames(), " = ?, ") + " = ? " +
			" WHERE " + StringUtil.arrayToString(primaryKeyFieldnames(), " = ? AND ") + " = ?";
	}
	
	/**
	 * Factory for the database table name
	 * @return
	 */
	public abstract String tablename();
	
	/**
	 * Factory for the field names of the primary key fields.
	 * Used for generating the WHERE-claus in an UPDATE-statement.
	 * @return Array of field names retrieved by the SQL select statement
	 */
	public abstract String[] primaryKeyFieldnames();
	
	/**
	 * Factory for the field names in the RecordSet
	 * @return Field name retrieved by the SQL select statement
	 */
	public abstract String[] fieldnames();
	
	/**
	 * Return all non-primary-key fields in the row
	 * @return
	 */
	public String[] nonPrimaryKeyFieldnames() {
		ArrayList<String> pkfieldnames = new ArrayList<String>(Arrays.asList(primaryKeyFieldnames()));
		ArrayList<String> result = new ArrayList<String>();
		for (String field : fieldnames()) {
			if (!pkfieldnames.contains(field)) {
				result.add(field);
			}
		}
		return result.toArray(new String[result.size()]);
	}
	
	/**
	 * Factory to create the specific instantiation of DatabaseRecord
	 * @param recordSet Link to the RecordSet the RecordSetRow belongs to
	 * @return
	 * @throws SQLException 
	 */
	public abstract E createRecordSetRow();
	/**
	 * Processing of the row before adding it to the rowList 
	 * @param row
	 */
	public void preAddRow(E row) {
		
	}
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
		E lookupKey = createRecordSetRow();
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
	
	/* (non-Javadoc)
	 * @see java.util.Collection#size(java.util.Collection)
	 */
	@Override
	public int size() {
		return rowList.size();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.util.Collection)
	 */
	@Override
	public boolean add(E row) {
		row.setRecordSet(this);
		return rowList.add(row);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c)
			e.setRecordSet(this);
		return rowList.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		rowList.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return rowList.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return rowList.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return rowList.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return rowList.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return rowList.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return rowList.retainAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return rowList.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return rowList.toArray(a);
	}
	
	public E get(int index) {
		return rowList.get(index);
	}
	
	public int indexOf(E row) {
		return rowList.indexOf(row);
	}
}
