/**
 * 
 */
package at.sume.sampling.timeuse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.sume.dm.Common;
import at.sume.dm.types.TravelMode;
import at.sume.sampling.entities.DbTimeUseRow;
import net.remesch.db.Database;

/**
 * @author Alexander Remesch
 *
 */
public class SampleDbTimeUse {
	//TODO: implement interface and array here with kind-of observer registration, to enable easier expansion with more activities for sampling
	private SampleTravelTimesByDistance sampleTravelTimesByDistance;
	private SampleCaringTimes sampleCaringTimes;
	private SampleTravelCaringTimes sampleTravelCaringTimes;
	private SampleOtherTimes sampleOtherTimes;
	private SampleHouseholdTimes sampleHouseholdTimes;
	private SampleLeisureTimes sampleLeisureTimes;
	private SampleTravelHouseholdTimes sampleTravelHouseholdTimes;
	private SampleTravelLeisureTimes sampleTravelLeisureTimes;
	private SamplePersonalTimes samplePersonalTimes;
	private SampleTravelPersonalTimes sampleTravelPersonalTimes;
	private SampleTravelTimes sampleTravelTimes;
	
	private TimeUseTargetCheck timeUseTargetAvg;

	private int commutingDest;
	private boolean householdWithChildren = false, inEducation = false, working = false;
	private int personId = 0, gender = 1;
	
	private int maxTries = 100;
	
	public SampleDbTimeUse(Database db) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException, SQLException {
		// Create all activity-related sampling classes & load the sampling distributions
		// Sampling of commuting times & mode
		sampleTravelTimesByDistance = new SampleTravelTimesByDistance(db);
		sampleCaringTimes = new SampleCaringTimes(db);
		sampleTravelCaringTimes = new SampleTravelCaringTimes(db);
		sampleOtherTimes = new SampleOtherTimes(db);
		sampleHouseholdTimes = new SampleHouseholdTimes(db);
		sampleLeisureTimes = new SampleLeisureTimes(db);
		sampleTravelHouseholdTimes = new SampleTravelHouseholdTimes(db);
		sampleTravelLeisureTimes = new SampleTravelLeisureTimes(db);
		samplePersonalTimes = new SamplePersonalTimes(db);
		sampleTravelPersonalTimes = new SampleTravelPersonalTimes(db);
		sampleTravelTimes = new SampleTravelTimes(db);
		
		timeUseTargetAvg = new TimeUseTargetCheck(db);
		
		maxTries = Byte.parseByte(Common.getSysParamDataPreparation("MaxTriesKeepTimeUseInRange"));
	}

	public void setCommutingOrigin(int origin) throws InstantiationException, IllegalAccessException, SQLException {
		sampleTravelTimesByDistance.loadTravelTimes(origin);
	}
	public void setCommutingDestination(int destination) {
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
	public void setGender(int gender) {
		this.gender = gender;
	}
	public void setHouseholdWithChildren(boolean householdWithChilren) {
		this.householdWithChildren = householdWithChilren;
	}

	/**
	 * Perform sampling of daily activities for one person. Sampling result will be an ArrayList of activities and
	 * the total daily time use for each. A day may have more than 24 hrs (i.e. "long days") to reflect parallel activities
	 * and their total footprint. 
	 * @return
	 */
	public List<DbTimeUseRow> randomSample() {
		int tries = 0;
		List<DbTimeUseRow> result = null;
		boolean sampleOk = false, retry = false;
		while (!sampleOk) {
			int totalTimeUse = 0;
			result = new ArrayList<DbTimeUseRow>();
			DbTimeUseRow t = null, best = null;
			// Caring time
			// TODO: put this into SampleCaringTimes class
//			tries = 0;
//			do {
//				retry = false;
//				t = sampleCaringTimes.randomSample(personId, householdWithChildren);
//				if (tries++ >= maxTries)
//					break;
//				if (timeUseTargetAvg.belowTargetRange(t)) {
//					retry = true;
//					if (best == null || best.getMinutesPerDay() < t.getMinutesPerDay()) // t is higher -> new best
//						best = t;
//				}
//				if (timeUseTargetAvg.abowTargetRange(t)) {
//					retry = true;
//					if (best == null || best.getMinutesPerDay() > t.getMinutesPerDay()) // t is lower -> new best
//						best = t;
//				}
//			} while (retry);
			t = sampleCaringTimes.randomSample(personId, householdWithChildren);
			if (t.getMinutesPerDay() > 0) {
				result.add(t);
				totalTimeUse += t.getMinutesPerDay();
				// Travel caring - only when caring time is there
				t = sampleTravelCaringTimes.randomSample(personId, householdWithChildren);
				if (t.getMinutesPerDay() > 0) {
					result.add(t);
					totalTimeUse += t.getMinutesPerDay();
				}
			}
			// Commuting
			if (working) {
				t = sampleTravelTimesByDistance.estimateTravelTime(personId, commutingDest);
				if (t.getMinutesPerDay() > 0)
					result.add(t);
				totalTimeUse += t.getMinutesPerDay();
			} else if (inEducation) {
				t = sampleTravelTimesByDistance.estimateTravelTime(personId, commutingDest, TravelMode.PUBLIC_TRANSPORT);
				if (t.getMinutesPerDay() > 0)
					result.add(t);
				totalTimeUse += t.getMinutesPerDay();
			}
			// Other (mainly work/education)
			t = sampleOtherTimes.randomSample(personId, inEducation, working);
			if (t.getMinutesPerDay() > 0)
				result.add(t);
			totalTimeUse += t.getMinutesPerDay();
			// Household
			t = sampleHouseholdTimes.randomSample(personId, gender);
			if (t.getMinutesPerDay() > 0) {
				result.add(t);
				totalTimeUse += t.getMinutesPerDay();
				// Travel household - only when household time is there
				t = sampleTravelHouseholdTimes.randomSample(personId, gender);
				if (t.getMinutesPerDay() > 0) {
					result.add(t);
					totalTimeUse += t.getMinutesPerDay();
				}
			}
			// Leisure
			t = sampleLeisureTimes.randomSample(personId, gender);
			if (t.getMinutesPerDay() > 0) {
				result.add(t);
				totalTimeUse += t.getMinutesPerDay();
				// Travel leisure - only with leisure time
				t = sampleTravelLeisureTimes.randomSample(personId, gender);
				if (t.getMinutesPerDay() > 0) {
					result.add(t);
					totalTimeUse += t.getMinutesPerDay();
				}
			}
			// Personal
			t = samplePersonalTimes.randomSample(personId);
			if (t.getMinutesPerDay() > 0) {
				result.add(t);
				totalTimeUse += t.getMinutesPerDay();
				// Travel personal - only with personal time
				t = sampleTravelPersonalTimes.randomSample(personId);
				if (t.getMinutesPerDay() > 0) {
					result.add(t);
					totalTimeUse += t.getMinutesPerDay();
				}
			}
			// Travel
			t = sampleTravelTimes.randomSample(personId);
			if (t.getMinutesPerDay() > 0)
				result.add(t);
			totalTimeUse += t.getMinutesPerDay();
			
			// total time use between 24 and 40 hrs?
			if ((totalTimeUse > 1440) && (totalTimeUse < 2400)) {
				// Check total average time use before adding the record
				timeUseTargetAvg.addActualTimeUse(result);
				sampleOk = true;
			}
		}
		return result;
	}
}
