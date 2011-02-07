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
	private String path = "";
	private String fileName;
	private final static String delimiter = ";";
	
	/**
	 * 
	 * @param path
	 * @param rowList
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public FileOutput(String path, String fileName, List<? extends Fileable> rowList) {
//		assert rowList.size() > 0 : "rowList cannot be empty!";
		this.rowList = (ArrayList<Fileable>) rowList;
		if (path != null)
			if (path.endsWith("\\"))
				this.path = path;
			else
				this.path = path + "\\";
		this.fileName = fileName;
	}
	/**
	 * 
	 * @param modelYear
	 * @throws IOException 
	 */
	public void persistDb(short modelYear) throws IOException {
		String pathName = path + fileName + "_" + modelYear + ".csv";
		// Rename existing file to a unique filename
		FileUtil.rotateFile(pathName);
		FileOutputStream fileOutputStream = new FileOutputStream(pathName, true);
		psOut = new PrintStream(fileOutputStream);
		psOut.println("ModelYear" + delimiter + rowList.get(0).toCsvHeadline(delimiter));
		for (Fileable row : rowList) {
			String orow = modelYear + delimiter + row.toString(delimiter);
			psOut.println(orow);
		}
		psOut.close();
		fileOutputStream.close();
	}
}
