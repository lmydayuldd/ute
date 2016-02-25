/**
 * 
 */
package at.sume.dm.model.timeuse;

import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.entities.TimeUseRow;
import at.sume.dm.model.travel.SampleTravelTimesByDistance;
import at.sume.sampling.entities.DbTimeUseRow;
import net.remesch.db.Database;

/**
 * @author Alexander Remesch
 *
 */
public class SampleTimeUseTest {
	private SampleTimeUse sampleTimeUse;
	private SampleTravelTimesByDistance sampleTravelTimesByDistance;
	
	@Before
	public void setUp() throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
		Database db = Common.openDatabase();
		Common.init();
		SpatialUnits spatialUnits = new SpatialUnits(db, Common.getSpatialUnitLevel());
		sampleTravelTimesByDistance = new SampleTravelTimesByDistance(db, spatialUnits.getRowList().stream().map(i -> i.getSpatialUnitId()).collect(Collectors.toList()));
		sampleTimeUse = new SampleTimeUse();
		sampleTimeUse.registerSampleActivity(sampleTravelTimesByDistance);
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
		TravelTimeSamplingParameters t = new TravelTimeSamplingParameters();
		t.setInEducation(true);
		t.setOrigin(90101);
		t.setDestination(92310);
		t.setEmployed(false);
		t.setPersonId(1);
		List<DbTimeUseRow> timeUse = new ArrayList<DbTimeUseRow>();
		for (TimeUseRow row : sampleTimeUse.randomSample(t)) {
			DbTimeUseRow dt = new DbTimeUseRow(1, row);
			timeUse.add(dt);
		}
		output(timeUse);
		System.out.println(sampleTravelTimesByDistance.getTravelMode());
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
