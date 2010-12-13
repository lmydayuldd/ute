/**
 * 
 */
package at.sume.dm.model.output;

import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.db.Database;
import at.sume.db.RecordSet;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public abstract class DbOutput<T extends RecordSetRow<?>, U extends OutputRow> {
	RecordSet<T> rowList;
	ArrayList<U> outputRowList;
	Database db;
	
	public DbOutput(Database db, RecordSet<T> rowList) {
		this.db = db;
		this.rowList = rowList;
	}
	
	public void persistDb(short modelYear) throws IllegalArgumentException, SQLException, IllegalAccessException {
		outputRowList = new ArrayList<U>();
		for (T row : rowList) {
			U orow = outputRowFactory(modelYear, row);
			outputRowList.add(orow);
		}
		db.insert(outputRowList, sqlStatement());
//		db.insertFieldMap(outputRowList, sqlStatement());
		// InsertSQL is slower...
//		db.insertSql(outputRowList, sqlStatement());
	}
	
	abstract U outputRowFactory(short modelYear, T row);
	abstract String sqlStatement();
}
