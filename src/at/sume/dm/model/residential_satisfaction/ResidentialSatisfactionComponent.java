/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;

/**
 * @author Alexander Remesch
 *
 */
public class ResidentialSatisfactionComponent {
	/**
	 * Calculate residential satisfaction for the household in the given spatial unit (without
	 * considering dwelling characteristics)
	 * 
	 * @param hh
	 * @param su
	 * @return Residential satisfaction in thousandth part
	 */
	public long calc(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		return (long)1000;
	}
	
	/**
	 * Calculate residential satisfaction for the household in its current spatial unit & dwelling
	 * @param hh
	 * @return Residential satisfaction in thousandth part
	 */
	public final long calc(HouseholdRow hh, int modelYear) {
		return calc(hh, hh.getSpatialunit(), modelYear);
	}
	
	/**
	 * Calculate residential satisfcation for the household for the given dwelling
	 * 
	 * @param hh
	 * @param dwelling
	 * @param modelYear
	 * @return Residential satisfaction in thousandth part
	 */
	public long calc(HouseholdRow hh, DwellingRow dwelling, int modelYear) {
		return calc(hh, dwelling.getSpatialunit(), modelYear);
	}
}
