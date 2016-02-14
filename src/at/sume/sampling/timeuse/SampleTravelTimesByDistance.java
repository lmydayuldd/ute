package at.sume.sampling.timeuse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import at.sume.dm.types.TravelMode;
import at.sume.sampling.distributions.TravelTimeRow;
import at.sume.sampling.entities.DbTimeUseRow;
import ec.util.MersenneTwisterFast;
import net.remesch.db.Database;

public class SampleTravelTimesByDistance {
	private Database db;
	private HashMap<Integer,Double> travelTimesMIT;
	private HashMap<Integer,Double> travelTimesPublic;
	private TravelMode travelMode;
	private static final String timeUseTag = "travel work";
	
	public SampleTravelTimesByDistance(Database db) {
		this.db = db;
	}
	
	public void loadTravelTimes(int origin) throws InstantiationException, IllegalAccessException, SQLException {
		// Load _DM_TravelTimes & build nested HashSets
		String sqlStatement = "SELECT Origin, Destination, Mode, Hours " +
				"FROM _DM_TravelTimes " +
				"WHERE ScenarioName = 'SUME_TransportModel' AND origin = " + origin +
				" ORDER BY Mode, Destination;";
		ArrayList<TravelTimeRow> travelTimes = db.select(TravelTimeRow.class, sqlStatement);
		assert travelTimes.size() > 0 : "No records found from '" + sqlStatement + "'";
		travelTimesMIT = new HashMap<Integer,Double>();
		travelTimesPublic = new HashMap<Integer,Double>();
		for (TravelTimeRow t : travelTimes) {
			if (t.mode == TravelMode.PUBLIC_TRANSPORT.getValue()) {
				travelTimesPublic.put(t.destination, t.hours);
			} else if (t.mode == TravelMode.MOTORIZED_INDIVIDUAL_TRANSPORT.getValue()) {
				travelTimesMIT.put(t.destination, t.hours);
			}
		}
	}
	/**
	 * Calculate the travel time to destination & determine the travel mode.
	 * The travel mode can be fetched with getTravelMode()
	 * @param destination
	 * @return Travel time in minutes
	 */
	public long estimateTravelTime(int destination) {
		//TODO: don't know at the moment what to do with commutings out of Vienna
		if ((destination == 3) || (destination == 1)) {
			travelMode = TravelMode.MOTORIZED_INDIVIDUAL_TRANSPORT;
			return 60;
		}
		MersenneTwisterFast r = new MersenneTwisterFast();
		long travelTimeMIT = Math.round(travelTimesMIT.get(destination) * 60);	      // convert to minutes
		long travelTimePublic = Math.round(travelTimesPublic.get(destination) * 60);  // convert to minutes
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
	public long estimateTravelTime(int destination, TravelMode mode) {
		//TODO: don't know at the moment what to do with commutings out of Vienna
		if ((destination == 3) || (destination == 1)) {
			travelMode = mode;
			return 60;
		}
		long travelTime = -1;
		switch (mode) {
		case PUBLIC_TRANSPORT:
			travelTime = Math.round(travelTimesPublic.get(destination) * 60);
			break;
		case MOTORIZED_INDIVIDUAL_TRANSPORT:
			travelTime = Math.round(travelTimesMIT.get(destination) * 60);
			break;
		}
		return travelTime;
	}
	public DbTimeUseRow estimateTravelTime(int personId, int destination) {
		return new DbTimeUseRow(personId, timeUseTag, (int) estimateTravelTime(destination));
	}
	public DbTimeUseRow estimateTravelTime(int personId, int destination, TravelMode mode) {
		return new DbTimeUseRow(personId, timeUseTag, (int) estimateTravelTime(destination, mode));
	}
	/**
	 * Return the travel mode previously determined by estimateTravelTime()
	 * @return
	 */
	public TravelMode getTravelMode() {
		return travelMode;
	}
}
