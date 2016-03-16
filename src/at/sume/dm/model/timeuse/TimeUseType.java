/**
 * 
 */
package at.sume.dm.model.timeuse;

/**
 * @author Alexander Remesch
 *
 */
public enum TimeUseType {
	SEX_CHILD_WORK, HOUSEHOLD_TYPE;

	public byte getId() {
		switch (this) {
		case SEX_CHILD_WORK:
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
		case SEX_CHILD_WORK:
			return "SEX_CHILD_WORK";
		case HOUSEHOLD_TYPE:
			return "HOUSEHOLD_TYPE";
		default:
			throw new AssertionError("Unknown TimeUseType");
		}
	}
}
