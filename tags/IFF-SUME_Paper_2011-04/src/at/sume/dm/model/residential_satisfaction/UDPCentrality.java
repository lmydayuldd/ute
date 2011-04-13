/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.Common;
import at.sume.dm.entities.SpatialUnitLevel;
import at.sume.dm.entities.SpatialUnitRow;

/**
 * Calculate the component of residential satisfaction based on the UDP indicator dimensions diversity and public transport
 * infrastructure (density has been chosen to be a dynamic model output instead) and household type preferences on these two 
 * dimensions.  
 * 
 * @author Alexander Remesch
 *
 */
public class UDPCentrality extends ResidentialSatisfactionComponent {
	public static class SpatialUnitUdp implements Comparable<SpatialUnitUdp> {
		// must be public static in order to be able to use Database.select()/java reflection api
		public long id;
		public long spatialUnitId;
		public int startYear;
		public int endYear;
		public short centralityIndicator;
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
		public short getCentralityIndicator() {
			return centralityIndicator;
		}

		/**
		 * @param centralityIndicator the centralityIndicator to set
		 */
		public void setCentralityIndicator(short centralityIndicator) {
			this.centralityIndicator = centralityIndicator;
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

	private volatile static UDPCentrality uniqueInstance;
	private ArrayList<SpatialUnitUdp> spatialUnitUdp;
	private static SpatialUnitLevel spatialUnitLevel;
	
	private UDPCentrality(SpatialUnitLevel spatialUnitLevel) {
		String tableName;
		if (spatialUnitLevel.equals(SpatialUnitLevel.SGT))
			tableName = "_DM_SpatialUnitUdp_" + spatialUnitLevel.toString();
		else
			tableName = "_DM_SpatialUnitUdp";
		try {
			spatialUnitUdp = Common.db.select(SpatialUnitUdp.class, 
					"select * from " + tableName + " order by SpatialUnitId, StartYear");
			assert spatialUnitUdp.size() > 0 : "No rows selected from " + tableName;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static UDPCentrality getInstance(SpatialUnitLevel spatialUnitLevel) {
		if (UDPCentrality.spatialUnitLevel == null) {
			UDPCentrality.spatialUnitLevel = spatialUnitLevel;
		} else {
			if (!UDPCentrality.spatialUnitLevel.equals(spatialUnitLevel)) {
				throw new AssertionError("Can't change spatial unit level");
			}
		}
		return getInstance();
	}
	public static UDPCentrality getInstance() {
		if (UDPCentrality.spatialUnitLevel == null) {
			throw new AssertionError("spatialUnitLevel must be set in first call to getInstance()");
		}
		if (uniqueInstance == null) {
			synchronized (UDPCentrality.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new UDPCentrality(spatialUnitLevel);
				}
			}
		}
		return uniqueInstance;
	}
	
	/* (non-Javadoc)
	 * @see at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionComponent#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.SpatialUnitRow)
	 */
	@Override
	public short calc(ResidentialSatisfactionHouseholdProperties household, ResidentialSatisfactionDwellingProperties dwelling, SpatialUnitRow spatialUnit, int modelYear) {
		// Lookup the UDP-indicators for the given spatial unit
		SpatialUnitUdp lookupSpatialUnitUdp = new SpatialUnitUdp();
		lookupSpatialUnitUdp.setSpatialUnitId(spatialUnit.getSpatialUnitId());
		int posUdp = Collections.binarySearch(spatialUnitUdp, lookupSpatialUnitUdp);
		assert posUdp >= 0 : "No UDP data found for spatial unit " + spatialUnit.getSpatialUnitId();
		lookupSpatialUnitUdp = spatialUnitUdp.get(posUdp);
		// Get indicator set for the given model year
		if ((modelYear < lookupSpatialUnitUdp.getStartYear()) || (modelYear > lookupSpatialUnitUdp.getEndYear())) {
			while ((lookupSpatialUnitUdp.getSpatialUnitId() == spatialUnit.getSpatialUnitId()) && (posUdp > 0)) {
				lookupSpatialUnitUdp = spatialUnitUdp.get(--posUdp);
			}
			lookupSpatialUnitUdp = spatialUnitUdp.get(++posUdp);
		}
		while ((modelYear < lookupSpatialUnitUdp.getStartYear()) || (modelYear > lookupSpatialUnitUdp.getEndYear())) {
			lookupSpatialUnitUdp = spatialUnitUdp.get(++posUdp);
			if (lookupSpatialUnitUdp.getSpatialUnitId() != spatialUnit.getSpatialUnitId()) {
				throw new AssertionError("No UDP indicators for spatial unit " + spatialUnit.getSpatialUnitId() + " in model year " + modelYear + " found");
			}
		}

		// Calculate the score
		short result = (short) (lookupSpatialUnitUdp.getCentralityIndicator() * 1000 / 4);
		if (result > 1000)
			result = 1000;
		assert result >= 0 : "rsCentrality out of range (" + result + ")";
		household.setRsUdpCentrality(result);
		return result;
	}
}
