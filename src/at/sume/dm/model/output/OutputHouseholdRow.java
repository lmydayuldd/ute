/**
 * 
 */
package at.sume.dm.model.output;

import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.types.HouseholdType;

/**
 * @author Alexander Remesch
 *
 */
public class OutputHouseholdRow implements OutputRow {
	private short modelYear;
	private int householdId;
	private byte householdSize;
	private int dwellingId;
	private byte householdType;
	private short movingDecisionYear;
	private short aspirationRegionLivingSpaceMin;
	private short aspirationRegionLivingSpaceMax;
	private int aspirationRegionMaxCosts;
	private short currentResidentialSatisfaction;
	private short rsUdp;
	private short rsCostEffectiveness;
	private short rsEnvironmentalAmenities;
	private short rsSocialPrestige;
	private short rsDesiredLivingSpace;
	
	public OutputHouseholdRow(short modelYear, HouseholdRow household) {
		this.modelYear = modelYear;
		this.householdId = household.getHouseholdId();
		this.householdSize = (byte) household.getMemberCount();
		this.dwellingId = household.getDwelling().getDwellingId();
		this.householdType = HouseholdType.getId(household.getHouseholdType());
		this.movingDecisionYear = household.getMovingDecisionYear();
		this.aspirationRegionLivingSpaceMin = household.getAspirationRegionLivingSpaceMin();
		this.aspirationRegionLivingSpaceMax = household.getAspirationRegionLivingSpaceMax();
		this.currentResidentialSatisfaction = household.getCurrentResidentialSatisfaction();
		this.rsUdp = household.rsUdp;
		this.rsCostEffectiveness = household.rsCostEffectiveness;
		this.rsEnvironmentalAmenities = household.rsEnvironmentalAmenities;
		this.rsSocialPrestige = household.rsSocialPrestige;
		this.rsDesiredLivingSpace = household.rsDesiredLivingSpace;
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.OutputRow#toCsv()
	 */
	@Override
	public String toCsv() {
		return modelYear + ";" + householdId + ";" + householdSize + ";" + dwellingId + ";" + 
			householdType + ";" + movingDecisionYear + ";" + aspirationRegionLivingSpaceMin + ";" +
			aspirationRegionLivingSpaceMax + ";" + aspirationRegionMaxCosts + ";" + currentResidentialSatisfaction + ";" +
			rsUdp + ";" + rsCostEffectiveness + ";" + rsEnvironmentalAmenities + ";" + rsSocialPrestige + ";" + rsDesiredLivingSpace;
	}
}
