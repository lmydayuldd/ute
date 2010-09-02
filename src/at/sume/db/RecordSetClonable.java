/**
 * 
 */
package at.sume.db;

import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.util.Database;

/**
 * @author Alexander Remesch
 *
 */
public abstract class RecordSetClonable<E extends RecordSetRow> extends RecordSet<E> implements Cloneable {

	public RecordSetClonable() {
		super();
	}
	
	/**
	 * @param db
	 * @throws SQLException
	 */
	public RecordSetClonable(Database db) throws SQLException {
		super(db);
		// TODO Auto-generated constructor stub
	}

	public abstract RecordSetClonable<E> factory();
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		RecordSetClonable<E> copy = factory();
		copy.rowList = (ArrayList<E>) rowList.clone();
		return(copy);
	}
}
