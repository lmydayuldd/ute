/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;

import at.sume.db.RecordSet;
import at.sume.dm.entities.PersonRow;

/**
 * @author Alexander Remesch
 *
 */
public class FileOutputPersons extends FileOutput<PersonRow> {
	/**
	 * @param pathName
	 * @param rowList
	 * @throws FileNotFoundException
	 */
	public FileOutputPersons(String pathName, RecordSet<PersonRow> rowList)
			throws FileNotFoundException {
		super(pathName, rowList);
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.FileOutput#toCsvHeadline()
	 */
	@Override
	public String toCsvHeadline() {
		return "ModelYear,PersonId,HouseholdId,Sex,Age,>earlyIncome";
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.FileOutput#createOutputRow(short, at.sume.db.RecordSetRow)
	 */
	@Override
	public OutputRow createOutputRow(short modelYear, PersonRow row) {
		return new OutputPersonRow(modelYear, row);
	}
}
