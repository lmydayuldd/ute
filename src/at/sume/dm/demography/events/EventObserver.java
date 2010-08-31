/**
 * 
 */
package at.sume.dm.demography.events;

/**
 * Observer pattern interface counterpart for EventObservable
 * @author Alexander Remesch
 *
 */
public interface EventObserver {
	public void eventOccured(EventObservable observable, String event);
}
