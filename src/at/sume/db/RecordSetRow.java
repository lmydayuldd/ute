/**
 * 
 */
package at.sume.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface for single items from a database
 * @author Alexander Remesch
 */
public abstract class RecordSetRow<T extends RecordSet<?>> implements Comparable<RecordSetRow<T>> {
	public Long id;
	protected T recordSet; 
	
	protected PreparedStatement psInsert;
	protected PreparedStatement psUpdate;
	
	/**
	 * Necessary for cloning of RecordSetRow
	 */
	public RecordSetRow() {
		
	}

	/**
	 * Create a row and make it member of the given recordset
	 * @param rowList
	 */
	public RecordSetRow(T rowList) {
		this.recordSet = rowList;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordSetRow<?> other = (RecordSetRow<?>) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * Set field values of the row from a data source
	 * @param rs ResultSet with database records
	 * @param name Name of the field whose value shall be set from the ResultSet
	 * @throws SQLException 
	 */
	public abstract void loadFromDatabase(ResultSet rs, String name) throws SQLException;

	/**
	 * Set all field values of the row from a data source
	 * @param rs ResultSet with database records
	 * @throws SQLException
	 */
	public final void loadFromDatabase(ResultSet rs) throws SQLException {
		for (String fieldName : recordSet.fieldnames()) {
			loadFromDatabase(rs, fieldName);
		}
	}
	
	/**
	 * Set all fields to update/insert the row into the corresponding database table
	 * @throws SQLException 
	 */
	public void saveToDatabase() throws SQLException {
		throw new IllegalArgumentException("This recordset cannot be saved to the database");
	}

	//public Object get(Class<T> class, String name);
	
	/**
	 * Compare primary keys with a set of values
	 * Default implementation handles Id field as PK of type long (can be overridden)
	 * @param lookupKeys
	 * @return true if equal, false if non-equal
	 */
	public boolean primaryKeyEquals(Object... lookupKeys) {
		if (lookupKeys.length != 1) {
			throw new IllegalArgumentException("PK is only one field");
		}
		if (lookupKeys[0] instanceof Long) {
			long lookupKey = (Long) lookupKeys[0];
			if (lookupKey == getId())
				return true;
			else
				return false;
		} else {
			throw new IllegalArgumentException("PK must by of type Long");
		}
	}
	
	/**
	 * Compare to unique id field
	 * @param row record from which the id will be taken for comparison
	 * @return
	 */
	public int compareTo(RecordSetRow<T> row) {
		return id.compareTo(row.getId());
	}
	
	/**
	 * Compare to unique id field
	 * @param id id to compare
	 * @return
	 */
	public int compareTo(Long id) {
		return this.id.compareTo(id);
	}

	/**
	 * Remove this row from its recordset (only in RAM)
	 */
	public void remove() {
//		rowList.remove(this);
		recordSet.getRowList().remove(this);
	}

	public void prepareStatement() throws SQLException {
		psInsert = recordSet.db.con.prepareStatement(recordSet.insertStatement());
		psUpdate = recordSet.db.con.prepareStatement(recordSet.updateStatement());
	}
	
	/**
	 * INSERT or UPDATE the current record in the database
	 * @throws SQLException 
	 */
	public void executeUpdate() throws SQLException {
		saveToDatabase();
		psUpdate.executeUpdate();
	}
	
	public void executeInsert() throws SQLException {
		saveToDatabase();
		psInsert.executeUpdate();
	}
}
