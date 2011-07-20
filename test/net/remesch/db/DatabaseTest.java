/**
 * 
 */
package net.remesch.db;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.model.residential_satisfaction.UDPCentrality.SpatialUnitUdp;

/**
 * @author Alexander Remesch
 *
 */
public class DatabaseTest {
	private Database db, odb;
	
	public static class HouseholdTest {
		@SuppressWarnings("unused")
		private short modelYear;
		@SuppressWarnings("unused")
		private int householdId;
	}
	
	/**
	 * Setup for unit test of {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	@Before
	public void setUp() throws SQLException, ClassNotFoundException {
		db = Common.openDatabase();
		odb = Common.openOutputDatabase();
	}
	
	/**
	 * Test method for {@link net.remesch.db.Database#select(java.lang.Class, java.lang.String)}.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 */
	@Test
	public void testSelect() throws SQLException, InstantiationException, IllegalAccessException {
		Collection<SpatialUnitUdp> spatialUnitUdp = null;
		spatialUnitUdp = db.select(SpatialUnitUdp.class, "select * from _DM_SpatialUnitUdp");
		
		assertEquals("Number of elements", 267, spatialUnitUdp.size());
	}

	@Test
	public void testInsert() throws IllegalArgumentException, SQLException, IllegalAccessException {
		ArrayList<HouseholdTest> hhTestList = new ArrayList<HouseholdTest>();
		for (int i = 1; i != 100; i++) {
			HouseholdTest hhTest = new HouseholdTest();
			hhTest.householdId = i;
			hhTest.modelYear = 2001;
			hhTestList.add(hhTest);
		}
		String sqlStatement = "select * from _DM_Households";
		odb.insert(hhTestList, sqlStatement);
	}
}
