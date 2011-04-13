/**
 * 
 */
package at.sume.dm.model.core;

import java.util.ArrayList;

import at.sume.db.RecordSet;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public class EntityDecisionManager<E extends RecordSetRow<?>, F extends RecordSet<?>> {
	private ArrayList<EntityDecision<E, F>> entityDecisionHandlers;
	
	public EntityDecisionManager() {
		entityDecisionHandlers = new ArrayList<EntityDecision<E, F>>();
	}
	
	public void register(EntityDecision<E, F> event) {
		entityDecisionHandlers.add(event);
	}
	
	public void remove(EntityDecision<E, F> event) {
		int i = entityDecisionHandlers.indexOf(event);
		if (i >= 0) {
			entityDecisionHandlers.remove(event);
		}
	}
	
	public void process(E entity) {
		for (EntityDecision<E, F> entityDecisionHandler : entityDecisionHandlers) {
			if (entityDecisionHandler.condition(entity)) {
				entityDecisionHandler.makeDecision(entity);
			}
		}
	}
}
