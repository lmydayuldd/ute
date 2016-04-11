/**
 * 
 */
package at.sume.dm.model.travel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.sume.dm.Common;
import at.sume.dm.model.timeuse.Activity;
import at.sume.dm.scenario_handling.Scenario;
import net.remesch.db.Database;

/**
 * Implementation of the location-based travel time modifiers
 * 
 * @author Alexander Remesch
 */
public class TravelTimeModifiers {
	private class ModifierRecord {
		@SuppressWarnings("unused")
		public int spatialUnitId;
		public String activityNoMode;
		public double modifier;
		public short year;
	}
	private HashMap<Integer,HashMap<Activity,List<ModifierRecord>>> timeUseModifiers;

	public TravelTimeModifiers(Database db, Scenario scenario, List<Integer> cells) throws InstantiationException, IllegalAccessException, SQLException {
		timeUseModifiers = new HashMap<Integer,HashMap<Activity,List<ModifierRecord>>>();
		// Load _UTE_TravelTimeModifiers & build nested HashSets
		for (Integer s : cells) {
			if (s == 1 || s == 3)
				continue;
			HashMap<Activity,List<ModifierRecord>> timeUseModifiersCell = new HashMap<Activity,List<ModifierRecord>>();
			String sqlStatement = "select SpatialUnitId, ActivityNoMode, Modifier, Year " +
					"from _UTE_TravelTimeModifiers where ScenarioName='" + scenario.getTravelTimeModifierScenario() + "' " +
					"order by spatialunitid, activitynomodel, year";
			ArrayList<ModifierRecord> l = db.select(ModifierRecord.class, sqlStatement);
			assert l.size() > 0 : "No records found from '" + sqlStatement + "' for cell " + s + " - maybe wrong SpatialUnitLevel?";
			Activity dest = Activity.NONE;
			List<ModifierRecord> timeUseModifiersCellActivity = new ArrayList<ModifierRecord>();
			for (ModifierRecord t : l) {
				if (dest != Activity.find(t.activityNoMode)) {
					if (dest != Activity.NONE) {
						timeUseModifiersCell.put(dest, timeUseModifiersCellActivity);
					}
					timeUseModifiersCellActivity = new ArrayList<ModifierRecord>();
				}
				timeUseModifiersCellActivity.add(t);
				dest = Activity.find(t.activityNoMode);
			}
		}
	}
	private ModifierRecord getTravelInfoYear(List<ModifierRecord> tl, short modelYear) {
		ModifierRecord result = null;
		if (tl.size() == 1) {
			return tl.get(0);
		} else {
			for (ModifierRecord t : tl) {
				if (modelYear >= t.year) {
					if (result == null)
						result = t;
					else if (t.year > result.year)
						result = t;
				}
			}
		}
		return result;
	}
	/**
	 * Get the travel time modifier for a given cell and activity in a certain year
	 * @param cellId
	 * @param activity
	 * @param modelYear
	 * @return
	 */
	public double getTravelTimeModifier(int cellId, Activity activity, short modelYear) {
		if ((cellId == 3) || (cellId == 1)) {
			return 1;
		}
		ModifierRecord ti = getTravelInfoYear(timeUseModifiers.get(cellId).get(activity), Common.getModelYear());
		return ti.modifier;
	}
}
