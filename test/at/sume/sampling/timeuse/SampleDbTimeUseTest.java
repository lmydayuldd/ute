/**
 * 
 */
package at.sume.sampling.timeuse;

import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.SpatialUnits;
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
		SpatialUnits spatialUnits = new SpatialUnits(db, Common.getSpatialUnitLevel());
		sampleDbTimeUse = new SampleDbTimeUse(db, spatialUnits.getRowList().stream().map(i -> i.getSpatialUnitId()).collect(Collectors.toList()));
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
		sampleDbTimeUse.setCommutingRoute(90101, 3);
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
