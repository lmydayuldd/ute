/**
 * 
 */
package at.sume.dm.model.timeuse;

/**
 * TODO: this is currently unused - put the things that are currently done in Main() here!
 * 
 * @author Alexander Remesch
 */
public enum TimeUseTypeScenario {
	GENDER_CHILD_WORK, HOUSEHOLD_TYPE;

	public byte getId() {
		switch (this) {
		case GENDER_CHILD_WORK:
			return 1;
		case HOUSEHOLD_TYPE:
			return 2;
		default:
			throw new AssertionError("Unknown householdType");
		}
	}
	
	@Override
	public String toString() {
		switch (this) {
		case GENDER_CHILD_WORK:
			return "SEX_CHILD_WORK";
		case HOUSEHOLD_TYPE:
			return "HOUSEHOLD_TYPE";
		default:
			throw new AssertionError("Unknown TimeUseType");
		}
	}
}
