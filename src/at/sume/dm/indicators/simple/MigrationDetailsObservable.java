/**
 * 
 */
package at.sume.dm.indicators.simple;

import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public interface MigrationDetailsObservable {
	public void registerMigrationObserver(MigrationDetailsObserver o);
	public void removeMigrationObserver(MigrationDetailsObserver o);
	public void notifyMigration(Integer spatialUnitIdFrom, Integer spatialUnitIdTo, MigrationRealm migrationRealm, HouseholdCharacteristics householdCharacteristics);
}
