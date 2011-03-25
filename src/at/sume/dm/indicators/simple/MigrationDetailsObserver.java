/**
 * 
 */
package at.sume.dm.indicators.simple;

import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public interface MigrationDetailsObserver {
	public void addMigration(Integer spatialUnitIdFrom, Integer spatialUnitIdTo, MigrationRealm migrationRealm, HouseholdCharacteristics householdCharacteristics);
}
