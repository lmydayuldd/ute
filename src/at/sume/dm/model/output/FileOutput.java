/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.remesch.util.FileUtil;

/**
 * @author Alexander Remesch
 *
 */
public class FileOutput {
	private PrintStream psOut;
	private ArrayList<Fileable> rowList;
	private final static String delimiter = ";";
	private boolean headLineWritten = false;
	
	/**
	 * 
	 * @param path
	 * @param rowList
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public FileOutput(String path, String fileName, List<? extends Fileable> rowList, boolean createNewOutputFile) throws FileNotFoundException {
		String pathName;
		this.rowList = (ArrayList<Fileable>) rowList;
		if (path.endsWith("\\"))
			pathName = path + fileName + ".csv";
		else
			pathName = path + "\\" + fileName + ".csv";
		// Rename existing file to a unique filename
		if (createNewOutputFile)
			FileUtil.rotateFile(pathName);
		FileOutputStream fileOutputStream = new FileOutputStream(pathName, true);
		psOut = new PrintStream(fileOutputStream);
	}
	/**
	 * 
	 * @param modelYear
	 * @throws IOException 
	 */
	public void persistDb(int modelRun, short modelYear) {
		if (!headLineWritten) {
			psOut.println("ModelYear" + delimiter + rowList.get(0).toCsvHeadline(delimiter));
			headLineWritten = true;
		}
		for (Fileable row : rowList) {
			String orow = modelYear + delimiter + row.toString(modelRun, delimiter);
			psOut.println(orow);
		}
	}

	public void close() {
		psOut.close();
		psOut = null;
	}
}
