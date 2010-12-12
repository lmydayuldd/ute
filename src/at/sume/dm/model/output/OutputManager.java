/**
 * 
 */
package at.sume.dm.model.output;

import java.sql.SQLException;

import net.remesch.db.Database;
import at.sume.dm.entities.Households;

/**
 * @author Alexander Remesch
 *
 */
public class OutputManager {
	DbOutputHouseholds outputHouseholds;
	
	public OutputManager(Database db, Households households) {
		outputHouseholds = new DbOutputHouseholds(db, households);
	}
	
	public void dbOutput(short modelYear) throws IllegalArgumentException, SQLException, IllegalAccessException {
		outputHouseholds.persistDb(modelYear);
	}
}
