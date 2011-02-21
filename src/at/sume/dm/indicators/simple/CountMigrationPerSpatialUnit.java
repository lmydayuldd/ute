/**
 * 
 */
package at.sume.dm.indicators.simple;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.indicators.rows.MigrationPerSpatialUnitRow;
import at.sume.dm.types.MigrationRealm;

/**
 * The number of moves (to/from any model spatial unit) per household/person is counted here on a local (within
 * the model area), national and international level
 *  
 * @author Alexander Remesch
 */
public class CountMigrationPerSpatialUnit implements MigrationObserver {
	private ArrayList<MigrationPerSpatialUnitRow> indicatorList = new ArrayList<MigrationPerSpatialUnitRow>();
	private boolean headLineWritten = false;
	/**
	 * @return the indicatorList
	 */
	public ArrayList<MigrationPerSpatialUnitRow> getIndicatorList() {
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
		assert indicatorList.size() > 0 : "MigrationCount is empty!";
		StringBuffer output = new StringBuffer();
		// Headline - written only once per model run
		if (!headLineWritten) {
			output.append("ModelYear;");
			output.append(indicatorList.get(0).toCsvHeadline(";"));
			ps.println(output);
			headLineWritten = true;
		}
		for (MigrationPerSpatialUnitRow row : indicatorList) {
			output = new StringBuffer(modelYear + ";" + row.toString(";"));
			ps.println(output);
		}
	}
	/**
	 * Count one household that is migrating within the model area
	 * 
	 * @param srcSpatialUnitId Spatial unit the household emigrates from
	 * @param destSpatialUnitId Spatial unit the household immigrates to
	 * @param householdMemberCount Number of household members
	 */
	@Override
	public void addLocalMigration(Integer srcSpatialUnitId, Integer destSpatialUnitId, byte householdMemberCount) {
		// Count the emigration of the household
		addEmigration(srcSpatialUnitId, householdMemberCount, MigrationRealm.LOCAL);
		// Count the immigration of the household
		addImmigration(destSpatialUnitId, householdMemberCount, MigrationRealm.LOCAL);
	}
	/**
	 * Count one household that is emigrating from the model area
	 * 
	 * @param srcSpatialUnitId Spatial unit the household emigrates from
	 * @param householdMemberCount Number of household members
	 * @param migrationRealm Specifies whether the household moves within the model area, to another place within the same country or to another country
	 */
	@Override
	public void addEmigration(Integer srcSpatialUnitId, byte householdMemberCount, MigrationRealm migrationRealm) {
		int pos = Collections.binarySearch(indicatorList, srcSpatialUnitId);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			MigrationPerSpatialUnitRow emigrationRow = new MigrationPerSpatialUnitRow();
			emigrationRow.setSpatialUnidId(srcSpatialUnitId);
			emigrationRow.addEmigratingHousehold(householdMemberCount, migrationRealm);
			indicatorList.add(pos, emigrationRow);
		} else {
			// available at position pos
			MigrationPerSpatialUnitRow emigrationRow = indicatorList.get(pos);
			emigrationRow.addEmigratingHousehold(householdMemberCount, migrationRealm);
			indicatorList.set(pos, emigrationRow);
		}
	}
	/**
	 * Count one household that is immigrating to the model area
	 * 
	 * @param destSpatialUnitId Spatial unit the household is immigrating to
	 * @param householdMemberCount Number of household members
	 * @param migrationRealm Specifies whether the household moves within the model area, to another place within the same country or to another country
	 */
	@Override
	public void addImmigration(Integer destSpatialUnitId, byte householdMemberCount, MigrationRealm migrationRealm) {
		int pos = Collections.binarySearch(indicatorList, destSpatialUnitId);
		if (pos < 0) {
			// insert at position pos
			pos = (pos + 1) * -1;
			MigrationPerSpatialUnitRow immigrationRow = new MigrationPerSpatialUnitRow();
			immigrationRow.setSpatialUnidId(destSpatialUnitId);
			immigrationRow.addImmigratingHousehold(householdMemberCount, migrationRealm);
			indicatorList.add(pos, immigrationRow);
		} else {
			// available at position pos
			MigrationPerSpatialUnitRow immigrationRow = indicatorList.get(pos);
			immigrationRow.addImmigratingHousehold(householdMemberCount, migrationRealm);
			indicatorList.set(pos, immigrationRow);
		}
	}
}
