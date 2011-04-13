/**
 * 
 */
package at.sume.dm.model.output;

import net.remesch.db.Database;
import at.sume.db.RecordSet;
import at.sume.dm.entities.HouseholdRow;

/**
 * @author Alexander Remesch
 *
 */
public class DbOutputHouseholds extends DbOutput<HouseholdRow, OutputHouseholdRow> {
	/**
	 * @param rowList
	 */
	public DbOutputHouseholds(Database db, RecordSet<HouseholdRow> rowList) {
		super(db, rowList);
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.DbOutput#outputRowFactory(at.sume.db.RecordSetRow)
	 */
	@Override
	OutputHouseholdRow outputRowFactory(short modelYear, HouseholdRow row) {
		return new OutputHouseholdRow(modelYear, row);
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.DbOutput#sqlStatement()
	 */
	@Override
	String sqlStatement() {
		return "select * from _DM_Households";
		// insertSQL version
//		return "_DM_Households";
	}
}
