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
	// TODO: implement similar to Activity.java
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
			return "1 male/child/work";
		case MALE_CHILD_NOWORK: 
			return "2 male/child/no work";
		case FEMALE_CHILD_WORK: 
			return "3 female/child/work";
		case FEMALE_CHILD_NOWORK: 
			return "4 female/child/no work";
		case MALE_NOCHILD_WORK: 
			return "5 male/no child/work";
		case MALE_NOCHILD_NOWORK: 
			return "6 male/no child/no work";
		case FEMALE_NOCHILD_WORK: 
			return "7 female/no child/work";
		case FEMALE_NOCHILD_NOWORK:
			return "8 female/no child/no work";
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
