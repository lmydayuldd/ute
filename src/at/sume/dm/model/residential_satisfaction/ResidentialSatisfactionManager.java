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
	COSTOFRESIDENCESHARE(new CostOfResidenceShare()),
	LIVINGSPACEREQUIREMENT(new LivingSpaceRequirement()),
	ENVIRONMENTALAMENITIES(new EnvironmentalAmenities()),
	UDPCLASSIFICATION(new UDPClassification()),
	LOCATIONFACTORS(new LocationFactors());
	
	private ResidentialSatisfactionComponent component;
	private double weight;

	ResidentialSatisfactionManager(ResidentialSatisfactionComponent component) {
		this.component = component;
		this.weight = 1;
	}
	
	/**
	 * Set the weight for each component of residential satisfaction
	 * @param weight
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	/**
	 * Calculate the residential satisfaction level for a household at its current dwelling
	 * @param hh
	 * @return
	 */
	public double calcResidentialSatisfaction(HouseholdRow hh, int modelYear) {
		return calcResidentialSatisfaction(hh, hh.getSpatialunit(), modelYear);
	}
	
	/**
	 * Calculate the residential satisfaction level for a household in an arbitrary spatial unit 
	 * @param hh
	 * @param su
	 * @return
	 */
	public static double calcResidentialSatisfaction(HouseholdRow hh, SpatialUnitRow su, int modelYear) {
		double rv = 0;
		for (ResidentialSatisfactionManager rs : values()) {
			rv += rs.component.calc(hh, su, modelYear) * rs.weight;
		}
		return rv;
	}
}
