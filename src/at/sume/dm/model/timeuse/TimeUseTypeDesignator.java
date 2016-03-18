/**
 * 
 */
package at.sume.dm.model.timeuse;

import at.sume.dm.entities.PersonRow;

/**
 * This will designate a time-use type for each person depending on the general time-use type scenario
 * (currently Gender/Child/Work and HouseholdType) which is implementation-dependent
 * 
 * @author Alexander Remesch
 */
public interface TimeUseTypeDesignator {
	public TimeUseType getTimeUseType(PersonRow person);
}
