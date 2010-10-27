/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;
import at.sume.db.RecordSet;
import net.remesch.db.Database;

/**
 * @author Alexander Remesch
 *
 */
public class SpatialUnits extends RecordSet<SpatialUnitRow> {

	/**
	 * @param db
	 * @throws SQLException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public SpatialUnits(Database db) throws SQLException, InstantiationException, IllegalAccessException {
		setDb(db);
		rowList = db.select(SpatialUnitRow.class, selectStatement());
	}

	public SpatialUnits() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#selectStatement()
	 */
	@Override
	public String selectStatement() {
		return "select * from _DM_SpatialUnits order by spatialunitId";
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createDatabaseRecord()
	 */
	@Override
	@Deprecated
	public SpatialUnitRow createRecordSetRow() {
		throw new AssertionError("SpatialUnits.createRecordSetRow is depreceated");
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	@Deprecated
	public String[] fieldnames() {
		throw new AssertionError("SpatialUnits.fieldnames is depreceated");
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	@Deprecated
	public String[] primaryKeyFieldnames() {
		throw new AssertionError("SpatialUnits.primaryKeyFieldnames is depreceated");
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_SpatialUnits";
	}
}
