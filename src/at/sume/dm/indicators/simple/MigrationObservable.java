/**
 * 
 */
package at.sume.dm.indicators.simple;

import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public interface MigrationObservable {
	public void registerMigrationObserver(MigrationObserver o);
	public void removeMigrationObserver(MigrationObserver o);
	public void notifyLocalMigration(Integer srcSpatialUnitId, Integer destSpatialUnitId);
	public void notifyEmigration(Integer srcSpatialUnitId, MigrationRealm migrationRealm);
	public void notifyImmigration(Integer destSpatialUnitId, MigrationRealm migrationRealm);
	public void notifyLeavingParents(Integer srcSpatialUnitId, Integer destSpatialUnitId);
	public void notifyMovingTogether(Integer srcSpatialUnitId, Integer destSpatialUnitId);
}
