/**
 * 
 */
package at.sume.dm.indicators.simple;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.entities.PersonRow;
import at.sume.dm.indicators.rows.MigrationAgeSexRow;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public class CountMigrationAgeSex implements MigrationDetailsObserver {
	private ArrayList<MigrationAgeSexRow> migrationDetailsList = new ArrayList<MigrationAgeSexRow>();
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
		for (MigrationAgeSexRow row : migrationDetailsList) {
			output = new StringBuffer(modelYear + ";" + row.toString(modelRun, ";"));
			ps.println(output);
		}
	}

	private int lookupMigrationDetailsRow(Byte sex, Byte ageGroupId,
			MigrationRealm migrationRealm,
			HouseholdType householdType) {
		MigrationAgeSexRow lookupRow = new MigrationAgeSexRow();
		lookupRow.setSex(sex);
		lookupRow.setAgeGroupId(ageGroupId);
		lookupRow.setMigrationRealm(migrationRealm);
		lookupRow.setHouseholdType(householdType);
		return Collections.binarySearch(migrationDetailsList, lookupRow);
	}
	
	@Override
	public void addMigration(Integer spatialUnitIdFrom, Integer spatialUnitIdTo,
			MigrationRealm migrationRealm,
			HouseholdCharacteristics householdCharacteristics) {
		for (PersonRow p : householdCharacteristics.getMembers()) {
			int index = lookupMigrationDetailsRow(p.getSex(), p.getAgeGroupId(), migrationRealm, householdCharacteristics.getHouseholdType());
			if (index < 0) {
				// insert at position index
				index = (index + 1) * -1;
				MigrationAgeSexRow insertRow = new MigrationAgeSexRow();
				insertRow.setSex(p.getSex());
				insertRow.setAgeGroupId(p.getAgeGroupId());
				insertRow.setMigrationRealm(migrationRealm);
				insertRow.setHouseholdType(householdCharacteristics.getHouseholdType());
				insertRow.setPersonCount(1);
				migrationDetailsList.add(index, insertRow);
			} else {
				// available at position index
				MigrationAgeSexRow updateRow = migrationDetailsList.get(index);
				updateRow.setPersonCount(updateRow.getPersonCount() + 1);
				migrationDetailsList.set(index, updateRow);
			}
		}
	}
}
