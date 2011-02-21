/**
 * 
 */
package at.sume.dm.indicators.simple;

/**
 * @author Alexander Remesch
 *
 */
public interface DemographyObserver {
	public void addBirth(Integer spatialUnitId);
	public void addDeath(Integer spatialUnitId);
}
