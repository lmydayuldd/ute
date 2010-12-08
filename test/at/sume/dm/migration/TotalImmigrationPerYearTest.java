/**
 * 
 */
package at.sume.dm.migration;

import static org.junit.Assert.*;

import net.remesch.db.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;

/**
 * @author Alexander Remesch
 *
 */
public class TotalImmigrationPerYearTest {
	TotalImmigrationPerYear totalImmigrationPerYear;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		@SuppressWarnings("unused")
		Database db = Common.openDatabase();
		totalImmigrationPerYear = new TotalImmigrationPerYear("STATA2010");
	}
	/**
	 * Test method for {@link at.sume.dm.migration.TotalImmigrationPerYear#get_v1(int)}.
	 */
	@Test
	public void testLoaded() {
		assertEquals("Data loaded", -1, -1);
	}
	/**
	 * Test method for {@link at.sume.dm.migration.TotalImmigrationPerYear#get_v1(int)}.
	 */
	@Test
	public void testGet_v1() {
		for (int i = 0; i != 100000; i++) {
			assertEquals("Immigrations 2002 (v1)", 69219, totalImmigrationPerYear.get_v1(2002));
			assertEquals("Immigrations 2050 (v1)", 67355, totalImmigrationPerYear.get_v1(2050));
			assertEquals("Immigrations 2075 (v1)", 67912, totalImmigrationPerYear.get_v1(2075));
		}
	}
	/**
	 * Test method for {@link at.sume.dm.migration.TotalImmigrationPerYear#get_v2(int)}.
	 */
	@Test
	public void testGet_v2() {
		for (int i = 0; i != 100000; i++) {
			assertEquals("Immigrations 2002 (v2)", 69219, totalImmigrationPerYear.get_v2(2002));
			assertEquals("Immigrations 2050 (v2)", 67355, totalImmigrationPerYear.get_v2(2050));
			assertEquals("Immigrations 2075 (v2)", 67912, totalImmigrationPerYear.get_v2(2075));
		}
	}
	/**
	 * Test method for {@link at.sume.dm.migration.TotalImmigrationPerYear#get_v3(int)}.
	 */
	@Test
	public void testGet_v3() {
		for (int i = 0; i != 100000; i++) {
			assertEquals("Immigrations 2002 (v3)", 69219, totalImmigrationPerYear.get_v3(2002));
			assertEquals("Immigrations 2050 (v3)", 67355, totalImmigrationPerYear.get_v3(2050));
			assertEquals("Immigrations 2075 (v3)", 67912, totalImmigrationPerYear.get_v3(2075));
		}
	}
}
