/**
 * 
 */
package at.sume.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface for single items from a database
 * @author Alexander Remesch
 */
public abstract class RecordSetRow implements Comparable<RecordSetRow> {
	protected Long id;
	protected boolean deleted = false;
	
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

	/**
	 * @return Is this record deleted?
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * Mark this row for removal/deletion from its recordset
	 * We need this instead of remove() to be able to remove the record during an iteration 
	 */
	public void setDeleted() {
		this.deleted = true;
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
		RecordSetRow other = (RecordSetRow) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * Set field values of the item from a data source
	 * @param rs ResultSet with database records
	 * @param name Name of the field whose value shall be set from the ResultSet
	 * @throws SQLException 
	 */
	public abstract void set(ResultSet rs, String name) throws SQLException;

	//public Object get(Class<T> class, String name);
	
	/**
	 * Compare primary keys with a set of values
	 * @param lookupKeys
	 * @return true if equal, false if non-equal
	 */
	public abstract boolean primaryKeyEquals(Object... lookupKeys);
	
	/**
	 * Compare to unique id field
	 * @param row record from which the id will be taken for comparison
	 * @return
	 */
	public int compareTo(RecordSetRow row) {
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
	 * Remove this row from its recordset
	 * TODO: this shall be non-abstract in future versions (implement the list here!)
	 */
	public abstract void remove();
}
