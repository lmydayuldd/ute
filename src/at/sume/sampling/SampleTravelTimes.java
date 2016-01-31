package at.sume.sampling;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import at.sume.sampling.distributions.TravelTimeRow;
import ec.util.MersenneTwisterFast;
import net.remesch.db.Database;

public class SampleTravelTimes {
	private Database db;
	private HashMap<Integer,Double> travelTimesMIT;
	private HashMap<Integer,Double> travelTimesPublic;
	private int travelMode;
	
	public SampleTravelTimes(Database db) {
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
			switch (t.mode) {
			case 1: // public
				travelTimesPublic.put(t.destination, t.hours);
				break;
			case 2: // MIT
				travelTimesMIT.put(t.destination, t.hours);
				break;
			}
		}
	}
	/**
	 * Calculate the travel time to destination & determine the travel mode.
	 * The travel mode can be fetched with getTravelMode()
	 * @param destination
	 * @return
	 */
	public long estimateTravelTime(int destination) {
		//TODO: don't know at the moment what to do with commutings out of Vienna
		if ((destination == 3) || (destination == 1)) {
			travelMode = 2;
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
			travelMode = 2;
			return travelTimeMIT;
		} else {
			travelMode = 1;
			return travelTimePublic;
		}
	}
	/**
	 * Calculate the travel time to destination & determine the travel mode.
	 * The travel mode can be fetched with getTravelMode()
	 * @param destination
	 * @param mode
	 * @return
	 */
	public long estimateTravelTime(int destination, int mode) {
		long travelTime = -1;
		switch (mode) {
		case 1:
			travelTime = Math.round(travelTimesPublic.get(destination) * 60);
			break;
		case 2:
			travelTime = Math.round(travelTimesMIT.get(destination) * 60);
			break;
		}
		return travelTime;
	}
	/**
	 * Return the travel mode previously determined by estimateTravelTime()
	 * @return
	 */
	public int getTravelMode() {
		return travelMode;
	}
}
