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
	 * @param hh
	 * @return
	 */
	public int calcResidentialSatisfaction(HouseholdRow hh, int modelYear) {
		return calcResidentialSatisfaction(hh, hh.getSpatialunit(), modelYear);
	}
	
	/**
	 * Calculate the residential satisfaction level for a household in an arbitrary spatial unit 
	 * @param hh
	 * @param su
	 * @return Overall residential satisfaction in thousandth part
	 */
	public static int calcResidentialSatisfaction(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		int rv = 0;
		for (ResidentialSatisfactionManager rs : values()) {
			rv += rs.component.calc(hh, su, modelYear) * (rs.weight / 1000);
		}
		return rv / values().length;
		
	}
}
