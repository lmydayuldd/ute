package at.sume.dm.model.travel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.sume.dm.Common;
import at.sume.dm.model.timeuse.SampleActivity;
import at.sume.dm.model.timeuse.TimeUseSamplingParameters;
import at.sume.dm.scenario_handling.Scenario;
import at.sume.dm.types.TravelMode;
import at.sume.sampling.distributions.TravelTimeRow;
import ec.util.MersenneTwisterFast;
import net.remesch.db.Database;

public class SampleTravelTimesByDistance implements SampleActivity {
	private class TravelInfo {
		public double hoursMIT;
		public double hoursPublic;
		public double distanceKm;
		public short beginYear;
	}
	private HashMap<Integer,HashMap<Integer,List<TravelInfo>>> travelInfo;
	private TravelMode travelMode;
	private static final String timeUseTag = "travel work";
	private TimeUseSamplingParameters timeUseSamplingParameters;
	
	public SampleTravelTimesByDistance(Database db, Scenario scenario, List<Integer> cells) throws InstantiationException, IllegalAccessException, SQLException {
		travelInfo = new HashMap<Integer,HashMap<Integer,List<TravelInfo>>>();
		// Load _DM_TravelTimes & build nested HashSets
		for (Integer s : cells) {
			if (s == 1 || s == 3)
				continue;
			HashMap<Integer,List<TravelInfo>> travelInfoOrigin = new HashMap<Integer,List<TravelInfo>>();
			travelInfo.put(s, travelInfoOrigin);
			String sqlStatement = "SELECT Origin, Destination, HoursMIT, HoursPublic, DistanceKm, StartYear " +
					"FROM _DM_TravelTimes " +
					"WHERE ScenarioName = '" + scenario.getTravelTimesScenario() + "' AND origin = " + s +
					" ORDER BY Destination, StartYear;";
			ArrayList<TravelTimeRow> travelTimes = db.select(TravelTimeRow.class, sqlStatement);
			assert travelTimes.size() > 0 : "No records found from '" + sqlStatement + "' for cell " + s + " - maybe wrong SpatialUnitLevel?";
			int dest = 0;
			List<TravelInfo> travelInfoList = new ArrayList<TravelInfo>();
			for (TravelTimeRow t : travelTimes) {
				if (dest != t.destination) {
					if (dest != 0) {
						travelInfoOrigin.put(dest, travelInfoList);
					}
					travelInfoList = new ArrayList<TravelInfo>();
				}
				TravelInfo travelInfo = new TravelInfo();
				travelInfo.hoursMIT = t.hoursMit;
				travelInfo.hoursPublic = t.hoursPublic;
				travelInfo.beginYear = (short) t.startYear;
				travelInfo.distanceKm = t.distanceKm;
				travelInfoList.add(travelInfo);
				dest = t.destination;
			}
		}
	}
	/**
	 * 
	 * @param modelYear
	 * @return
	 */
	private TravelInfo getTravelInfoYear(List<TravelInfo> tl, short modelYear) {
		TravelInfo result = null;
		if (tl.size() == 1) {
			return tl.get(0);
		} else {
			for (TravelInfo t : tl) {
				if (modelYear >= t.beginYear) {
					if (result == null)
						result = t;
					else if (t.beginYear > result.beginYear)
						result = t;
				}
			}
		}
		return result;
	}
	/**
	 * Calculate the travel time to destination & determine the travel mode.
	 * The travel mode can be fetched with getTravelMode()
	 * @param origin
	 * @param destination
	 * @param modelYear
	 * @return
	 */
	private int estimateTravelTime(int origin, int destination) {
		//TODO: don't know at the moment what to do with commutings out of Vienna
		if ((destination == 3) || (destination == 1)) {
			travelMode = TravelMode.MOTORIZED_INDIVIDUAL_TRANSPORT;
			return 60;
		}
		MersenneTwisterFast r = new MersenneTwisterFast();
		TravelInfo ti = getTravelInfoYear(travelInfo.get(origin).get(destination), Common.getModelYear());
		int travelTimeMIT = (int)Math.round(ti.hoursMIT * 60);	      // convert to minutes
		int travelTimePublic = (int)Math.round(ti.hoursPublic * 60);  // convert to minutes
		double pTravelMIT = 0.1; // p for travel by car even if it takes longer than public transport
		if (travelTimeMIT < travelTimePublic) {
			pTravelMIT = (travelTimeMIT - travelTimePublic) / travelTimeMIT;
		}
		if (r.nextDouble() < pTravelMIT) {
			travelMode = TravelMode.MOTORIZED_INDIVIDUAL_TRANSPORT;
			return travelTimeMIT;
		} else {
			travelMode = TravelMode.PUBLIC_TRANSPORT;
			return travelTimePublic;
		}
	}
	/**
	 * Calculate the travel time to destination & determine the travel mode.
	 * The travel mode can be fetched with getTravelMode()
	 * @param origin
	 * @param destination
	 * @param mode
	 * @return
	 */
	private int estimateTravelTime(int origin, int destination, TravelMode mode) {
		//TODO: don't know at the moment what to do with commutings out of Vienna
		travelMode = mode;
		if ((destination == 3) || (destination == 1)) {
			return 60;
		}
		int travelTime = -1;
		TravelInfo ti = getTravelInfoYear(travelInfo.get(origin).get(destination), Common.getModelYear());
		switch (mode) {
		case PUBLIC_TRANSPORT:
			travelTime = (int)Math.round(ti.hoursPublic * 60);
			break;
		case MOTORIZED_INDIVIDUAL_TRANSPORT:
			travelTime = (int)Math.round(ti.hoursMIT * 60);
			break;
		}
		return travelTime;
	}
	/**
	 * Return the average distance in km between cell origin and cell destination
	 * @param origin
	 * @param destination
	 * @param modelYear
	 * @return
	 */
	public double getTravelDistance(int origin, int destination) {
		//TODO: don't know at the moment what to do with commutings out of Vienna
		if ((destination == 3) || (destination == 1)) {
			return 0;
		}
		TravelInfo ti = getTravelInfoYear(travelInfo.get(origin).get(destination), Common.getModelYear());
		return ti.distanceKm;
	}
//	public DbTimeUseRow estimateTravelTime(int personId, int origin, int destination) {
//		return new DbTimeUseRow(personId, timeUseTag, (int) estimateTravelTime(origin, destination));
//	}
//	public DbTimeUseRow estimateTravelTime(int personId, int origin, int destination, TravelMode mode) {
//		return new DbTimeUseRow(personId, timeUseTag, (int) estimateTravelTime(origin, destination, mode));
//	}
	/**
	 * Return the travel mode previously determined by estimateTravelTime()
	 * @return
	 */
	public TravelMode getTravelMode() {
		return travelMode;
	}
	@Override
	public int sampleMinutesPerDay() {
		if ((timeUseSamplingParameters.getOrigin() != 0) && (timeUseSamplingParameters.getDestination() != 0)) {
			if (timeUseSamplingParameters.isEmployed()) {
				return estimateTravelTime(timeUseSamplingParameters.getOrigin(), timeUseSamplingParameters.getDestination());
			} else if (timeUseSamplingParameters.isInEducation()) {
				return estimateTravelTime(timeUseSamplingParameters.getOrigin(), timeUseSamplingParameters.getDestination(), TravelMode.PUBLIC_TRANSPORT);
			}
		}
		return 0;
	}
	@Override
	public double sampleHoursPerDay() {
		return ((double)sampleMinutesPerDay()) / 60;
	}
	@Override
	public void setSamplingParameterSource(TimeUseSamplingParameters timeUseSamplingParameters) {
		this.timeUseSamplingParameters = timeUseSamplingParameters;
	}
	@Override
	public String getActivityName() {
		return timeUseTag;
	}
}
