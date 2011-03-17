/**
 * 
 */
package at.sume.dm.migration;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.db.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public class SampleImmigrationsHouseholdsTest {
	SampleMigratingHouseholds sampleImmigratingHouseholds; 

	@Before
	public void setUp() throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		@SuppressWarnings("unused")
		Database db = Common.openDatabase();
		Common.init();
		sampleImmigratingHouseholds = new SampleMigratingHouseholds("STATA2010", "NEUZUD");
	}

	/**
	 * Test method for {@link at.sume.dm.migration.SampleMigratingHouseholds#sample(int)}.
	 */
	@Test
	public void testSample() {
		ArrayList<HouseholdRow> immigratingHouseholds = sampleImmigratingHouseholds.sample(2001, MigrationRealm.INTERNATIONAL);
		assertEquals("Number of immigrating households", 0, immigratingHouseholds.size()); 
	}

}
