/**
 * 
 */
package at.sume.dm.indicators.simple;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.indicators.rows.DemographicMovementsRow;

/**
 * @author Alexander Remesch
 *
 */
public class CountDemographicMovements implements DemographyObserver {
	private ArrayList<DemographicMovementsRow> indicatorList = new ArrayList<DemographicMovementsRow>();
	private boolean headLineWritten = false;
	/**
	 * @return the indicatorList
	 */
	public ArrayList<DemographicMovementsRow> getIndicatorList() {
		return indicatorList;
	}
	/**
	 * Reset all migration counts
	 */
	public void clearIndicatorList() {
		indicatorList.clear();
	}
	/**
	 * 
	 * @param ps
	 * @param modelYear
	 */
	public void output(PrintStream ps, int modelYear) {
		assert indicatorList.size() > 0 : "DemographicMovementCount is empty!";
		StringBuffer output = new StringBuffer();
		// Headline - written only once per model run
		if (!headLineWritten) {
			output.append("ModelYear;");
			output.append(indicatorList.get(0).toCsvHeadline(";"));
			ps.println(output);
			headLineWritten = true;
		}
		for (DemographicMovementsRow row : indicatorList) {
			output = new StringBuffer(modelYear + ";" + row.toString(";"));
			ps.println(output);
		}
	}
	@Override
	public void addBirth(Integer spatialUnitId) {
		int pos = Collections.binarySearch(indicatorList, spatialUnitId);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			DemographicMovementsRow demographicMovementsRow = new DemographicMovementsRow();
			demographicMovementsRow.setSpatialUnitId(spatialUnitId);
			demographicMovementsRow.addBirthCount();
			indicatorList.add(pos, demographicMovementsRow);
		} else {
			// available at position pos
			DemographicMovementsRow demographicMovementsRow = indicatorList.get(pos);
			demographicMovementsRow.addBirthCount();
			indicatorList.set(pos, demographicMovementsRow);
		}
	}
	@Override
	public void addDeath(Integer spatialUnitId) {
		int pos = Collections.binarySearch(indicatorList, spatialUnitId);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			DemographicMovementsRow demographicMovementsRow = new DemographicMovementsRow();
			demographicMovementsRow.setSpatialUnitId(spatialUnitId);
			demographicMovementsRow.addDeathCount();
			indicatorList.add(pos, demographicMovementsRow);
		} else {
			// available at position pos
			DemographicMovementsRow demographicMovementsRow = indicatorList.get(pos);
			demographicMovementsRow.addDeathCount();
			indicatorList.set(pos, demographicMovementsRow);
		}
	}

}
