/**
 * 
 */
package at.sume.dm.demography.events;

import java.sql.SQLException;
import java.util.Random;

import net.remesch.db.Database;
import at.sume.dm.demography.Fertility;
import at.sume.dm.entities.PersonRow;

/**
 * @author Alexander Remesch
 *
 */
public class ChildBirth extends Event<PersonRow> {
	Fertility fertilityDistribution;
	// Sex proportion for births - number of male per female births
	static Double sexProportion;

	/**
	 * @param eventManager
	 * @throws SQLException 
	 */
	public ChildBirth(Database db, EventManager<PersonRow> eventManager) throws SQLException {
		super(db, eventManager);
		fertilityDistribution = new Fertility(db);
		// set sex proportion here
		// TODO: find a better place for this
		sexProportion = (Double) db.lookupSql("select SexProp from StatA_SexProp_W where Jahr = 2009");
		if (sexProportion == null)
			sexProportion = 1000.0;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.events.Event#probability(at.sume.db.RecordSetRow)
	 */
	@Override
	protected double probability(PersonRow entity) {
		return fertilityDistribution.probability(entity.getAgeGroupId());
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.events.Event#action(at.sume.dm.demography.events.EventAction)
	 */
	@Override
	public void action(PersonRow entity) {
		// if a child is born, it will be added to the household of the mother
		PersonRow child = new PersonRow(entity.getPersons());
		child.setHousehold(entity.getHousehold());
		child.setAgeGroupId((short) 1);
//		child.setAge((short) 0);
		child.setHouseholdRepresentative(false);
		child.setAge((short)0);
		// determine sex - sexProportion male (2) per 1000 female (1) births
		Random r = new Random();
		long rand = (long) (r.nextDouble() * (sexProportion + 1000));
		if (rand > 1000) {
			child.setSex((short)2);
		} else {
			child.setSex((short)1);
		}
		entity.getHousehold().addMember(child);
		entity.getPersons().add(child);
	}
	
	/**
	 * Birth of a child can happen only to females
	 */
	@Override
	public boolean condition(PersonRow entity) {
		if (entity.getSex() == 1) {
			return true;
		} else {
			return false;
		}
	}
}
