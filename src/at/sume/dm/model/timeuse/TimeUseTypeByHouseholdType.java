/**
 * 
 */
package at.sume.dm.model.timeuse;

import at.sume.dm.entities.PersonRow;

/**
 * @author Alexander Remesch
 *
 */
public class TimeUseTypeByHouseholdType implements TimeUseTypeDesignator {

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.TimeUseTypeDesignator#getTimeUseType(at.sume.dm.entities.PersonRow)
	 */
	@Override
	public TimeUseType getTimeUseType(PersonRow person) {
		switch (person.getHousehold().getHouseholdType()) {
		case SINGLE_YOUNG:
			return TimeUseType.SINGLE_YOUNG;
		case SINGLE_OLD:
			return TimeUseType.SINGLE_OLD;
		case COUPLE_YOUNG:
			return TimeUseType.COUPLE_YOUNG;
		case COUPLE_OLD:
			return TimeUseType.COUPLE_OLD;
		case SINGLE_PARENT:
			return TimeUseType.SINGLE_PARENT;
		case SMALL_FAMILY:
			return TimeUseType.SMALL_FAMILY;
		case LARGE_FAMILY:
			return TimeUseType.LARGE_FAMILY;
		default:
			return TimeUseType.OTHER;
		}
	}

}
