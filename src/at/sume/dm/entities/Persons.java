/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;

import net.remesch.util.Database;
import at.sume.db.RecordSet;

/**
 * @author Alexander Remesch
 *
 */
public class Persons extends RecordSet<PersonRow> {

	/**
	 * @param db
	 * @throws SQLException
	 */
	public Persons(Database db) throws SQLException {
		super(db);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createDatabaseRecord(at.sume.db.RecordSet)
	 */
	@Override
	public PersonRow createDatabaseRecord(RecordSet<PersonRow> recordSet) {
		return new PersonRow((Persons) recordSet);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "PersonId", "HouseholdId", "Sex", "YearBorn", "AgeGroupId", "HouseholdRepresentative", "YearlyIncome" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	public String[] primaryKeyFieldnames() {
		String s[] = { "PersonId" };
		return s;
	}

	/**
	 * Link households and persons according to the household-id in the person row
	 * @param households
	 */
	public void linkHouseholds(Households households) {
		for (PersonRow p : rowList) {
			HouseholdRow hh = households.lookup(p.getHouseholdId());
			p.setHousehold(hh);
			hh.addMember(p);
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_Persons";
	}
}
