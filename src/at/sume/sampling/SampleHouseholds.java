/**
 * 
 */
package at.sume.sampling;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import at.sume.sampling.distributions.HouseholdsPerSpatialUnit;

import net.remesch.db.Database;

/**
 * Get number of households and persons per spatial unit and household size
 * Base table is "vz_2001_haushalte (zb) relational"
 * 
 * @author Alexander Remesch
 */
public class SampleHouseholds implements Iterable<HouseholdsPerSpatialUnit> {
	private ArrayList<HouseholdsPerSpatialUnit> householdsPerSpatialUnit;
	
	/**
	 * Load distribution of households per spatial unit from database
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public SampleHouseholds(Database db) throws SQLException, InstantiationException, IllegalAccessException
	{
		String sqlStatement = "select SpatialUnitId, HouseholdSize, HouseholdCount, PersonCount " + 
			"from [VZ_2001_Haushalte (ZB) relational] " +
//			"where SpatialUnitId = 91101 or SpatialUnitId = 91001 " +
			"order by SpatialUnitId, HouseholdSize";
		householdsPerSpatialUnit = db.select(HouseholdsPerSpatialUnit.class, sqlStatement);
	}

	/**
	 * Load distribution of households per spatial unit from database and limit it by certain
	 * criteria for purpose of faster loading during testing
	 * @param db
	 * @param whereClause
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public SampleHouseholds(Database db, String whereClause) throws SQLException, InstantiationException, IllegalAccessException
	{
		// TODO: in order to generate households per SGT, this table would have to be changed!!!!
		String sqlStatement = "select SpatialUnitId, HouseholdSize, HouseholdCount, PersonCount " + 
			"from [VZ_2001_Haushalte (ZB) relational] " +
			"where " + whereClause +
			" order by SpatialUnitId, HouseholdSize";
		householdsPerSpatialUnit = db.select(HouseholdsPerSpatialUnit.class, sqlStatement);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<at.sume.sampling.distributions.HouseholdsPerSpatialUnit> iterator() {
		return householdsPerSpatialUnit.iterator();
	}
}
