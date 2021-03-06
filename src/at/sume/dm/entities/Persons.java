/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;

import at.sume.db.RecordSetClonable;
import at.sume.dm.tracing.ObjectSource;
import net.remesch.db.Database;
import net.remesch.util.Random;

/**
 * @author Alexander Remesch
 *
 */
public class Persons extends RecordSetClonable<PersonRow> {
	Random r = new Random();
	
	/**
	 * needed for cloning
	 */
	public Persons() {
		super();
	}
	
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
	public PersonRow createRecordSetRow() {
		return new PersonRow(ObjectSource.INIT);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "PersonId", "HouseholdId", "Sex", "Age", "AgeGroupId", "HouseholdRepresentative", "YearlyIncome", "PersonNrInHousehold", "WorkplaceId" };
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
			assert p.getHouseholdId() != 0 : "no household set for person " + p.getPersonId();
			HouseholdRow hh = households.lookup(p.getHouseholdId());
			hh.addMember(p);
			//p.setHousehold(hh); - this is already done in hh.addMember()
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_Persons";
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetClonable#factory()
	 */
	@Override
	public RecordSetClonable<PersonRow> factory() {
		return new Persons();
	}
	
	/**
	 * Return a random person
	 * @return
	 */
	public PersonRow getRandomPerson() {
		int index = r.nextInt(rowList.size());
		return rowList.get(index);
	}
}
