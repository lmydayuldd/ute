/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.db.RecordSetRow;

/**
 * List of spatial units in the model
 * @author Alexander Remesch
 * 
 * TODO: must include properties of each spatial unit, must include geo-coordinates for graphical representation 
 */
public class SpatialUnitRow extends RecordSetRow {
//	private long spatialUnitId;

	/**
	 * @return the spatialUnitId
	 */
	public long getSpatialUnitId() {
		return id;
	}

	/**
	 * @param spatialUnitId the spatialUnitId to set
	 */
	public void setSpatialUnitId(long spatialUnitId) {
		this.id = spatialUnitId;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.DatabaseRecord#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void set(ResultSet rs, String name) throws SQLException {
		if (name.equals("SpatialUnitId")) {
			setSpatialUnitId(rs.getLong(name));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#primaryKeyEquals(java.lang.Object[])
	 */
	@Override
	public boolean primaryKeyEquals(Object... lookupKeys) {
		if (lookupKeys.length != 1) {
			throw new IllegalArgumentException("PK is only one field");
		}
		if (lookupKeys[0] instanceof Long) {
			long lookupKey = (Long) lookupKeys[0];
			if (lookupKey == getSpatialUnitId())
				return true;
			else
				return false;
		} else {
			throw new IllegalArgumentException("PK must by of type Long");
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#remove()
	 */
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("SpatialUnitRow.remove() not yet implemented");
	}
}
