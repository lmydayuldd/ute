/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

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
		return "select spatialUnitId AS id, spatialUnitId, totalarea, areaShareContinousAndDiscontinousUrbanFabric, " +
			"areaShareIndustrialCommercialConstructionInfrastructure, areaShareArtificialVegetation, " +
			"areaShareAgricultural, areaShareForest, areaShareWater from _DM_SpatialUnits order by spatialunitId";
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createDatabaseRecord()
	 */
	@Override
	public SpatialUnitRow createRecordSetRow() {
		return new SpatialUnitRow();
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
	/**
	 * Convert a list of spatial unit ids into a list of spatial unit rows
	 * @param spatialUnitIds
	 * @return
	 */
	public ArrayList<SpatialUnitRow> getSpatialUnits(ArrayList<Integer> spatialUnitIds) {
		ArrayList<SpatialUnitRow> result = new ArrayList<SpatialUnitRow>();
		SpatialUnitRow lookup = new SpatialUnitRow();
		for (int spatialUnitId : spatialUnitIds) {
			lookup.setSpatialUnitId(spatialUnitId);
			int index = Collections.binarySearch(rowList, lookup);
			if (index >= 0) {
				result.add(rowList.get(index));
			} else {
				System.out.println("Can't find spatial unit id " + spatialUnitId + " in the list of spatial units");
			}
		}
		return result;
	}
}
