/**
 * 
 */
package at.sume.dm.demography.events;

import java.sql.SQLException;

import net.remesch.db.Database;
import at.sume.dm.demography.Mortality;
import at.sume.dm.entities.PersonRow;

/**
 * @author Alexander Remesch
 *
 */
public class PersonDeath extends Event<PersonRow> {
	private Mortality mortalityDistribution;
	private short maxAge;
	
	/**
	 * @param eventManager
	 * @throws SQLException 
	 */
	public PersonDeath(Database db, EventManager<PersonRow> eventManager, short maxAge) throws SQLException {
		super(db, eventManager);
		mortalityDistribution = new Mortality(db);
		this.maxAge = maxAge;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.events.Event#probability(at.sume.db.RecordSetRow)
	 */
	@Override
	protected double probability(PersonRow entity) {
		if (entity.getAge() >= maxAge) {
			// this should not possibly happen
			return 1;
		} else {
			return mortalityDistribution.probability(entity.getAge(), entity.getSex());
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.events.Event#action(at.sume.dm.demography.events.EventAction)
	 */
	@Override
	public void action(PersonRow entity) {
		// if a person dies, it will be simply removed
		// the corresponding household will be notified via an Observer/Observable 
		// and will check itself if any further action is necessary
		entity.die();
	}
}
