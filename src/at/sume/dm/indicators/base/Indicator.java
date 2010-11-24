/**
 * 
 */
package at.sume.dm.indicators.base;

import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 * TODO: To have a joint set of indicators this interface could include add/remove methods for each entity that will be
 * accumulated - like dwellings, household moves (for prices), etc.
 * The template version has the disadvantage that each entity will need an extra instance.
 */
public interface Indicator<T extends RecordSetRow<?>> {
	/**
	 * Add data of a household to the set of indicators
	 * @param hh
	 */
	public void add(T hh);
	/**
	 * Remove data of a household from the set of indicators
	 * @param hh
	 */
	public void remove(T hh);
	/**
	 * Reset the set of indicators to zero
	 */
	public void clear();
}
