/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.remesch.db.Database;
import at.sume.dm.entities.Dwellings;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.Persons;

/**
 * @author Alexander Remesch
 *
 */
public class OutputManager {
	DbOutputHouseholds dbOutputHouseholds;
	ArrayList<FileOutput> fileOutputList;
	boolean createNewOutputFile = false;
//	FileOutput fileOutputHouseholds;
//	FileOutput fileOutputPersons;
//	FileOutput fileOutputDwellings;
	
	/**
	 * 
	 * @param db
	 * @param households
	 * @param dwellings
	 * @param persons
	 */
	public OutputManager(Database db, Households households, Dwellings dwellings, Persons persons) {
		dbOutputHouseholds = new DbOutputHouseholds(db, households);
	}
	/**
	 * 
	 * @param path
	 * @param fileNameList
	 * @param fileableList
	 * @throws FileNotFoundException 
	 */
	public OutputManager(int modelRun, String path, List<String> fileNameList, List<List<? extends Fileable>> fileableList) throws FileNotFoundException {
		assert fileNameList.size() == fileableList.size() : "fileNameList is longer/shorter than fileableList";
		fileOutputList = new ArrayList<FileOutput>();
		int i = 0;
		for (List<? extends Fileable> fileable : fileableList) {
			add(path, fileNameList.get(i++), fileable);
		}
//		fileOutputHouseholds = new FileOutput(path, "households", households.getRowList());
//		fileOutputPersons = new FileOutput(path, "persons", persons.getRowList());
//		fileOutputDwellings = new FileOutput(path, "dwellings", dwellings.getRowList());
//		fileOutputRentPerSpatialUnit = new FileOutput(path, "rent_prices", )
		if (modelRun == 0)
			createNewOutputFile = true;
	}
	/**
	 * Add a new fileable output entity
	 * @param path
	 * @param fileable
	 * @throws FileNotFoundException 
	 */
	public void add(String path, String fileName, List<? extends Fileable> fileable) throws FileNotFoundException {
		// Fileable list may yet be empty (in case of indicators)
//		assert fileable.size() > 0 : "List fileable is empty!";
//		String entityName = fileable.get(0).getClass().getSimpleName();
		FileOutput fileoutput = new FileOutput(path, fileName, fileable, createNewOutputFile);
		fileOutputList.add(fileoutput);
	}
	/**
	 * 
	 * @param modelYear
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 */
	public void dbOutput(short modelYear) throws IllegalArgumentException, SQLException, IllegalAccessException {
		dbOutputHouseholds.persistDb(modelYear);
	}
	/**
	 * 
	 * @param modelYear
	 * @throws IOException 
	 */
	public void fileOutput(int modelRun, short modelYear) {
		for (FileOutput fileOutput : fileOutputList) {
			fileOutput.persistDb(modelRun, modelYear);
		}
//		fileOutputHouseholds.persistDb(modelYear);
//		fileOutputPersons.persistDb(modelYear);
//		fileOutputDwellings.persistDb(modelYear);
	}
	/**
	 * 
	 * @param modelYear
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws IOException 
	 */
	public void output(int modelRun, short modelYear) throws IllegalArgumentException, SQLException, IllegalAccessException, IOException {
		if (dbOutputHouseholds != null)
			dbOutput(modelYear);
		else
			fileOutput(modelRun, modelYear);
	}

	public void close() {
		for (FileOutput fileOutput : fileOutputList) {
			fileOutput.close();
		}
	}
}
