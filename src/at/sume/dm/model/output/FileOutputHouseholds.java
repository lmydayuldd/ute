/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;

import at.sume.db.RecordSet;
import at.sume.dm.entities.HouseholdRow;

/**
 * @author Alexander Remesch
 *
 */
public class FileOutputHouseholds extends FileOutput<HouseholdRow> {
	/**
	 * @param pathName
	 * @param rowList
	 * @throws FileNotFoundException
	 */
	public FileOutputHouseholds(String pathName, RecordSet<HouseholdRow> rowList)
			throws FileNotFoundException {
		super(pathName, rowList);
	}

	public String toCsvHeadline() {
		return "ModelYear;HouseholdId;HouseholdSize;DwellingId;HouseholdType;MovingDecisionYear;" +
			"AspirationRegionLivingSpaceMin;AspirationRegionLivingSpaceMax;AspirationRegionMaxCosts;CurrentResidentialSatisfaction";
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.FileOutput#createOutputRow(short, at.sume.db.RecordSetRow)
	 */
	@Override
	public OutputRow createOutputRow(short modelYear, HouseholdRow row) {
		return new OutputHouseholdRow(modelYear, row);
	}

}
