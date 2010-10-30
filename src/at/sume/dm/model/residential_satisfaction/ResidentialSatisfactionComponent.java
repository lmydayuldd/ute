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
	 * Calculate residential satisfaction for the household in the given dwelling and spatial unit.
	 * In implementations of this method one should take each relevant parameter from the "lowest" entity
	 * where it is available, e.g. dwelling costs from the dwelling, spatial unit characteristics from the
	 * spatial unit, etc. This ensures, that each component can be individually exchanged with another
	 * component to calculate hypothetical residential satisfaction values.
	 *  
	 * @param household
	 * @param dwelling
	 * @param spatialUnit
	 * @param ModelYear
	 * @return
	 */
	public long calc(HouseholdRow household, DwellingRow dwelling, SpatialUnitRow spatialUnit, int ModelYear) {
		return (long)1000;
	}
	/**
	 * Calculate residential satisfaction for the household in the given spatial unit (without
	 * considering dwelling characteristics or by taking dwelling characteristics to be similar to
	 * the households current dwelling)
	 * 
	 * @param household
	 * @param spatialUnit
	 * @return Residential satisfaction in thousandth part
	 */
	public final long calc(HouseholdRow household, SpatialUnitRow spatialUnit, int modelYear) {
		return calc(household, household.getDwelling(), spatialUnit, modelYear);
	}
	/**
	 * Calculate residential satisfaction for the household in its current spatial unit & dwelling
	 * @param household
	 * @return Residential satisfaction in thousandth part
	 */
	public final long calc(HouseholdRow household, int modelYear) {
		return calc(household, household.getDwelling(), modelYear);
	}
	/**
	 * Calculate residential satisfaction for the household for the given dwelling
	 * 
	 * @param household
	 * @param dwelling
	 * @param modelYear
	 * @return Residential satisfaction in thousandth part
	 */
	public final long calc(HouseholdRow household, DwellingRow dwelling, int modelYear) {
		return calc(household, dwelling.getSpatialunit(), modelYear);
	}
}
