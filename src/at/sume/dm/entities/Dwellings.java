/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import net.remesch.db.Database;
import at.sume.db.RecordSet;
import at.sume.dm.Common;
import at.sume.dm.model.residential_mobility.RentPerSpatialUnit;
import at.sume.dm.types.LivingSpaceGroup;

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
		return "select dwellingId AS id, dwellingId, spatialUnitId, dwellingSize, totalYearlyDwellingCosts, " +
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
		return new DwellingRow();
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
			if (dwelling.getHousehold().equals(null)) {
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
		Random r = new Random();
		ArrayList<DwellingRow> freeDwellings = new ArrayList<DwellingRow>();
		for (DwellingRow dwelling : rowList) {
			if (dwelling.getHousehold().equals(null)) {
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
			dwelling.setDwellingSize(LivingSpaceGroup.sampleLivingSpace(dwelling.getLivingSpaceGroup6Id()));
		// calculate dwelling costs
		if (dwelling.getTotalYearlyDwellingCosts() == 0) {
			Random r = new Random();
			long yearlyRentPer100Sqm = RentPerSpatialUnit.getYearlyAverageRentPer100Sqm(dwelling.getSpatialunitId()) / 100;
			// TODO: 20% random deviance from the avg. rent price -> sysparam!
			yearlyRentPer100Sqm = yearlyRentPer100Sqm + Math.round(yearlyRentPer100Sqm * (r.nextGaussian() - 0.5) * 0.1);
			long dwellingCosts = Math.round(dwelling.getDwellingSize() * yearlyRentPer100Sqm / 100);
			dwelling.setTotalYearlyDwellingCosts(dwellingCosts);
		}
	}
}
