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
	 * @param households
	 * @param dwellings
	 * @param persons
	 * @throws FileNotFoundException 
	 */
	public OutputManager(String path, List<List<? extends Fileable>> fileableList) {
		fileOutputList = new ArrayList<FileOutput>();
		for (List<? extends Fileable> fileable : fileableList) {
			add(path, fileable);
		}
//		fileOutputHouseholds = new FileOutput(path, "households", households.getRowList());
//		fileOutputPersons = new FileOutput(path, "persons", persons.getRowList());
//		fileOutputDwellings = new FileOutput(path, "dwellings", dwellings.getRowList());
//		fileOutputRentPerSpatialUnit = new FileOutput(path, "rent_prices", )
	}
	/**
	 * Add a new fileable output entity
	 * @param path
	 * @param fileable
	 */
	public void add(String path, List<? extends Fileable> fileable) {
		assert fileable.size() > 0 : "List fileable is empty!";
		String entityName = fileable.get(0).getClass().getSimpleName();
		FileOutput fileoutput = new FileOutput(path, entityName, fileable);
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
	public void fileOutput(short modelYear) throws IOException {
		for (FileOutput fileOutput : fileOutputList) {
			fileOutput.persistDb(modelYear);
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
	public void output(short modelYear) throws IllegalArgumentException, SQLException, IllegalAccessException, IOException {
		if (dbOutputHouseholds != null)
			dbOutput(modelYear);
		else
			fileOutput(modelYear);
	}
}
