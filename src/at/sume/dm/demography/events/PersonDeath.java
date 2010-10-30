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
	Mortality mortalityDistribution;
	
	/**
	 * @param eventManager
	 * @throws SQLException 
	 */
	public PersonDeath(Database db, EventManager<PersonRow> eventManager) throws SQLException {
		super(db, eventManager);
		mortalityDistribution = new Mortality(db);
	}

//	/* (non-Javadoc)
//	 * @see at.sume.dm.demography.events.Event#occur(java.lang.Object)
//	 */
//	@Override
//	public void occur(PersonRow entity) {
//		Random r = new Random();
//		// generate random number for sampling
//		long rand = (long) (r.nextDouble() * 100);
//		if (rand <= mortalityDistribution.probability(entity)) {
//			// if a person dies, it will be simply removed
//			// the corresponding household will be notified via an Observer/Observable 
//			// and will check itself if any further action is necessary
//			entity.remove();
//		}
//	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.events.Event#probability(at.sume.db.RecordSetRow)
	 */
	@Override
	protected double probability(PersonRow entity) {
		return mortalityDistribution.probability(entity.getAgeGroupId(), entity.getSex());
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.events.Event#action(at.sume.dm.demography.events.EventAction)
	 */
	@Override
	public void action(PersonRow entity) {
		// if a person dies, it will be simply removed
		// the corresponding household will be notified via an Observer/Observable 
		// and will check itself if any further action is necessary
		entity.remove();
	}
}
