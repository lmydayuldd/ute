/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import net.remesch.db.Database;
import at.sume.db.RecordSet;
import at.sume.db.RecordSetRow;
import at.sume.dm.Common;

/**
 * @author Alexander Remesch
 *
 */
public class Dwellings extends RecordSet<DwellingRow> {
	public Dwellings() {
		
	}
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
		return "select dwellingId AS id, dwellingId, spatialUnitId, dwellingSize, dwellingCosts, " +
			"livingSpaceGroup6Id, costOfResidenceGroupId, constructionPeriod7Id from _DM_Dwellings order by dwellingId";
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
	public DwellingRow createRecordSetRow() {
		return new DwellingRow(this);
	}

	/**
	 * Link all households in the collection to their corresponding spatial unit according to the spatialunit-id
	 * @param spatialunits Collection of spatial units
	 */
	public void linkSpatialUnits(SpatialUnits spatialunits) {
		for (RecordSetRow<Dwellings> row : rowList) {
			DwellingRow dwelling = (DwellingRow) row;
			dwelling.setSpatialunit(spatialunits.lookup(dwelling.getSpatialunitId()));
		}
	}
	
	/**
	 * Return all dwellings that are not currently occupied by a household
	 * @return
	 */
	public ArrayList<DwellingRow> getFreeDwellings() {
		ArrayList<DwellingRow> freeDwellings = new ArrayList<DwellingRow>();
		for (DwellingRow row : rowList) {
			if (row.getHousehold().equals(null)) {
				freeDwellings.add(row);
			}
		}
		return freeDwellings;
	}
	
	/**
	 * Return all dwellings that are currently not occupied by a household and on the dwelling market
	 * (according to a random selection through the system parameter DwellingsOnMarketShare
	 * @return
	 */
	public ArrayList<DwellingRow> getDwellingsOnMarket() {
		Random r = new Random();
		ArrayList<DwellingRow> freeDwellings = new ArrayList<DwellingRow>();
		for (DwellingRow row : rowList) {
			if (row.getHousehold().equals(null)) {
				if (r.nextInt() <= Common.getDwellingsOnMarketShare()) {
					freeDwellings.add(row);
				}
			}
		}
		return freeDwellings;
	}
}
