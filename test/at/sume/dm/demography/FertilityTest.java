/**
 * 
 */
package at.sume.dm.demography;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.scenario_handling.Scenario;
import net.remesch.db.Database;
import net.remesch.util.Random;

/**
 * @author Alexander Remesch
 *
 */
public class FertilityTest {
	private Database db;
	private Fertility fertility;
	private static final int TOTAL_SAMPLES = 10000;
	private Random r = new Random();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		db = Common.openDatabase();
		Common.init(false);
		Scenario scenario = new Scenario(db, Common.getScenarioId(), false);
		fertility = new Fertility(db, scenario.getFertilityScenario());
	}

	/**
	 * Test method for {@link at.sume.dm.demography.Fertility#probability(byte, short)}.
	 */
	@Test
	public void testProbability() {
		int hitCount = 0;
		double p = fertility.probability((byte)5);
		for (int sampleCount = 0; sampleCount != TOTAL_SAMPLES; sampleCount++) {
			double rand = r.nextDouble();
			if (rand < p) {
				hitCount++;
			}
		}
		System.out.println("hitCount = " + hitCount + ", %hits = " + (double) hitCount / TOTAL_SAMPLES + ", p = " + p);
	}
}
