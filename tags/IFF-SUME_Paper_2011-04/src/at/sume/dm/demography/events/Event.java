/**
 * 
 */
package at.sume.dm.demography.events;

import net.remesch.db.Database;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public abstract class Event<T extends RecordSetRow<?>> {
	public Event(Database db, EventManager<T> eventManager) {
		eventManager.register(this);
	}

	protected abstract double probability(T entity);
	
	public void occur(T entity) {
		// generate random number for sampling
		double rand = Math.random();
		double p = probability(entity);
		if (rand < p) {
			action(entity);
		}
	}
	
	/**
	 * What shall happen when the event occurs on an entity? (consequences)
	 * @param entity
	 */
	public abstract void action(T entity);

	/**
	 * Filter entities on which an event can't happen
	 * @param entity
	 * @return true if the event can happen to the entity, false if not
	 */
	public boolean condition(T entity) {
		return true;
	}
}
