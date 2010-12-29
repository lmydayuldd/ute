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
			"from [VZ_2001_Haushalte (ZB) relational] order by SpatialUnitId, HouseholdSize;";
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
