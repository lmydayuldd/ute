package at.sume.dm.model.travel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.sume.dm.types.TravelMode;
import at.sume.sampling.distributions.TravelTimeRow;
import at.sume.sampling.entities.DbTimeUseRow;
import ec.util.MersenneTwisterFast;
import net.remesch.db.Database;

public class SampleTravelTimesByDistance {
	private HashMap<Integer,HashMap<Integer,Double>> travelTimesMIT;
	private HashMap<Integer,HashMap<Integer,Double>> travelTimesPublic;
	private HashMap<Integer,HashMap<Integer,Double>> travelDistances;
	private TravelMode travelMode;
	private static final String timeUseTag = "travel work";
	
	public SampleTravelTimesByDistance(Database db, List<Integer> cells) throws InstantiationException, IllegalAccessException, SQLException {
		// TODO: make one out of the three HashMaps
		travelTimesMIT = new HashMap<Integer,HashMap<Integer,Double>>();
		travelTimesPublic = new HashMap<Integer,HashMap<Integer,Double>>();
		travelDistances = new HashMap<Integer,HashMap<Integer,Double>>();
		// Load _DM_TravelTimes & build nested HashSets
		for (Integer s : cells) {
			if (s == 1 || s == 3)
				continue;
			HashMap<Integer,Double> tMIT = new HashMap<Integer,Double>();
			HashMap<Integer,Double> tPublic = new HashMap<Integer,Double>();
			HashMap<Integer,Double> tDist = new HashMap<Integer,Double>();
			travelTimesMIT.put(s, tMIT);
			travelTimesPublic.put(s, tPublic);
			travelDistances.put(s, tDist);
			String sqlStatement = "SELECT Origin, Destination, Mode, Hours, DistanceKm " +
					"FROM _DM_TravelTimes " +
					"WHERE ScenarioName = 'SUME_TransportModel' AND origin = " + s +
					" ORDER BY Mode, Destination;";
			ArrayList<TravelTimeRow> travelTimes = db.select(TravelTimeRow.class, sqlStatement);
			assert travelTimes.size() > 0 : "No records found from '" + sqlStatement + "'";
			for (TravelTimeRow t : travelTimes) {
				if (t.mode == TravelMode.PUBLIC_TRANSPORT.getValue()) {
					tPublic.put(t.destination, t.hours);
					tDist.put(t.destination, t.distanceKm);
				} else if (t.mode == TravelMode.MOTORIZED_INDIVIDUAL_TRANSPORT.getValue()) {
					tMIT.put(t.destination, t.hours);
				}
			}
		}
	}
	/**
	 * Calculate the travel time to destination & determine the travel mode.
	 * The travel mode can be fetched with getTravelMode()
	 * @param destination
	 * @return Travel time in minutes
	 */
	public long estimateTravelTime(int origin, int destination) {
		//TODO: don't know at the moment what to do with commutings out of Vienna
		if ((destination == 3) || (destination == 1)) {
			travelMode = TravelMode.MOTORIZED_INDIVIDUAL_TRANSPORT;
			return 60;
		}
		MersenneTwisterFast r = new MersenneTwisterFast();
		HashMap<Integer,Double> t = travelTimesMIT.get(origin);
		long travelTimeMIT = Math.round(t.get(destination) * 60);	      // convert to minutes
		t = travelTimesPublic.get(origin);
		long travelTimePublic = Math.round(t.get(destination) * 60);  // convert to minutes
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
	 * @param destination
	 * @param mode
	 * @return Travel time in minutes
	 */
	public long estimateTravelTime(int origin, int destination, TravelMode mode) {
		//TODO: don't know at the moment what to do with commutings out of Vienna
		if ((destination == 3) || (destination == 1)) {
			travelMode = mode;
			return 60;
		}
		long travelTime = -1;
		HashMap<Integer,Double> t;
		switch (mode) {
		case PUBLIC_TRANSPORT:
			t = travelTimesPublic.get(origin);
			travelTime = Math.round(t.get(destination) * 60);
			break;
		case MOTORIZED_INDIVIDUAL_TRANSPORT:
			t = travelTimesMIT.get(origin);
			travelTime = Math.round(t.get(destination) * 60);
			break;
		}
		return travelTime;
	}
	public DbTimeUseRow estimateTravelTime(int personId, int origin, int destination) {
		return new DbTimeUseRow(personId, timeUseTag, (int) estimateTravelTime(origin, destination));
	}
	public DbTimeUseRow estimateTravelTime(int personId, int origin, int destination, TravelMode mode) {
		return new DbTimeUseRow(personId, timeUseTag, (int) estimateTravelTime(origin, destination, mode));
	}
	/**
	 * Return the travel mode previously determined by estimateTravelTime()
	 * @return
	 */
	public TravelMode getTravelMode() {
		return travelMode;
	}
}
