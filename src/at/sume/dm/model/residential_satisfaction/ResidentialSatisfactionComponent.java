/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;

/**
 * @author Alexander Remesch
 *
 */
public class ResidentialSatisfactionComponent {
	/**
	 * Calculate residential satisfaction for the household in the given spatial unit
	 * @param hh
	 * @param su
	 * @return Residential satisfaction in thousandth part
	 */
	public long calc(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		return (long)1000;
	}
	
	/**
	 * Calculate residential satisfaction for the household in its current spatial unit
	 * @param hh
	 * @return Residential satisfaction in thousandth part
	 */
	public final long calc(HouseholdRow hh, int modelYear) {
		return calc(hh, hh.getSpatialunit(), modelYear);
	}
}
