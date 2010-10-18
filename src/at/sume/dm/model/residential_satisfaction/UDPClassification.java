/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.types.HouseholdType;

/**
 * Calculate the component of residential satisfaction based on the UDP indicator dimensions diversity and public transport
 * infrastructure (density has been chosen to be a dynamic model output instead) and household type preferences on these two 
 * dimensions.  
 * 
 * @author Alexander Remesch
 *
 */
public class UDPClassification extends ResidentialSatisfactionComponent {
	public static class HouseholdPrefs implements Comparable<HouseholdPrefs> {
		// must be public static in order to be able to use Database.select()/java reflection api
		public long id;
		public long scenarioId;
		public long householdTypeId;
		public short prefDensity;
		public short prefDiversity;
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
		 * @return the scenarioId
		 */
		public long getScenarioId() {
			return scenarioId;
		}

		/**
		 * @param scenarioId the scenarioId to set
		 */
		public void setScenarioId(long scenarioId) {
			this.scenarioId = scenarioId;
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
		public short getPrefDensity() {
			return prefDensity;
		}

		/**
		 * @param prefDensity the prefDensity to set
		 */
		public void setPrefDensity(short prefDensity) {
			this.prefDensity = prefDensity;
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
	public static class SpatialUnitUdp implements Comparable<SpatialUnitUdp> {
		// must be public static in order to be able to use Database.select()/java reflection api
		public long id;
		public long spatialUnitId;
		public int startYear;
		public int endYear;
		public short diversityIndicator;
		public short publicTransportIndicator;
		
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
		 * @return the spatialUnitId
		 */
		public long getSpatialUnitId() {
			return spatialUnitId;
		}

		/**
		 * @param spatialUnitId the spatialUnitId to set
		 */
		public void setSpatialUnitId(long spatialUnitId) {
			this.spatialUnitId = spatialUnitId;
		}

		/**
		 * @return the startYear
		 */
		public int getStartYear() {
			return startYear;
		}

		/**
		 * @param startYear the startYear to set
		 */
		public void setStartYear(int startYear) {
			this.startYear = startYear;
		}

		/**
		 * @return the endYear
		 */
		public int getEndYear() {
			return endYear;
		}

		/**
		 * @param endYear the endYear to set
		 */
		public void setEndYear(int endYear) {
			this.endYear = endYear;
		}

		/**
		 * @return the diversityIndicator
		 */
		public short getDiversityIndicator() {
			return diversityIndicator;
		}

		/**
		 * @param diversityIndicator the diversityIndicator to set
		 */
		public void setDiversityIndicator(short diversityIndicator) {
			this.diversityIndicator = diversityIndicator;
		}

		/**
		 * @return the publicTransportIndicator
		 */
		public short getPublicTransportIndicator() {
			return publicTransportIndicator;
		}

		/**
		 * @param publicTransportIndicator the publicTransportIndicator to set
		 */
		public void setPublicTransportIndicator(short publicTransportIndicator) {
			this.publicTransportIndicator = publicTransportIndicator;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(SpatialUnitUdp o) {
			if (spatialUnitId == o.spatialUnitId)
				return 0;
			else if (spatialUnitId > o.spatialUnitId) 
				return 1;
			else
				return -1;
		}
	}

	private ArrayList<SpatialUnitUdp> spatialUnitUdp;
	private ArrayList<HouseholdPrefs> householdPrefs;
	
	public UDPClassification() {
		try {
			spatialUnitUdp = Common.db.select(SpatialUnitUdp.class, 
					"select * from _DM_SpatialUnitUdp order by SpatialUnitId, StartYear");
			assert spatialUnitUdp.size() > 0 : "No rows selected from _DM_SpatialUnitUdp";
			householdPrefs = Common.db.select(HouseholdPrefs.class, 
					"select * from _DM_HouseholdPrefs where ScenarioId = " + Common.scenarioId + " order by HouseholdTypeId");
			assert householdPrefs.size() > 0 : "No rows selected from _DM_HouseholdPrefs";
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionComponent#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.SpatialUnitRow)
	 */
	@Override
	public long calc(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		// Lookup the UDP-indicators for the given spatial unit
		SpatialUnitUdp lookupSpatialUnitUdp = new SpatialUnitUdp();
		lookupSpatialUnitUdp.setSpatialUnitId(su.getSpatialUnitId());
//		int posUdp = Collections.binarySearch(spatialUnitUdp, lookupSpatialUnitUdp, new SpatialUnitUdpComparatorSpatialUnitId());
		int posUdp = Collections.binarySearch(spatialUnitUdp, lookupSpatialUnitUdp);
		assert posUdp >= 0 : "No UDP data found for spatial unit " + su.getSpatialUnitId();
		lookupSpatialUnitUdp = spatialUnitUdp.get(posUdp);
		// Get indicator set for the given model year
		while ((lookupSpatialUnitUdp.getSpatialUnitId() == su.getSpatialUnitId()) && (posUdp > 0)) {
			lookupSpatialUnitUdp = spatialUnitUdp.get(--posUdp);
		}
		lookupSpatialUnitUdp = spatialUnitUdp.get(++posUdp);
		while ((modelYear < lookupSpatialUnitUdp.getStartYear()) || (modelYear > lookupSpatialUnitUdp.getEndYear())) {
			lookupSpatialUnitUdp = spatialUnitUdp.get(++posUdp);
			if (lookupSpatialUnitUdp.getSpatialUnitId() != su.getSpatialUnitId()) {
				throw new AssertionError("No UDP indicators for spatial unit " + su.getSpatialUnitId() + " in model year " + modelYear + " found");
			}
		}

		// Lookup the household preferences for the given household type
		HouseholdPrefs lookupHouseholdPrefs = new HouseholdPrefs();
		lookupHouseholdPrefs.setHouseholdTypeId(HouseholdType.getId(hh.getHouseholdType()));
//		int posHHType = Collections.binarySearch(householdPrefs, lookupHouseholdPrefs, new HouseholdPrefsComparatorHouseholdType());
		int posHHType = Collections.binarySearch(householdPrefs, lookupHouseholdPrefs);		
		assert posHHType >= 0 : "No household preference data found for hh " + hh.getId() + ", hh type " + hh.getHouseholdType();
		lookupHouseholdPrefs = householdPrefs.get(posHHType);

		// Calculate the score
		int hhScore = lookupHouseholdPrefs.getPrefDiversity() * lookupSpatialUnitUdp.getDiversityIndicator() + lookupHouseholdPrefs.getPrefTransportAccess() * lookupSpatialUnitUdp.getPublicTransportIndicator();
		int hhScoreMax = lookupHouseholdPrefs.getPrefDiversity() * 4 + lookupHouseholdPrefs.getPrefTransportAccess() * 4;
		return (hhScore * 1000) / hhScoreMax;
	}
}
