package at.sume.dm.migration;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.migration.SampleMigratingHouseholds.MigrationsPerAgeSex;
import at.sume.dm.scenario_handling.Scenario;
import at.sume.dm.types.AgeGroup16;
import at.sume.sampling.Distribution;
import net.remesch.db.Database;

public class MigrationsPerAgeSexTest {
	private Distribution<MigrationsPerAgeSex> migrationsPerAgeSex;
	private List<MigrationsPerAgeSex> baseData;
	private static final int TOTAL_SAMPLES = 1000;

	@Before
	public void setUp() throws ClassNotFoundException, SQLException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, InstantiationException {
		Database db = Common.openDatabase();
		Common.init(false);
		Scenario scenario = new Scenario(db, Common.getScenarioId(), false);
		String selectStatement = "SELECT id, ageGroupId, sex, share " +
				"FROM _DM_MigrationAgeSex " +
				"WHERE scenarioName = '" + scenario.getMigrationPerAgeSexScenario() + "' " +
				"ORDER BY sex, ageGroupId";
		baseData = Common.db.select(MigrationsPerAgeSex.class, selectStatement);
		migrationsPerAgeSex = new Distribution<MigrationsPerAgeSex>(baseData, "share");
		assert migrationsPerAgeSex.size() > 0 : "No rows selected from _DM_MigrationAgeSex (scenarioName = " + scenario.getMigrationPerAgeSexScenario() + ")";

	}

	@Test
	public void test() {
		Map<Byte,Long> resultCountMale = new HashMap<Byte,Long>();
		Map<Byte,Long> resultCountFemale = new HashMap<Byte,Long>();
		// Sample & count results
		for (int i = 0; i != TOTAL_SAMPLES; i++) {
			int index = migrationsPerAgeSex.randomSample();
			MigrationsPerAgeSex m = migrationsPerAgeSex.get(index);
			Map<Byte,Long> resultCount = m.getSex() == 1 ? resultCountFemale : resultCountMale;
			Long currentCount = resultCount.get(m.getAgeGroupId());
			if (currentCount == null) currentCount = 0L;
			resultCount.put(m.getAgeGroupId(), currentCount + 1);
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
					", actual = " + df.format(actual / (double) TOTAL_SAMPLES) + " (" + actual + ") ");
		}
		System.out.println("Total: desired = " + desiredTotal + ", actual = " + TOTAL_SAMPLES);
	}

}
