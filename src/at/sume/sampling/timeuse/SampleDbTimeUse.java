/**
 * 
 */
package at.sume.sampling.timeuse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.sume.dm.model.travel.SampleTravelTimesByDistance;
import at.sume.dm.types.TravelMode;
import at.sume.sampling.entities.DbTimeUseRow;
import net.remesch.db.Database;

/**
 * Sample travel times & modes for each person
 * - Commuting depends on distance of workplaces
 * - Caring, Household, Leisure, Personal and other Travel depend on average distance to next local center
 *   (estimated as 2km for outer districts, 1km for middle districts, 0.5km for inner districts)
 * 
 * @author Alexander Remesch
 *
 */
public class SampleDbTimeUse {
	//TODO: implement interface and array here with kind-of observer registration, to enable easier expansion with more activities for sampling
	private SampleTravelTimesByDistance sampleTravelTimesByDistance;
//	private SampleTravelCaringTimes sampleTravelCaringTimes;
//	private SampleTravelHouseholdTimes sampleTravelHouseholdTimes;
//	private SampleTravelLeisureTimes sampleTravelLeisureTimes;
//	private SampleTravelPersonalTimes sampleTravelPersonalTimes;
//	private SampleTravelTimes sampleTravelTimes;
	
	private int commutingOrigin, commutingDest;
//	private boolean householdWithChildren = false;
	private boolean inEducation = false, working = false;
	private int personId = 0;
//	private int gender = 1;
	
	public SampleDbTimeUse(Database db, List<Integer> cells) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException, SQLException {
		// Create all activity-related sampling classes & load the sampling distributions
		// Sampling of commuting times & mode
		sampleTravelTimesByDistance = new SampleTravelTimesByDistance(db, cells);
//		sampleTravelCaringTimes = new SampleTravelCaringTimes(db);
//		sampleTravelHouseholdTimes = new SampleTravelHouseholdTimes(db);
//		sampleTravelLeisureTimes = new SampleTravelLeisureTimes(db);
//		sampleTravelPersonalTimes = new SampleTravelPersonalTimes(db);
//		sampleTravelTimes = new SampleTravelTimes(db);
	}

	public void setCommutingRoute(int origin, int destination) {
		commutingOrigin = origin;
		commutingDest = destination;
	}
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	public void setInEducation(boolean inEducation) {
		this.inEducation = inEducation;
	}
	public void setWorking(boolean working) {
		this.working = working;
	}
//	public void setGender(int gender) {
//		this.gender = gender;
//	}
//	public void setHouseholdWithChildren(boolean householdWithChilren) {
//		this.householdWithChildren = householdWithChilren;
//	}

	/**
	 * Perform sampling of daily activities for one person. Sampling result will be an ArrayList of activities and
	 * the total daily time use for each. A day may have more than 24 hrs (i.e. "long days") to reflect parallel activities
	 * and their total footprint. 
	 * @return
	 */
	public List<DbTimeUseRow> randomSample() {
		List<DbTimeUseRow> result = null;
//		boolean sampleOk = false;
//		while (!sampleOk) {
			result = new ArrayList<DbTimeUseRow>();
			DbTimeUseRow t = null;
			// Commuting
			if (working) {
				t = sampleTravelTimesByDistance.estimateTravelTime(personId, commutingOrigin, commutingDest);
				if (t.getMinutesPerDay() > 0)
					result.add(t);
			} else if (inEducation) {
				t = sampleTravelTimesByDistance.estimateTravelTime(personId, commutingOrigin, commutingDest, TravelMode.PUBLIC_TRANSPORT);
				if (t.getMinutesPerDay() > 0)
					result.add(t);
			}
//		}
		return result;
	}
	/**
	 * Get the travel mode for the last sampled set of travel activities
	 * @return public transport or motorized individual transport
	 */
	public TravelMode getTravelMode() {
		return sampleTravelTimesByDistance.getTravelMode();
	}
}
