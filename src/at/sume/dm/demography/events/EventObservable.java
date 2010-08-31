/**
 * 
 */
package at.sume.dm.demography.events;


/**
 * Observer pattern interface counterpart for EventObserver
 * @author Alexander Remesch
 *
 */
public interface EventObservable {
	public void registerObserver(EventObserver observer);
	public void removeObserver(EventObserver observer);
	public void notifyObservers();
}
