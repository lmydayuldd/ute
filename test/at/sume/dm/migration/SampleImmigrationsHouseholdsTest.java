/**
 * 
 */
package at.sume.dm.migration;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.scenario_handling.Scenario;
import at.sume.dm.types.MigrationRealm;
import net.remesch.db.Database;

/**
 * @author Alexander Remesch
 *
 */
public class SampleImmigrationsHouseholdsTest {
	SampleMigratingHouseholds sampleImmigratingHouseholds; 

	@Before
	public void setUp() throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
		Database db = Common.openDatabase();
		Common.init();
		try {
			Scenario scenario = new Scenario(db, Common.getScenarioId());
//			sampleImmigratingHouseholds = new SampleMigratingHouseholds(scenario.getMigrationScenario(), scenario.getMigrationHouseholdSizeScenario(), scenario.getMigrationIncomeScenario());
			sampleImmigratingHouseholds = new SampleMigratingHouseholds("TEST", scenario.getMigrationHouseholdSizeScenario(), scenario.getMigrationIncomeScenario());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Test method for {@link at.sume.dm.migration.SampleMigratingHouseholds#sample(int)}.
	 */
	@Test
	public void testSample() {
		ArrayList<HouseholdRow> immigratingHouseholds = sampleImmigratingHouseholds.sample(2001, MigrationRealm.INTERNATIONAL_INCOMING);
//		assertEquals("Number of immigrating households", 6, immigratingHouseholds.size());
		int hc = 1;
		int pc = 1;
		for (HouseholdRow h : immigratingHouseholds) {
			System.out.println("Household " + hc++);
			for (PersonRow p : h.getMembers()) {
				System.out.println("Person " + pc++ + " age=" + p.getAge() + " sex=" + p.getSex());
			}
		}
	}

}
