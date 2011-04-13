/**
 * 
 */
package at.sume.dm.model.core;

import net.remesch.db.Database;
import at.sume.db.RecordSet;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public abstract class EntityDecision<T extends RecordSetRow<?>, S extends RecordSet<?>> {
	public EntityDecision(Database db, EntityDecisionManager<T, S> entityDecisionManager) {
		entityDecisionManager.register(this);
	}

	/**
	 * Calculate the decision of the entity
	 * @param entity
	 * @return true
	 */
	protected abstract boolean decide(T entity);
	
	public void makeDecision(T entity) {
		boolean b = decide(entity);
		if (b == true) {
			consequence(entity);
		}
	}
	
	/**
	 * What shall happen when the decision is made by an entity?
	 * @param entity
	 */
	public abstract void consequence(T entity);

	/**
	 * Filter entities on which can't make a certain decision
	 * @param entity
	 * @return true if the decision can't be made by the entity, false if not
	 */
	public boolean condition(T entity) {
		return true;
	}
}
