package at.sume.dm.model.residential_satisfaction;

import at.sume.dm.entities.DwellingRow;
import at.sume.dm.types.HouseholdType;

/**
 * This interface collects all methods of the HouseholdRow object that are needed in order to calculate
 * the residential satisfaction for a household.
 * 
 * The purpose is to decouple the HouseholdRow object from residential satisfaction calculation a little bit
 * and to document the needed household properties in that process.
 * 
 * At some point this inteface should be replaced by a class that is part of HouseholdRow and contains everything
 * thats necessary for residential satisfaction calculation. Currently this is not easy to achieve because
 * Database.select() which is used to load all households from the database won't be able to handle such a 
 * "decorated" class.
 * 
 * @author Alexander Remesch
 */
public interface ResidentialSatisfactionHouseholdProperties {
	public void setRsCostEffectiveness(short rsCostEffectiveness);
	public void setRsDesiredLivingSpace(short rsDesiredLivingSpace);
	public void setRsEnvironmentalAmenities(short rsEnvironmentalAmenities);
	public void setRsSocialPrestige(short rsSocialPrestige);
	public void setRsUdpCentrality(short rsUdpCentrality);
	public void setRsUdpPublicTransportAccessibility(short rsUdpPublicTransportAccessibility);
	public DwellingRow getDwelling();
	public boolean hasDwelling();
	public void estimateDesiredLivingSpace();
	public short getAspirationRegionLivingSpaceMin();
	public short getAspirationRegionLivingSpaceMax();
	public int getAspirationRegionMaxCosts();
	public long getYearlyIncomePerMemberWeighted();
	public HouseholdType getHouseholdType();
}
