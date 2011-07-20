/**
 * 
 */
package at.sume.dm.types;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import net.remesch.db.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;


/**
 * @author Alexander Remesch
 *
 */
public class IncomeGroupTest {
	Database db;

	@Test(expected=AssertionError.class)
	  public void testAssertionsEnabled() {
	    assert(false);
	  }

	/**
	 * Setup for unit test of {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	@Before
	public void setUp() throws SQLException, ClassNotFoundException {
		db = Common.openDatabase();
		Common.init();
	}

	@Test
	public void testGetIncomeGroupId() {
		short ret;
		ret = IncomeGroup.getIncomeGroupId(12000);
		assertEquals("income group for 12000", 8, ret);
		ret = IncomeGroup.getIncomeGroupId(45000);
		assertEquals("income group for 45000", 13, ret);
		ret = IncomeGroup.getIncomeGroupId(500);
		assertEquals("income group for 500", 2, ret);
	}
}
