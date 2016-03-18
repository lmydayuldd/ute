/**
 * 
 */
package at.sume.dm.model.timeuse;

/**
 * Time Use Types for all scenarios
 * 
 * @author Alexander Remesch
 *
 */
public enum TimeUseType {
	MALE_CHILD_WORK,
	MALE_CHILD_NOWORK, 
	FEMALE_CHILD_WORK, 
	FEMALE_CHILD_NOWORK, 
	MALE_NOCHILD_WORK, 
	MALE_NOCHILD_NOWORK, 
	FEMALE_NOCHILD_WORK, 
	FEMALE_NOCHILD_NOWORK,
	
	SINGLE_YOUNG,
	SINGLE_OLD,
	COUPLE_YOUNG,
	COUPLE_OLD,
	SINGLE_PARENT,
	SMALL_FAMILY,
	LARGE_FAMILY,
	OTHER;

	@Override
	public String toString() {
		switch (this) {
		case MALE_CHILD_WORK:
			return "MALE_CHILD_WORK";
		case MALE_CHILD_NOWORK: 
			return "MALE_CHILD_NOWORK";
		case FEMALE_CHILD_WORK: 
			return "FEMALE_CHILD_WORK";
		case FEMALE_CHILD_NOWORK: 
			return "FEMALE_CHILD_NOWORK";
		case MALE_NOCHILD_WORK: 
			return "MALE_NOCHILD_WORK";
		case MALE_NOCHILD_NOWORK: 
			return "MALE_NOCHILD_NOWORK";
		case FEMALE_NOCHILD_WORK: 
			return "FEMALE_NOCHILD_WORK";
		case FEMALE_NOCHILD_NOWORK:
			return "FEMALE_NOCHILD_NOWORK";
		case SINGLE_YOUNG:
			return "SINGLE_YOUNG";
		case SINGLE_OLD:
			return "SINGLE_OLD";
		case COUPLE_YOUNG:
			return "COUPLE_YOUNG";
		case COUPLE_OLD:
			return "COUPLE_OLD";
		case SINGLE_PARENT:
			return "SINGLE_PARENT";
		case SMALL_FAMILY:
			return "SMALL_FAMILY";
		case LARGE_FAMILY:
			return "LARGE_FAMILY";
		case OTHER:
			return "OTHER";
		default:
			throw new AssertionError("Unknown TimeUseType");
		}
	}
}
