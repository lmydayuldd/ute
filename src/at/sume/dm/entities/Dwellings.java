/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;

import net.remesch.db.Database;
import at.sume.db.RecordSet;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public class Dwellings extends RecordSet<DwellingRow> {

	public Dwellings(Database db) throws SQLException, InstantiationException, IllegalAccessException {
		setDb(db);
		rowList = db.select(DwellingRow.class, selectStatement());
	}
	
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_Dwellings";
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#selectStatement()
	 */
	@Override
	public String selectStatement() {
		return "select * from _DM_Dwellings order by dwellingId";
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	@Deprecated
	public String[] primaryKeyFieldnames() {
		throw new AssertionError("Dwellings.primaryKeyFieldnames is depreceated");
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	@Deprecated
	public String[] fieldnames() {
		throw new AssertionError("Dwellings.fieldnames is depreceated");
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createRecordSetRow()
	 */
	@Override
	@Deprecated
	public DwellingRow createRecordSetRow() {
		throw new AssertionError("Dwellings.createRecordSetRow is depreceated");
	}

	/**
	 * Link all households in the collection to their corresponding spatial unit according to the spatialunit-id
	 * @param spatialunits Collection of spatial units
	 */
	public void linkSpatialUnits(SpatialUnits spatialunits) {
		for (RecordSetRow<Dwellings> row : rowList) {
			DwellingRow hh = (DwellingRow) row;
			hh.setSpatialunit(spatialunits.lookup(hh.getSpatialunitId()));
		}
	}
}
