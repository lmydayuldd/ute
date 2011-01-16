/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

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
	FileOutputHouseholds fileOutputHouseholds;
	FileOutputPersons fileOutputPersons;
	FileOutputDwellings fileOutputDwellings;
	
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
	public OutputManager(String path, Households households, Dwellings dwellings, Persons persons) {
		fileOutputHouseholds = new FileOutputHouseholds(path, households);
		fileOutputPersons = new FileOutputPersons(path, persons);
		fileOutputDwellings = new FileOutputDwellings(path, dwellings);
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
		fileOutputHouseholds.persistDb(modelYear);
		fileOutputPersons.persistDb(modelYear);
		fileOutputDwellings.persistDb(modelYear);
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
