/**
 * 
 */
package at.sume.dm.demography.events;

import java.util.ArrayList;

/**
 * @author Alexander Remesch
 *
 */
public class EventManager<E> {
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
			eventHandler.occur(entity);
		}
	}
}
