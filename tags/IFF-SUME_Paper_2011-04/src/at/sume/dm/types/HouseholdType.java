/**
 * 
 */
package at.sume.dm.types;

/**
 * @author Alexander Remesch
 *
 */
public enum HouseholdType {
	SINGLE_YOUNG,
	COUPLE_YOUNG,
	SINGLE_PARENT,
	SMALL_FAMILY,
	LARGE_FAMILY,
	SINGLE_OLD,
	COUPLE_OLD,
	OTHER;
	
	public byte getId() {
		switch (this) {
		case SINGLE_YOUNG:
			return 1;
		case COUPLE_YOUNG:
			return 2;
		case SINGLE_PARENT:
			return 3;
		case SMALL_FAMILY:
			return 4;
		case LARGE_FAMILY:
			return 5;
		case SINGLE_OLD:
			return 6;
		case COUPLE_OLD:
			return 7;
		case OTHER:
			return 8;
		default:
			throw new AssertionError("Unknown householdType");
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this) {
		case SINGLE_YOUNG:
			return "single young";
		case COUPLE_YOUNG:
			return "couple young";
		case SINGLE_PARENT:
			return "single parent";
		case SMALL_FAMILY:
			return "small family";
		case LARGE_FAMILY:
			return "large family";
		case SINGLE_OLD:
			return "single old";
		case COUPLE_OLD:
			return "couple old";
		case OTHER:
			return "other";
		default:
			throw new AssertionError("Unknown householdType");
		}
	}
}
