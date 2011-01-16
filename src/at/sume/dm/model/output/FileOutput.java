/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	String path = "";
	
	/**
	 * 
	 * @param path
	 * @param rowList
	 * @throws FileNotFoundException
	 */
	public FileOutput(String path, RecordSet<T> rowList) {
		this.rowList = rowList;
		if (path != null)
			if (path.endsWith("\\"))
				this.path = path;
			else
				this.path = path + "\\";
	}
	/**
	 * 
	 * @param modelYear
	 * @throws IOException 
	 */
	public void persistDb(short modelYear) throws IOException {
		String pathName = path + rowList.get(0).getClass().getName() + "_" + modelYear + ".csv";
		OutputRow orow;
		FileOutputStream fileOutputStream = new FileOutputStream(pathName, false);
		psOut = new PrintStream(fileOutputStream);
		psOut.println(toCsvHeadline());
		for (T row : rowList) {
			orow = createOutputRow(modelYear, row);
			psOut.println(orow.toCsv());
		}
		psOut.close();
		fileOutputStream.close();
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
