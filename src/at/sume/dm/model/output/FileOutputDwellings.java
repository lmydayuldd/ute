/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;

import at.sume.db.RecordSet;
import at.sume.dm.entities.DwellingRow;

/**
 * @author Alexander Remesch
 *
 */
public class FileOutputDwellings extends FileOutput<DwellingRow> {
	/**
	 * @param pathName
	 * @param rowList
	 * @throws FileNotFoundException
	 */
	public FileOutputDwellings(String pathName, RecordSet<DwellingRow> rowList) {
		super(pathName, rowList);
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.FileOutput#toCsvHeadline()
	 */
	@Override
	public String toCsvHeadline() {
		return "ModelYear;DwellingId;SpatialunitId;DwellingSize;TotalYearlyDwellingCosts;HouseholdId";
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.FileOutput#createOutputRow(short, at.sume.db.RecordSetRow)
	 */
	@Override
	public OutputRow createOutputRow(short modelYear, DwellingRow row) {
		return new OutputDwellingRow(modelYear, row);
	}
}
