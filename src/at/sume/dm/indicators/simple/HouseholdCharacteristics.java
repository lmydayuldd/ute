/**
 * 
 */
package at.sume.dm.indicators.simple;

import at.sume.dm.types.HouseholdType;

/**
 * @author Alexander Remesch
 *
 */
public interface HouseholdCharacteristics {
	public HouseholdType getHouseholdType();
	public short getHouseholdSize();
}
