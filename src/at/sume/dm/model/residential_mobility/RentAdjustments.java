/**
 * 
 */
package at.sume.dm.model.residential_mobility;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.Common;

/**
 * Adjust rents according to various scenarios
 * 
 * @author Alexander Remesch
 */
public class RentAdjustments {
	private ArrayList<RentAdjustmentRow> rentAdjustmentRows;
	
	public RentAdjustments(String scenarioName) throws SQLException, InstantiationException, IllegalAccessException {
		String selectStatement = "select * from _DM_RentAdjustment where rentScenarioName = '" + scenarioName + "' order by spatialUnitId, modelYear";
		rentAdjustmentRows = Common.db.select(RentAdjustmentRow.class, selectStatement);
//		assert rentAdjustmentRows.size() > 0 : "No rows selected from _DM_RentAdjustment";
	}
	/**
	 * Return absolute adjustment factor of the rent price for a given spatial unit and model year.
	 * 
	 * @param spatialUnitId
	 * @param modelYear
	 * @return rent adjustment factor, e.g. 1 = rent stays at 100%
	 */
	public double getRentAdjustmentFactor(int spatialUnitId, short modelYear) {
		RentAdjustmentRow lookup = new RentAdjustmentRow();
		lookup.setSpatialUnitId(spatialUnitId);
		lookup.setModelYear(modelYear);
		int index = Collections.binarySearch(rentAdjustmentRows, lookup);
		if (index >= 0) {
			return 1 + ((double)rentAdjustmentRows.get(index).getRentAdjustment()) / 100;
		} else {
			return 1;
		}
	}
}
