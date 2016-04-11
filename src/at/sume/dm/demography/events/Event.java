/**
 * 
 */
package at.sume.dm.demography.events;

import at.sume.db.RecordSetRow;
import net.remesch.db.Database;
import net.remesch.util.Random;

/**
 * @author Alexander Remesch
 *
 */
public abstract class Event<T extends RecordSetRow<?>> {
	Random r = new Random();
	public Event(Database db, EventManager<T> eventManager) {
		eventManager.register(this);
	}

	protected abstract double probability(T entity);
	/**
	 * Perform occuring event
	 * @param entity
	 * @return Continue with other events for the same object?
	 */
	public boolean occur(T entity) {
		// generate random number for sampling
		double rand = r.nextDouble();
		double p = probability(entity);
		if (rand < p) {
			return action(entity);
		}
		return true;
	}
	
	/**
	 * What shall happen when the event occurs on an entity? (consequences)
	 * @param entity
	 * @return Continue with other events for the same object?
	 */
	public abstract boolean action(T entity);

	/**
	 * Filter entities on which an event can't happen
	 * @param entity
	 * @return true if the event can happen to the entity, false if not
	 */
	public boolean condition(T entity) {
		return true;
	}
}
