/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;
import at.sume.db.RecordSet;
import net.remesch.util.Database;

/**
 * @author Alexander Remesch
 *
 */
public class SpatialUnits extends RecordSet<SpatialUnitRow> {

	/**
	 * @param db
	 * @throws SQLException
	 */
	public SpatialUnits(Database db) throws SQLException {
		super(db);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createDatabaseRecord()
	 */
	@Override
	public SpatialUnitRow createDatabaseRecord() {
		return new SpatialUnitRow(this);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "SpatialUnitId" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	public String[] primaryKeyFieldnames() {
		String s[] = { "SpatialUnitId" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_SpatialUnits";
	}

//	/* (non-Javadoc)
//	 * @see at.sume.db.RecordSetIndexed#lookup(at.sume.db.RecordSetRow)
//	 */
//	@Override
//	public SpatialUnitRow lookup(RecordSetRow lookupKeys) {
//		SpatialUnitRow suLookup = (SpatialUnitRow) lookupKeys;
//		return lookup(suLookup.getSpatialUnitId());
//	}
//	
//	/**
//	 * Lookup a spatial unit row by its id, find the correct row by iterating through the RecordSet (which should contain 59 or 250
//	 * records maximum)
//	 * @param spatialUnitId
//	 * @return
//	 */
//	public SpatialUnitRow lookup(long spatialUnitId) {
//		for (RecordSetRow row : rowList) {
//			SpatialUnitRow su = (SpatialUnitRow) row;
//			if (su.getSpatialUnitId() == spatialUnitId)
//				return su;
//		}
//		return null;
//	}

}
