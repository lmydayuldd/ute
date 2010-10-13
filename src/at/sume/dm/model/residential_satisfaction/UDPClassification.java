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
import at.sume.dm.model.residential_satisfaction.entities.HouseholdPrefs;
import at.sume.dm.model.residential_satisfaction.entities.HouseholdPrefsComparatorHouseholdType;
import at.sume.dm.model.residential_satisfaction.entities.SpatialUnitUdp;
import at.sume.dm.model.residential_satisfaction.entities.SpatialUnitUdpComparatorSpatialUnitId;
import at.sume.dm.types.HouseholdType;

/**
 * @author Alexander Remesch
 *
 */
public class UDPClassification extends ResidentialSatisfactionComponent {
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
	public double calc(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		// Lookup the UDP-indicators for the given spatial unit
		SpatialUnitUdp lookupSpatialUnitUdp = new SpatialUnitUdp();
		lookupSpatialUnitUdp.spatialUnitId = su.getSpatialUnitId();
		int posUdp = Collections.binarySearch(spatialUnitUdp, lookupSpatialUnitUdp, new SpatialUnitUdpComparatorSpatialUnitId());
		lookupSpatialUnitUdp = spatialUnitUdp.get(posUdp);
		// Get indicator set for the given model year
		while (lookupSpatialUnitUdp.spatialUnitId == su.getSpatialUnitId()) {
			lookupSpatialUnitUdp = spatialUnitUdp.get(--posUdp);
		}
		lookupSpatialUnitUdp = spatialUnitUdp.get(++posUdp);
		while ((modelYear < lookupSpatialUnitUdp.startYear) || (modelYear > lookupSpatialUnitUdp.endYear)) {
			lookupSpatialUnitUdp = spatialUnitUdp.get(++posUdp);
			if (lookupSpatialUnitUdp.spatialUnitId != su.getSpatialUnitId()) {
				throw new AssertionError("No UDP indicators for spatial unit " + su.getSpatialUnitId() + " in model year " + modelYear + " found");
			}
		}

		// Lookup the household preferences for the given household type
		HouseholdPrefs lookupHouseholdPrefs = new HouseholdPrefs();
		lookupHouseholdPrefs.householdTypeId = HouseholdType.getId(hh.getHouseholdType());
		int posHHType = Collections.binarySearch(householdPrefs, lookupHouseholdPrefs, new HouseholdPrefsComparatorHouseholdType());
		lookupHouseholdPrefs = householdPrefs.get(posHHType);

		// Calculate the score
		int hhScore = lookupHouseholdPrefs.prefDiversity * lookupSpatialUnitUdp.diversityIndicator + lookupHouseholdPrefs.prefTransportAccess * lookupSpatialUnitUdp.publicTransportIndicator;
		int hhScoreMax = lookupHouseholdPrefs.prefDiversity * 4 + lookupHouseholdPrefs.prefTransportAccess * 4;
		return (double) hhScore / (double) hhScoreMax;
	}
}
