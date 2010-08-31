/**
 * 
 */
package at.sume.dm.demography.events;

import java.util.Random;
import net.remesch.util.Database;

/**
 * @author Alexander Remesch
 *
 */
public abstract class Event<T> {
	public Event(Database db, EventManager<T> eventManager) {
		eventManager.register(this);
	}

	protected abstract double probability(T entity);
	
	public void occur(T entity) {
		Random r = new Random();
		// generate random number for sampling
		long rand = (long) (r.nextDouble() * 100);
		double p = probability(entity);
		if (rand <= p) {
			action(entity);
		}
	}
	
	/**
	 * What shall happen when the event occurs on an entity? (consequences)
	 * @param entity
	 */
	public abstract void action(T entity);
	
//	public double probability(T entity);
}
