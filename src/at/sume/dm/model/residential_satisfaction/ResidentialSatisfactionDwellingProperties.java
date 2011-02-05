package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.SpatialUnitRow;

/**
 * This interface collects all methods of the DwellingRow object that are needed in order to calculate
 * the residential satisfaction for a household.
 * 
 * The purpose is to decouple the DwellingRow object from residential satisfaction calculation a little bit
 * and to document the needed dwelling properties in that process.
 * 
 * At some point this inteface should be replaced by a class that is part of DwellingRow and contains everything
 * thats necessary for residential satisfaction calculation. Currently this is not easy to achieve because
 * Database.select() which is used to load all households from the database won't be able to handle such a 
 * "decorated" class.
 * 
 * @author Alexander Remesch
 */
public interface ResidentialSatisfactionDwellingProperties {
	public SpatialUnitRow getSpatialunit();
	public int getTotalYearlyDwellingCosts();
	public short getDwellingSize();
}
