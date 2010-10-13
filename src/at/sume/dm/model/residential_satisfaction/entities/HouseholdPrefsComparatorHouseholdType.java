/**
 * 
 */
package at.sume.dm.model.residential_satisfaction.entities;

import java.util.Comparator;

/**
 * @author Alexander Remesch
 *
 */
public class HouseholdPrefsComparatorHouseholdType implements Comparator<HouseholdPrefs> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(HouseholdPrefs o1, HouseholdPrefs o2) {
		if (o1.householdTypeId == o2.householdTypeId)
			return 0;
		else if (o1.householdTypeId > o2.householdTypeId) 
			return 1;
		else
			return -1;
	}

}
