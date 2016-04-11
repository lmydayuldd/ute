/**
 * 
 */
package at.sume.dm.demography.events;

import java.util.ArrayList;

import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public class EventManager<E extends RecordSetRow<?>> {
	private ArrayList<Event<E>> eventHandlers;
	
	public EventManager() {
		eventHandlers = new ArrayList<Event<E>>();
	}
	
	public void register(Event<E> event) {
		eventHandlers.add(event);
	}
	
	public void remove(Event<E> event) {
		int i = eventHandlers.indexOf(event);
		if (i >= 0) {
			eventHandlers.remove(event);
		}
	}
	
	public void process(E entity) {
		for (Event<E> eventHandler : eventHandlers) {
			if (eventHandler.condition(entity)) {
				if (!eventHandler.occur(entity))
					break;
			}
		}
	}
}
