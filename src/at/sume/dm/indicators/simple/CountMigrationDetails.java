/**
 * 
 */
package at.sume.dm.indicators.simple;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.entities.PersonRow;
import at.sume.dm.indicators.rows.MigrationDetailsRow;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public class CountMigrationDetails implements MigrationDetailsObserver {
	private ArrayList<MigrationDetailsRow> migrationDetailsList = new ArrayList<MigrationDetailsRow>();
	private boolean headLineWritten = false;

	public void clear() {
		migrationDetailsList.clear();
	}
	/**
	 * 
	 * @param ps
	 * @param modelYear
	 */
	public void output(PrintStream ps, int modelYear, int modelRun) {
		assert migrationDetailsList.size() > 0 : "MigrationCount is empty!";
		StringBuffer output = new StringBuffer();
		// Headline - written only once per model run
		if (!headLineWritten && modelRun == 0) {
			output.append("ModelYear;");
			output.append(migrationDetailsList.get(0).toCsvHeadline(";"));
			ps.println(output);
			headLineWritten = true;
		}
		for (MigrationDetailsRow row : migrationDetailsList) {
			output = new StringBuffer(modelYear + ";" + row.toString(modelRun, ";"));
			ps.println(output);
		}
	}

	private int lookupMigrationDetailsRow(Integer spatialUnitIdFrom, Integer spatialUnitIdTo,
			MigrationRealm migrationRealm,
			HouseholdType householdType) {
		MigrationDetailsRow lookupRow = new MigrationDetailsRow();
		lookupRow.setSpatialUnitIdFrom(spatialUnitIdFrom);
		lookupRow.setSpatialUnitIdTo(spatialUnitIdTo);
		lookupRow.setMigrationRealm(migrationRealm);
		lookupRow.setHouseholdType(householdType);
		return Collections.binarySearch(migrationDetailsList, lookupRow);
	}
	
	@Override
	public void addMigration(Integer spatialUnitIdFrom, Integer spatialUnitIdTo,
			MigrationRealm migrationRealm,
			HouseholdCharacteristics householdCharacteristics) {
		int index = lookupMigrationDetailsRow(spatialUnitIdFrom, spatialUnitIdTo, migrationRealm, householdCharacteristics.getHouseholdType());
		if (index < 0) {
			// insert at position index
			index = (index + 1) * -1;
			MigrationDetailsRow insertRow = new MigrationDetailsRow();
			insertRow.setSpatialUnitIdFrom(spatialUnitIdFrom);
			insertRow.setSpatialUnitIdTo(spatialUnitIdTo);
			insertRow.setMigrationRealm(migrationRealm);
			insertRow.setHouseholdType(householdCharacteristics.getHouseholdType());
			insertRow.setHouseholdCount(1);
			insertRow.setPersonCount(householdCharacteristics.getHouseholdSize());
			migrationDetailsList.add(index, insertRow);
		} else {
			// available at position index
			MigrationDetailsRow updateRow = migrationDetailsList.get(index);
			updateRow.setHouseholdCount(updateRow.getHouseholdCount() + 1);
			updateRow.setPersonCount(updateRow.getPersonCount() + householdCharacteristics.getHouseholdSize());
			migrationDetailsList.set(index, updateRow);
		}
	}
	@Override
	public void addSingleMigration(Integer spatialUnitIdFrom, Integer spatialUnitIdTo, MigrationRealm migrationRealm,
			PersonRow person) {
		int index = lookupMigrationDetailsRow(spatialUnitIdFrom, spatialUnitIdTo, migrationRealm, person.getHousehold().getHouseholdType());
		if (index < 0) {
			// insert at position index
			index = (index + 1) * -1;
			MigrationDetailsRow insertRow = new MigrationDetailsRow();
			insertRow.setSpatialUnitIdFrom(spatialUnitIdFrom);
			insertRow.setSpatialUnitIdTo(spatialUnitIdTo);
			insertRow.setMigrationRealm(migrationRealm);
			insertRow.setHouseholdType(person.getHousehold().getHouseholdType());
			insertRow.setHouseholdCount(1);
			insertRow.setPersonCount(1);
			migrationDetailsList.add(index, insertRow);
		} else {
			// available at position index
			MigrationDetailsRow updateRow = migrationDetailsList.get(index);
			updateRow.setHouseholdCount(updateRow.getHouseholdCount() + 1);
			updateRow.setPersonCount(updateRow.getPersonCount() + 1);
			migrationDetailsList.set(index, updateRow);
		}
	}
}
