package at.sume.dm.types;

/**
 * This enum is needed for counting the migrating households/persons per spatial unit
 * 
 * @author Alexander Remesch
 */
public enum MigrationRealm {
	LOCAL,
	NATIONAL,
	INTERNATIONAL,
	LEAVING_PARENTS,
	COHABITATION;
	// TODO: LeavingParents and cohabitation don't really fit into a type "migration realm" - Probably it would be
	// best to create another type for these two?
}
