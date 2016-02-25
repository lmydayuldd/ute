/**
 * 
 */
package at.sume.sampling;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.TimeUseRow;
import net.remesch.db.Database;

/**
 * @author Alexander Remesch
 *
 */
public class GeneratePopulationTest {
	private static String timeUseSummaryFileName = "TimeUseSummary.txt";
	Database db;

	@Before
	public void setUp() throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
		db = Common.openDatabase();
		Common.init();
	}

	@Test
	public void testSaveTimeUseSummary() throws InstantiationException, IllegalAccessException, SQLException, IOException {
		String sqlStatement = "SELECT Activity, SUM(MinutesPerDay) AS avgTimeUse FROM _DM_TimeUse GROUP BY Activity;";
		List<TimeUseRow> timeUse = db.select(TimeUseRow.class, sqlStatement);
		GeneratePopulation.saveTimeUseSummary(timeUseSummaryFileName, timeUse);
	}
}
