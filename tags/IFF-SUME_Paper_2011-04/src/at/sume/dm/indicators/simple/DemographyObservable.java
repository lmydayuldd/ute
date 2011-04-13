/**
 * 
 */
package at.sume.dm.indicators.simple;


/**
 * @author Alexander Remesch
 *
 */
public interface DemographyObservable {
	public void registerDemographyObserver(DemographyObserver o);
	public void removeDemographyObserver(DemographyObserver o);
	public void notifyBirth(Integer spatialUnitId);
	public void notifyDeath(Integer spatialUnitId);
}
