/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.SpatialUnitRow;

/**
 * Calculate the environmental amenities dimension of residential satisfaction based on the share of green area in the
 * target spatial unit and the respective household preferences
 *  
 * @author Alexander Remesch
 *
 */
public class EnvironmentalAmenities extends ResidentialSatisfactionComponent {
	/* (non-Javadoc)
	 * @see at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionComponent#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.DwellingRow, at.sume.dm.entities.SpatialUnitRow, int)
	 */
	@Override
	public short calc(ResidentialSatisfactionHouseholdProperties household, ResidentialSatisfactionDwellingProperties dwelling,
			SpatialUnitRow spatialUnit, int ModelYear) {
		int envAmenitiesSum = spatialUnit.getAreaShareArtificialVegetation() + spatialUnit.getAreaShareAgricultural() +
			spatialUnit.getAreaShareForest() + spatialUnit.getAreaShareWater();
		int result = envAmenitiesSum * 10;
		if (result > 1000)
			return 1000;
		assert result >= 0 : "Environmental amenities score is out of range: " + result;
		household.setRsEnvironmentalAmenities((short) result);
		return (short) result;
	}
}
