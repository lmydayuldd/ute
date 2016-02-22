/**
 * 
 */
package at.sume.sampling.timeuse;

import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.sampling.entities.DbTimeUseRow;
import net.remesch.db.Database;

/**
 * @author Alexander Remesch
 *
 */
public class SampleDbTimeUseTest {
	private SampleDbTimeUse sampleDbTimeUse;
	
	@Before
	public void setUp() throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
		Database db = Common.openDatabase();
		Common.init();
		sampleDbTimeUse = new SampleDbTimeUse(db);
	}

	/**
	 * Test method for {@link at.sume.sampling.timeuse.SampleDbTimeUse#randomSample()}.
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Test
	public void testRandomSample() throws InstantiationException, IllegalAccessException, SQLException {
		// 1st person
//		sampleDbTimeUse.setHouseholdWithChildren(true);
		sampleDbTimeUse.setInEducation(true);
		sampleDbTimeUse.setCommutingOrigin(90101);
		sampleDbTimeUse.setCommutingDestination(3);
		sampleDbTimeUse.setWorking(false);
//		sampleDbTimeUse.setGender(2);
		sampleDbTimeUse.setPersonId(1);
		List<DbTimeUseRow> result = sampleDbTimeUse.randomSample();
		output(result);
	}
	
	private void output(List<DbTimeUseRow> result) {
		long totalTimeUse = 0;
		for(DbTimeUseRow r : result) {
			totalTimeUse += r.getMinutesPerDay();
			System.out.println(r.toString());
		}
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		System.out.println("total - " + totalTimeUse + " min - " + df.format((double)totalTimeUse / 60) + " h");
	}
}
