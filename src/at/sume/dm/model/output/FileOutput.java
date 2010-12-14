/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import at.sume.db.RecordSet;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public abstract class FileOutput<T extends RecordSetRow<?>> {
	PrintStream psOut;
	RecordSet<T> rowList;
	
	/**
	 * 
	 * @param pathName
	 * @param rowList
	 * @throws FileNotFoundException
	 */
	public FileOutput(String pathName, RecordSet<T> rowList) throws FileNotFoundException {
		this.rowList = rowList;
		FileOutputStream householdsFile = new FileOutputStream(pathName, false);
		psOut = new PrintStream(householdsFile);
		psOut.println(toCsvHeadline());
	}
	/**
	 * 
	 * @param modelYear
	 */
	public void persistDb(short modelYear) {
		OutputRow orow;
		for (T row : rowList) {
			orow = createOutputRow(modelYear, row);
			psOut.println(orow.toCsv());
		}
	}
	/**
	 * 
	 * @return
	 */
	public abstract String toCsvHeadline();
	/**
	 * 
	 * @param modelYear
	 * @param row
	 * @return
	 */
	public abstract OutputRow createOutputRow(short modelYear, T row);
}
