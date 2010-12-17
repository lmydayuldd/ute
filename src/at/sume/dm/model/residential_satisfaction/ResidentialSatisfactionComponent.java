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
	 * @param spatialUnitId
	 * @param ModelYear
	 * @return
	 */
	public short calc(HouseholdRow household, DwellingRow dwelling, SpatialUnitRow spatialUnitId, int ModelYear) {
		return (short)1000;
	}
	/**
	 * Calculate residential satisfaction for the household in the given spatial unit (without
	 * considering dwelling characteristics or by taking dwelling characteristics to be similar to
	 * the households current dwelling)
	 * 
	 * @param household
	 * @param spatialUnitId
	 * @return Residential satisfaction in thousandth part
	 */
	public final short calc(HouseholdRow household, SpatialUnitRow spatialUnitId, int modelYear) {
		return calc(household, household.getDwelling(), spatialUnitId, modelYear);
	}
	/**
	 * Calculate residential satisfaction for the household in its current spatial unit & dwelling
	 * @param household
	 * @return Residential satisfaction in thousandth part
	 */
	public final short calc(HouseholdRow household, int modelYear) {
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
	public final short calc(HouseholdRow household, DwellingRow dwelling, int modelYear) {
		return calc(household, dwelling, dwelling.getSpatialunit(), modelYear);
	}
}
