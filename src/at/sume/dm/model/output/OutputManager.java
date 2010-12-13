/**
 * 
 */
package at.sume.dm.model.output;

import java.io.FileNotFoundException;
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
	 * @param pathName
	 * @param households
	 * @param dwellings
	 * @param persons
	 * @throws FileNotFoundException 
	 */
	public OutputManager(String pathName, Households households, Dwellings dwellings, Persons persons) throws FileNotFoundException {
		fileOutputHouseholds = new FileOutputHouseholds(pathName + "households.csv", households);
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
	 */
	public void fileOutput(short modelYear) {
		fileOutputHouseholds.persistDb(modelYear);
	}
	/**
	 * 
	 * @param modelYear
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 */
	public void output(short modelYear) throws IllegalArgumentException, SQLException, IllegalAccessException {
		if (dbOutputHouseholds != null)
			dbOutput(modelYear);
		else
			fileOutput(modelYear);
	}
}
