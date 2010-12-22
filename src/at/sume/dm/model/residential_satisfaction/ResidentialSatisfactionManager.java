/**
 * 
 */
package at.sume.dm.model.residential_satisfaction;

import java.util.ArrayList;

import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.SpatialUnitRow;

/**
 * @author Alexander Remesch
 *
 */
public enum ResidentialSatisfactionManager {
	SOCIALPRESTIGE(new SocialPrestige()),
	COSTEFFECTIVENESS(new CostEffectiveness(), ResidentialSatisfactionWeight.getPrefCosts()),
	DESIREDLIVINGSPACE(new DesiredLivingSpace(), ResidentialSatisfactionWeight.getPrefLivingSpace()),
	ENVIRONMENTALAMENITIES(new EnvironmentalAmenities(), ResidentialSatisfactionWeight.getPrefEnvAmen()),
	UDPCENTRALITY(new UDPCentrality(), ResidentialSatisfactionWeight.getPrefCentrality()),
	UDPTRANSPORT(new UDPPublicTransportAccessibility(), ResidentialSatisfactionWeight.getPrefTransportAccess());

	private ResidentialSatisfactionComponent component;
	private ArrayList<Short> weightList;
	private static short maxWeight = 4;

	ResidentialSatisfactionManager(ResidentialSatisfactionComponent component) {
		this.component = component;
		this.weightList = null;
	}
	
	ResidentialSatisfactionManager(ResidentialSatisfactionComponent component, ArrayList<Short> weightList) {
		this.component = component;
		this.weightList = weightList;
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
	public static short calcResidentialSatisfaction(HouseholdRow household, int modelYear) {
		return calcResidentialSatisfaction(household, household.getDwelling(), modelYear);
	}
	/**
	 * Calculate the residential satisfaction level for a household in an arbitrary spatial unit.
	 * Take the necessary dwelling characteristics from the current dwelling of the household and
	 * pretend it is in the given spatial unit.
	 *  
	 * @param household
	 * @param dwelling
	 * @return Overall residential satisfaction in thousandth part
	 */
	public static short calcResidentialSatisfaction(HouseholdRow household, SpatialUnitRow spatialUnit, int modelYear) {
		long rv = 0;
		short weight = maxWeight;
		int weightSum = 0;
		for (ResidentialSatisfactionManager rs : values()) {
			if (rs.weightList != null)
				weight = rs.weightList.get(household.getHouseholdType().getId() - 1);
			if (weight == 0)
				continue;
			int value = rs.component.calc(household, spatialUnit, modelYear) * weight;
			rv += value;
			weightSum += weight;
		}
		long result = rv / weightSum;
		assert (result >= 0) && (result <= 32767) : "Residential satisfaction out of range (" + result + ")";
		return (short) result;
	}
	/**
	 * Calculate the residential satisfaction level for a household in an arbitrary dwelling
	 *  
	 * @param household
	 * @param dwelling
	 * @return Overall residential satisfaction in thousandth part
	 */
	public static short calcResidentialSatisfaction(HouseholdRow household, DwellingRow dwelling, int modelYear) {
		long rv = 0;
		short weight = maxWeight;
		int weightSum = 0;
		for (ResidentialSatisfactionManager rs : values()) {
			if (rs.weightList != null)
				weight = rs.weightList.get(household.getHouseholdType().getId() - 1);
			if (weight == 0)
				continue;
			int value = rs.component.calc(household, dwelling, modelYear) * weight;
			rv += value;
			weightSum += weight;
		}
		long result = rv / weightSum;
		assert (result >= 0) && (result <= 32767) : "Residential satisfaction out of range (" + result + ")";
		return (short) result;
	}
	public ResidentialSatisfactionComponent getComponent() {
		return component;
	}
}
