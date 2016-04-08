/**
 * 
 */
package at.sume.dm.model.timeuse;

import at.sume.dm.entities.PersonRow;

/**
 * Determine time use type depending on persons' gender, employment status and whether there are
 * children in the household
 * 
 * @author Alexander Remesch
 */
public class TimeUseTypeByGenderChildWork implements TimeUseTypeDesignator {

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.TimeUseTypeDesignator#getTimeUseType(at.sume.dm.entities.PersonRow)
	 */
	@Override
	public TimeUseType getTimeUseType(PersonRow person) {
		if (person.getAge() > 10) {
			if (person.getSex() == 1) { // female
				if (person.getWorkplaceCellId() != 0) { // employed
					if (person.getHousehold().hasChildrenBelow18() && person.getHousehold().getHouseholdSize() > 1) {
						return TimeUseType.FEMALE_CHILD_WORK;
					} else {
						return TimeUseType.FEMALE_NOCHILD_WORK;
					}
				} else { // not employed
					if (person.getHousehold().hasChildrenBelow18() && person.getHousehold().getHouseholdSize() > 1) {
						return TimeUseType.FEMALE_CHILD_NOWORK;
					} else {
						return TimeUseType.FEMALE_NOCHILD_NOWORK;
					}
				}
			} else { // male
				if (person.getWorkplaceCellId() != 0) { // employed
					if (person.getHousehold().hasChildrenBelow18() && person.getHousehold().getHouseholdSize() > 1) {
						return TimeUseType.MALE_CHILD_WORK;
					} else {
						return TimeUseType.MALE_NOCHILD_WORK;
					}
				} else { // not employed
					if (person.getHousehold().hasChildrenBelow18() && person.getHousehold().getHouseholdSize() > 1) {
						return TimeUseType.MALE_CHILD_NOWORK;
					} else {
						return TimeUseType.MALE_NOCHILD_NOWORK;
					}
				}
			}
		} else
			return TimeUseType.NONE;
	}
}
