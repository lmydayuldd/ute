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
	
	public int compareTo(RecordSetRow row) {
		return id.compareTo(row.getId());
	}
	
	public int compareTo(Long id) {
		return this.id.compareTo(id);
	}
}
