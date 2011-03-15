/**
 * 
 */
package at.sume.dm.indicators.simple;

import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public interface MigrationObserver {
	/**
	 * Count one household that is migrating within the model area
	 * 
	 * @param srcSpatialUnitId Spatial unit the household emigrates from
	 * @param destSpatialUnitId Spatial unit the household immigrates to
	 * @param householdMemberCount Number of household members
	 */
	public void addLocalMigration(Integer srcSpatialUnitId, Integer destSpatialUnitId, byte householdMemberCount);
	/**
	 * Count one newly founded household by a child leaving her/his parents
	 * 
	 * @param srcSpatialUnitId
	 * @param destSpatialUnitId
	 */
	public void addChildLeavingParents(Integer srcSpatialUnitId, Integer destSpatialUnitId);
	/**
	 * Count one household that moves in with another household
	 * 
	 * @param srcSpatialUnitId
	 * @param destSpatialUnitId
	 * @param householdMemberCount
	 */
	public void addCohabitation(Integer srcSpatialUnitId, Integer destSpatialUnitId, byte householdMemberCount);
	/**
	 * Count one household that is emigrating from the model area
	 * 
	 * @param srcSpatialUnitId Spatial unit the household emigrates from
	 * @param householdMemberCount Number of household members
	 * @param migrationRealm Specifies whether the household moves within the model area, to another place within the same country or to another country
	 */
	public void addEmigration(Integer srcSpatialUnitId, byte householdMemberCount, MigrationRealm migrationRealm);
	/**
	 * Count one household that is immigrating to the model area
	 * 
	 * @param destSpatialUnitId Spatial unit the household is immigrating to
	 * @param householdMemberCount Number of household members
	 * @param migrationRealm Specifies whether the household moves within the model area, to another place within the same country or to another country
	 */
	public void addImmigration(Integer destSpatialUnitId, byte householdMemberCount, MigrationRealm migrationRealm);
}
