/**
 * 
 */
package at.sume.dm.entities;

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
public class DwellingsTest {
	Database db;

	@Before
	public void setUp() throws SQLException {
		db = Common.openDatabase();
		Common.init();
	}

	@Test
	public void testDwellings() {
		Dwellings dwellings = null;
		try {
			dwellings = new Dwellings(db, Common.getSpatialUnitLevel());
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
		assertEquals("Number of elements", 908310, dwellings.size());
	}
}
