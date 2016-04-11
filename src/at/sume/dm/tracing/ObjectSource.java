/**
 * 
 */
package at.sume.dm.tracing;

/**
 * This is to determine where an object (household, person, dwelling) was created in order to trace it back to its origin
 * in case of an error.
 * 
 * @author Alexander Remesch
 */
public enum ObjectSource {
	UNKNOWN, INIT, IMMIGRATION, LEAVING_PARENTS, MOVING_TOGETHER_TEMP, TEMP_LOOKUP, BIRTH, BUILDING_PROJECT;
}
