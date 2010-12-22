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
public enum ResidentialSatisfactionManager {
	SOCIALPRESTIGE(new SocialPrestige()),
	COSTEFFECTIVENESS(new CostEffectiveness()),
	DESIREDLIVINGSPACE(new DesiredLivingSpace()),
	ENVIRONMENTALAMENITIES(new EnvironmentalAmenities()),
	UDPCLASSIFICATION(new UDPClassification());
	
	private ResidentialSatisfactionComponent component;
	private int weight;

	ResidentialSatisfactionManager(ResidentialSatisfactionComponent component) {
		this.component = component;
		this.weight = 1000;
	}
	
	/**
	 * Set the weight for each component of residential satisfaction
	 * @param weight
	 */
	public void setWeight(int weight) {
		this.weight = weight;
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
		int rv = 0;
		for (ResidentialSatisfactionManager rs : values()) {
			int value = rs.component.calc(household, spatialUnit, modelYear) * (rs.weight / 1000);
			String componentName = rs.component.getClass().getName();
			if (componentName.equals("at.sume.dm.model.residential_satisfaction.SocialPrestige")) {
				household.rsSocialPrestige = (short) value;
			} else if (componentName.equals("at.sume.dm.model.residential_satisfaction.CostEffectiveness")) {
				household.rsCostEffectiveness = (short) value;
			} else if (componentName.equals("at.sume.dm.model.residential_satisfaction.DesiredLivingSpace")) {
				household.rsDesiredLivingSpace = (short) value;
			} else if (componentName.equals("at.sume.dm.model.residential_satisfaction.EnvironmentalAmenities")) {
				household.rsEnvironmentalAmenities = (short) value;
			} else if (componentName.equals("at.sume.dm.model.residential_satisfaction.UDPClassification")) {
				household.rsUdp = (short) value;
			} else {
				throw new AssertionError("Invalid component " + componentName);
			}
			rv += value;
		}
		return (short) (rv / values().length);
	}
	/**
	 * Calculate the residential satisfaction level for a household in an arbitrary dwelling
	 *  
	 * @param household
	 * @param dwelling
	 * @return Overall residential satisfaction in thousandth part
	 */
	public static short calcResidentialSatisfaction(HouseholdRow household, DwellingRow dwelling, int modelYear) {
		int rv = 0;
		for (ResidentialSatisfactionManager rs : values()) {
			int value = rs.component.calc(household, dwelling, modelYear) * (rs.weight / 1000);
			String componentName = rs.component.getClass().getName();
			if (componentName.equals("at.sume.dm.model.residential_satisfaction.SocialPrestige")) {
				household.rsSocialPrestige = (short) value;
			} else if (componentName.equals("at.sume.dm.model.residential_satisfaction.CostEffectiveness")) {
				household.rsCostEffectiveness = (short) value;
			} else if (componentName.equals("at.sume.dm.model.residential_satisfaction.DesiredLivingSpace")) {
				household.rsDesiredLivingSpace = (short) value;
			} else if (componentName.equals("at.sume.dm.model.residential_satisfaction.EnvironmentalAmenities")) {
				household.rsEnvironmentalAmenities = (short) value;
			} else if (componentName.equals("at.sume.dm.model.residential_satisfaction.UDPClassification")) {
				household.rsUdp = (short) value;
			} else {
				throw new AssertionError("Invalid component " + componentName);
			}
			rv += value;
		}
		return (short) (rv / values().length);
	}
	public ResidentialSatisfactionComponent getComponent() {
		return component;
	}
}
