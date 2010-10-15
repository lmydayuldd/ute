/**
 * 
 */
package at.sume.dm.indicators;

import at.sume.dm.entities.HouseholdRow;

/**
 * @author Alexander Remesch
 *
 */
public interface Indicator {
	/**
	 * Add data of a household to the set of indicators
	 * @param hh
	 */
	public void add(HouseholdRow hh);
	/**
	 * Remove data of a household from the set of indicators
	 * @param hh
	 */
	public void remove(HouseholdRow hh);
	/**
	 * Reset the set of indicators to zero
	 */
	public void clear();
}
