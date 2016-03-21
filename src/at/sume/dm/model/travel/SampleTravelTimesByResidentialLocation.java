/**
 * 
 */
package at.sume.dm.model.travel;

import java.sql.SQLException;
import java.util.List;

import at.sume.dm.model.timeuse.SampleActivity;
import at.sume.dm.model.timeuse.TimeUseSamplingParameters;
import at.sume.dm.scenario_handling.Scenario;
import at.sume.dm.types.TravelMode;
import net.remesch.db.Database;

/**
 * Sample non-commuting travel times by using the TimeUseType and the TravelTimeModifieres based on the residential location
 * 
 * @author Alexander Remesch
 *
 */
public class SampleTravelTimesByResidentialLocation implements SampleActivity {

	public SampleTravelTimesByResidentialLocation(Database db, Scenario scenario, List<Integer> cells) throws InstantiationException, IllegalAccessException, SQLException {
		// 1) _UTE_TimeUseTypes einlesen
		// 2) je nach Zählbezirk die Modifiers einrechnen und das Ergebnis zurückgeben
	}
	
	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.SampleActivity#setSamplingParameterSource(at.sume.dm.model.timeuse.TimeUseSamplingParameters)
	 */
	@Override
	public void setSamplingParameterSource(TimeUseSamplingParameters timeUseSamplingParameters) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.SampleActivity#sampleMinutesPerDay()
	 */
	@Override
	public int sampleMinutesPerDay() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.SampleActivity#sampleHoursPerDay()
	 */
	@Override
	public double sampleHoursPerDay() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.SampleActivity#getTravelMode()
	 */
	@Override
	public TravelMode getTravelMode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.SampleActivity#getActivityName()
	 */
	@Override
	public String getActivityName() {
		// TODO Auto-generated method stub
		return null;
	}

}
