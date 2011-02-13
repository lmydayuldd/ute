/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.Common;
import at.sume.dm.types.HouseholdType;


/**
 * @author Alexander Remesch
 *
 */
public class ResidentialSatisfactionWeight {
	public static class HouseholdPrefs implements Comparable<HouseholdPrefs> {
		// must be public static in order to be able to use Database.select()/java reflection api
		public long id;
		public long householdTypeId;
		public short prefDiversity;
		public short prefCentrality;
		public short prefEnvAmen;
		public short prefCosts;
		public short prefTransportAccess;
		public short prefLivingSpace;
		
		/**
		 * @return the id
		 */
		public long getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(long id) {
			this.id = id;
		}

		/**
		 * @return the householdTypeId
		 */
		public long getHouseholdTypeId() {
			return householdTypeId;
		}

		/**
		 * @param householdTypeId the householdTypeId to set
		 */
		public void setHouseholdTypeId(long householdTypeId) {
			this.householdTypeId = householdTypeId;
		}

		/**
		 * @return the prefDensity
		 */
		public short getPrefCentrality() {
			return prefCentrality;
		}

		/**
		 * @param prefCentrality the prefCentrality to set
		 */
		public void setPrefCentrality(short prefCentrality) {
			this.prefCentrality = prefCentrality;
		}

		/**
		 * @return the prefDiversity
		 */
		public short getPrefDiversity() {
			return prefDiversity;
		}

		/**
		 * @param prefDiversity the prefDiversity to set
		 */
		public void setPrefDiversity(short prefDiversity) {
			this.prefDiversity = prefDiversity;
		}

		/**
		 * @return the prefEnvAmen
		 */
		public short getPrefEnvAmen() {
			return prefEnvAmen;
		}

		/**
		 * @param prefEnvAmen the prefEnvAmen to set
		 */
		public void setPrefEnvAmen(short prefEnvAmen) {
			this.prefEnvAmen = prefEnvAmen;
		}

		/**
		 * @return the prefCosts
		 */
		public short getPrefCosts() {
			return prefCosts;
		}

		/**
		 * @param prefCosts the prefCosts to set
		 */
		public void setPrefCosts(short prefCosts) {
			this.prefCosts = prefCosts;
		}

		/**
		 * @return the prefTransportAccess
		 */
		public short getPrefTransportAccess() {
			return prefTransportAccess;
		}

		/**
		 * @param prefTransportAccess the prefTransportAccess to set
		 */
		public void setPrefTransportAccess(short prefTransportAccess) {
			this.prefTransportAccess = prefTransportAccess;
		}

		/**
		 * @return the prefLivingSpace
		 */
		public short getPrefLivingSpace() {
			return prefLivingSpace;
		}

		/**
		 * @param prefLivingSpace the prefLivingSpace to set
		 */
		public void setPrefLivingSpace(short prefLivingSpace) {
			this.prefLivingSpace = prefLivingSpace;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(HouseholdPrefs o) {
			if (householdTypeId == o.householdTypeId)
				return 0;
			else if (householdTypeId > o.householdTypeId) 
				return 1;
			else
				return -1;
		}
	}

	private static ArrayList<HouseholdPrefs> householdPrefs;

	static {
		try {
			// TODO: finish scenario handling
			householdPrefs = Common.db.select(HouseholdPrefs.class, 
					"select * from _DM_HouseholdPrefs where HouseholdPrefsScenarioName = 'BASE' order by HouseholdTypeId");
			assert householdPrefs.size() > 0 : "No rows selected from _DM_HouseholdPrefs";
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static HouseholdPrefs lookupHouseholdTypePrefs(HouseholdType householdType) {
		HouseholdPrefs lookupHouseholdPrefs = new HouseholdPrefs();
		lookupHouseholdPrefs.setHouseholdTypeId(householdType.getId());
		int posHHType = Collections.binarySearch(householdPrefs, lookupHouseholdPrefs);		
		assert posHHType >= 0 : "No household preference data found for hh type " + householdType.toString();
		return householdPrefs.get(posHHType);
	}
	
	public static ArrayList<Short> getPrefCosts() {
		ArrayList<Short> result = new ArrayList<Short>(householdPrefs.size());
		for (HouseholdPrefs prefs : householdPrefs) {
			result.add(prefs.getPrefCosts());
		}
		return result;
	}

	public static ArrayList<Short> getPrefDiversity() {
		ArrayList<Short> result = new ArrayList<Short>(householdPrefs.size());
		for (HouseholdPrefs prefs : householdPrefs) {
			result.add(prefs.getPrefDiversity());
		}
		return result;
	}
	
	public static ArrayList<Short> getPrefCentrality() {
		ArrayList<Short> result = new ArrayList<Short>(householdPrefs.size());
		for (HouseholdPrefs prefs : householdPrefs) {
			result.add(prefs.getPrefCentrality());
		}
		return result;
	}
	
	public static ArrayList<Short> getPrefEnvAmen() {
		ArrayList<Short> result = new ArrayList<Short>(householdPrefs.size());
		for (HouseholdPrefs prefs : householdPrefs) {
			result.add(prefs.getPrefEnvAmen());
		}
		return result;
	}

	public static ArrayList<Short> getPrefLivingSpace() {
		ArrayList<Short> result = new ArrayList<Short>(householdPrefs.size());
		for (HouseholdPrefs prefs : householdPrefs) {
			result.add(prefs.getPrefLivingSpace());
		}
		return result;
	}

	public static ArrayList<Short> getPrefTransportAccess() {
		ArrayList<Short> result = new ArrayList<Short>(householdPrefs.size());
		for (HouseholdPrefs prefs : householdPrefs) {
			result.add(prefs.getPrefTransportAccess());
		}
		return result;
	}
}
