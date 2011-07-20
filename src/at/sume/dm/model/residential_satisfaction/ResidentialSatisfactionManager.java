/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import java.util.ArrayList;

import at.sume.dm.entities.SpatialUnitRow;

/**
 * @author Alexander Remesch
 *
 */
public enum ResidentialSatisfactionManager {
	SOCIALPRESTIGE(new SocialPrestige(), ResidentialSatisfactionWeight.getInstance().getPrefSocialPrestige(), false),
	COSTEFFECTIVENESS(new CostEffectiveness(), ResidentialSatisfactionWeight.getInstance().getPrefCosts(), true),
	DESIREDLIVINGSPACE(new DesiredLivingSpace(), ResidentialSatisfactionWeight.getInstance().getPrefLivingSpace(), true),
	ENVIRONMENTALAMENITIES(new EnvironmentalAmenities(), ResidentialSatisfactionWeight.getInstance().getPrefEnvAmen(), true),
	UDPCENTRALITY(UDPCentrality.getInstance(), ResidentialSatisfactionWeight.getInstance().getPrefCentrality(), true),
	UDPTRANSPORT(UDPPublicTransportAccessibility.getInstance(), ResidentialSatisfactionWeight.getInstance().getPrefTransportAccess(), true);

	private ResidentialSatisfactionComponent component;
	private ArrayList<Short> weightList;
	private static short maxWeight = 4;
	private boolean calcForFreeDwellingsAlwaysAvailable;
	/**
	 * 
	 * @param component
	 * @param calcForFreeDwellingsAlwaysAvailable Calculate this part of residential satisfaction for spatial units where dwellings are not managed (= always available)
	 */
	ResidentialSatisfactionManager(ResidentialSatisfactionComponent component, boolean calcForFreeDwellingsAlwaysAvailable) {
		this.component = component;
		this.weightList = null;
		this.calcForFreeDwellingsAlwaysAvailable = calcForFreeDwellingsAlwaysAvailable;
	}
	/**
	 * 
	 * @param component
	 * @param weightList
	 * @param calcForFreeDwellingsAlwaysAvailable Calculate this part of residential satisfaction for spatial units where dwellings are not managed (= always available)
	 */
	ResidentialSatisfactionManager(ResidentialSatisfactionComponent component, ArrayList<Short> weightList, boolean calcForFreeDwellingsAlwaysAvailable) {
		this.component = component;
		this.weightList = weightList;
		this.calcForFreeDwellingsAlwaysAvailable = calcForFreeDwellingsAlwaysAvailable;
	}
	/**
	 * Set the weight for each component of residential satisfaction
	 * @param weightList
	 */
	public void setWeight(ArrayList<Short> weightList) {
		this.weightList = weightList;
	}
	
	/**
	 * Calculate the residential satisfaction level for a household at its current dwelling
	 * @param household
	 * @return
	 */
	public static short calcResidentialSatisfaction(ResidentialSatisfactionHouseholdProperties household, int modelYear) {
		return calcResidentialSatisfaction(household, household.getDwelling(), modelYear);
	}
	/**
	 * Calculate the residential satisfaction level for a household in an arbitrary spatial unit.
	 * Take the necessary dwelling characteristics from the current dwelling of the household and
	 * pretend it is in the given spatial unit.
	 *  
	 * @param household
	 * @param dwelling
	 * @return Overall residential satisfaction in thousandth part ranging from 0 to 1000. A value of -1 indicates that the household shall not be included in any activity depending on residential satisfaction calculation.
	 */
	public static short calcResidentialSatisfaction(ResidentialSatisfactionHouseholdProperties household, SpatialUnitRow spatialUnit, int modelYear) {
		long rv = 0;
		short weight = maxWeight;
		int weightSum = 0;
		for (ResidentialSatisfactionManager rs : values()) {
			if (!rs.calcForFreeDwellingsAlwaysAvailable && spatialUnit.isFreeDwellingsAlwaysAvailable())
				continue;
			if (rs.weightList != null)
				weight = rs.weightList.get(household.getHouseholdType().getId() - 1);
			if (weight == 0)
				continue;
			int value = rs.component.calc(household, spatialUnit, modelYear) * weight;
			rv += value;
			weightSum += weight;
		}
		long result = 0;
		if (weightSum != 0)
			result = rv / weightSum;
		else
			return -1; // don't include household in any activity depending on residential satisfaction calculation
		assert (result >= 0) && (result <= 32767) : "Residential satisfaction out of range (" + result + ")";
		return (short) result;
	}
	/**
	 * Calculate the residential satisfaction level for a household in an arbitrary dwelling
	 *  
	 * @param household
	 * @param dwelling
	 * @return Overall residential satisfaction in thousandth part ranging from 0 to 1000. A value of -1 indicates that the household shall not be included in any activity depending on residential satisfaction calculation.
	 */
	public static short calcResidentialSatisfaction(ResidentialSatisfactionHouseholdProperties household, ResidentialSatisfactionDwellingProperties dwelling, int modelYear) {
		long rv = 0;
		short weight = maxWeight;
		int weightSum = 0;
		for (ResidentialSatisfactionManager rs : values()) {
			// isFreeDwellingsAlwaysAvailable() is ignored here since we always have a dwelling at this point
			if (rs.weightList != null)
				weight = rs.weightList.get(household.getHouseholdType().getId() - 1);
			if (weight == 0)
				continue;
			int value = rs.component.calc(household, dwelling, modelYear) * weight;
			rv += value;
			weightSum += weight;
		}
		long result = 0;
		if (weightSum != 0)
			result = rv / weightSum;
		else
			return -1; // don't include household in any activity depending on residential satisfaction calculation
		assert (result >= 0) && (result <= 32767) : "Residential satisfaction out of range (" + result + ")";
		return (short) result;
	}
	public ResidentialSatisfactionComponent getComponent() {
		return component;
	}
}
