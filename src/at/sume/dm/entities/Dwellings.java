/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;
import java.util.ArrayList;

import at.sume.db.RecordSet;
import at.sume.dm.Common;
import at.sume.dm.tracing.ObjectSource;
import at.sume.dm.types.LivingSpaceGroup6;
import net.remesch.db.Database;
import net.remesch.util.Random;

/**
 * @author Alexander Remesch
 *
 */
public class Dwellings extends RecordSet<DwellingRow> {
	private SpatialUnitLevel spatialUnitLevel;
	private Random r = new Random();
	
	public Dwellings() {
		
	}
	public Dwellings(Database db, SpatialUnitLevel spatialUnitLevel) throws SQLException, InstantiationException, IllegalAccessException {
		setDb(db);
		this.spatialUnitLevel = spatialUnitLevel;
		rowList = db.select(DwellingRow.class, selectStatement());
		// calc variables - TODO: define callback in db.select for that
		for (DwellingRow dwelling : rowList) {
			preAddRow(dwelling);
		}
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
		switch (spatialUnitLevel) {
		case ZB:
			return "select dwellingId AS id, dwellingId, spatialUnitId, dwellingSize, totalYearlyDwellingCosts, " +
				"livingSpaceGroup6Id, costOfResidenceGroupId, constructionPeriod7Id from _DM_Dwellings order by dwellingId";
		case SGT:
			return "select dwellingId AS id, dwellingId, spatialUnitId_SGT AS spatialUnitId, dwellingSize, totalYearlyDwellingCosts, " +
				"livingSpaceGroup6Id, costOfResidenceGroupId, constructionPeriod7Id from _DM_Dwellings order by dwellingId";
		}
		throw new AssertionError("Unknown spatial unit level (not ZB or SGT)");
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
		return new DwellingRow(ObjectSource.INIT);
	}

	/**
	 * Link all households in the collection to their corresponding spatial unit according to the spatialunit-id
	 * @param spatialunits Collection of spatial units
	 */
	public void linkSpatialUnits(SpatialUnits spatialunits) {
		for (DwellingRow dwelling : rowList) {
			dwelling.setSpatialunit(spatialunits.lookup(dwelling.getSpatialunitId()));
		}
	}
	
	/**
	 * Return all dwellings that are not currently occupied by a household
	 * @return
	 */
	public ArrayList<DwellingRow> getFreeDwellings() {
		ArrayList<DwellingRow> freeDwellings = new ArrayList<DwellingRow>();
		for (DwellingRow dwelling : rowList) {
			if (dwelling.getHousehold() == null) {
				freeDwellings.add(dwelling);
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
		ArrayList<DwellingRow> freeDwellings = new ArrayList<DwellingRow>();
		for (DwellingRow dwelling : rowList) {
			if (dwelling.getHousehold() == null) {
				if (r.nextInt() <= Common.getDwellingsOnMarketShare()) {
					freeDwellings.add(dwelling);
				}
			}
		}
		return freeDwellings;
	}
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#preAddRow(at.sume.db.RecordSetRow)
	 */
	@Override
	public void preAddRow(DwellingRow dwelling) {
		// calculate dwelling size
		if (dwelling.getDwellingSize() == 0)
			dwelling.setDwellingSize(LivingSpaceGroup6.sampleLivingSpace(dwelling.getLivingSpaceGroup6Id()));
		// calculate dwelling costs
		if (dwelling.getHousehold() == null) {
			// Force calculation of dwelling costs to current market values if the dwelling is vacant
			dwelling.calcTotalYearlyDwellingCosts(true);
		} else {
			dwelling.calcTotalYearlyDwellingCosts(false);
		}
	}
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#add(at.sume.db.RecordSetRow)
	 */
	@Override
	public boolean add(DwellingRow row) {
		preAddRow(row);
		return super.add(row);
	}
}
