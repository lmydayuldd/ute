/**
 * 
 */
package at.sume.dm.migration;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.migration.SampleMigratingHouseholds.MigrationsPerAgeSex;
import at.sume.dm.scenario_handling.Scenario;
import at.sume.dm.types.AgeGroup16;
import at.sume.dm.types.MigrationRealm;
import net.remesch.db.Database;

/**
 * @author Alexander Remesch
 *
 */
public class SampleImmigrationsHouseholdsTest {
	private SampleMigratingHouseholds sampleImmigratingHouseholds; 
	private List<MigrationsPerAgeSex> baseData;
	
	@Before
	public void setUp() throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
		Database db = Common.openDatabase();
		Common.init(false);
		try {
			Scenario scenario = new Scenario(db, Common.getScenarioId(), false);
			sampleImmigratingHouseholds = new SampleMigratingHouseholds(scenario.getMigrationScenario(), scenario.getMigrationPerAgeSexScenario(), scenario.getMigrationHouseholdSizeScenario(), scenario.getMigrationIncomeScenario());
//			sampleImmigratingHouseholds = new SampleMigratingHouseholds(scenario.getMigrationScenario(), "TEST", scenario.getMigrationHouseholdSizeScenario(), scenario.getMigrationIncomeScenario());
			// Load comparison data
			String selectStatement = "SELECT id, ageGroupId, sex, share " +
					"FROM _DM_MigrationAgeSex " +
					"WHERE scenarioName = '" + scenario.getMigrationPerAgeSexScenario() + "' " +
					"ORDER BY sex, ageGroupId";
			baseData = Common.db.select(MigrationsPerAgeSex.class, selectStatement);
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
		Map<Byte,Long> resultCountMale = new HashMap<Byte,Long>();
		Map<Byte,Long> resultCountFemale = new HashMap<Byte,Long>();
		// Count results
		double actualTotal = 0;
		for (HouseholdRow h : immigratingHouseholds) {
			actualTotal += h.getMemberCount();
			for (PersonRow p : h.getMembers()) {
				Map<Byte,Long> resultCount = p.getSex() == 1 ? resultCountFemale : resultCountMale;
				Long currentCount = resultCount.get(p.getAgeGroupId());
				if (currentCount == null) currentCount = 0L;
				resultCount.put(p.getAgeGroupId(), currentCount + 1);
			}
		}
		// Output comparison of baseData & resultCount
		double desiredTotal = baseData.stream().mapToDouble(i -> i.getShare()).sum();
		DecimalFormat df = new DecimalFormat("#0.00%");
		for(MigrationsPerAgeSex b : baseData) {
			long desired = (long)b.getShare();
			Map<Byte,Long> resultCount = b.getSex() == 1 ? resultCountFemale : resultCountMale;
			Long actual = resultCount.get(b.getAgeGroupId());
			if (actual == null) actual = 0L;
			System.out.println((b.getSex() == 1 ? "female " : "male   ") + AgeGroup16.getAgeGroupName(b.getAgeGroupId()) + 
					": desired = " + df.format(desired / desiredTotal) + " (" + desired + ")" + 
					", actual = " + df.format(actual / actualTotal) + " (" + actual + ") ");
		}
		System.out.println("Total: desired = " + desiredTotal + ", actual = " + actualTotal);
	}

}
