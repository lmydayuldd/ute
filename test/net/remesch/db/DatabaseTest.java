/**
 * 
 */
package net.remesch.db;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Collection;


import net.remesch.db.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.model.residential_satisfaction.UDPClassification.SpatialUnitUdp;

/**
 * @author Alexander Remesch
 *
 */
public class DatabaseTest {
	private Database db;
	
	/**
	 * Setup for unit test of {@link at.sume.dm.indicators.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 */
	@Before
	public void setUp() throws SQLException {
		db = Common.openDatabase();
	}
	
	/**
	 * Test method for {@link net.remesch.db.Database#select(java.lang.Class, java.lang.String)}.
	 */
	@Test
	public void testSelect() {
		Collection<SpatialUnitUdp> spatialUnitUdp = null;
		try {
			spatialUnitUdp = db.select(SpatialUnitUdp.class, "select * from _DM_SpatialUnitUdp");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals("Number of elements", 267, spatialUnitUdp.size());
	}

}
