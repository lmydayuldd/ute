/**
 * 
 */
package at.sume.dm.indicators.base;

import java.util.List;

import at.sume.db.RecordSetRow;
import at.sume.dm.model.output.Fileable;

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
	 * @param entity
	 */
	public void add(T entity);
	/**
	 * Remove data of a household from the set of indicators
	 * @param entity
	 */
	public void remove(T entity);
	/**
	 * Reset the set of indicators to zero
	 */
	public void clear();
	
	public List<? extends Fileable> getIndicatorList();
}
