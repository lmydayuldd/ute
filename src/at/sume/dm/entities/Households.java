/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;
import net.remesch.util.Database;
import at.sume.db.RecordSet;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public class Households extends RecordSet<HouseholdRow> {
//	private SpatialUnits spatialunits;
	
	/**
	 * @param db
	 * @throws SQLException
	 */
	public Households(Database db) throws SQLException {
		super(db);
	}

	/**
	 * Link all households in the collection to their corresponding spatial unit according to the spatialunit-id
	 * @param spatialunits Collection of spatial units
	 */
	public void linkSpatialUnits(SpatialUnits spatialunits) {
//		this.spatialunits = spatialunits;
		
		for (RecordSetRow row : rowList) {
			HouseholdRow hh = (HouseholdRow) row;
			hh.setSpatialunit(spatialunits.lookup(hh.getSpatialunitId()));
		}
	}
	
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createDatabaseRecord()
	 */
	@Override
	public HouseholdRow createDatabaseRecord(RecordSet recordset) {
		return new HouseholdRow((Households) recordset);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "HouseholdId", "SpatialunitId", "HouseholdSize", "DwellingId" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	public String[] primaryKeyFieldnames() {
		String s[] = { "HouseholdId" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_Households";
	}
}
