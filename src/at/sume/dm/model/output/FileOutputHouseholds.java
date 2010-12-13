/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import at.sume.db.RecordSet;
import at.sume.dm.entities.HouseholdRow;

/**
 * @author Alexander Remesch
 *
 */
public class FileOutputHouseholds {
	RecordSet<HouseholdRow> rowList;
//	ArrayList<OutputHouseholdRow> outputRowList;
	PrintStream psOut;
	
	public FileOutputHouseholds(String pathName, RecordSet<HouseholdRow> rowList) throws FileNotFoundException {
		this.rowList = rowList;
		FileOutputStream householdsFile = new FileOutputStream(pathName, false);
		psOut = new PrintStream(householdsFile);
	}

	public String toCsvHeadline() {
		return "ModelYear,HouseholdId,HouseholdSize,DwellingId,HouseholdType,MovingDecisionYear," +
			"AspirationRegionLivingSpaceMin,AspirationRegionLivingSpaceMax,CurrentResidentialSatisfaction";
	}

	public void persistDb(short modelYear) {
		OutputHouseholdRow orow;
//		outputRowList = new ArrayList<OutputHouseholdRow>();
		psOut.println(toCsvHeadline());
		for (HouseholdRow row : rowList) {
			orow = new OutputHouseholdRow(modelYear, row);
//			outputRowList.add(orow);
			psOut.println(orow.toCsv());
		}
	}
}
