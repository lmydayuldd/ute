/**
 * 
 */
package at.sume.sampling.entities;


/**
 * @author Alexander Remesch
 *
 */
public class DbHouseholdRow {
	private int householdId;
	private int spatialUnitId;		// do we need that really? (should be part of dwelling)
	private int dwellingId;
	private short livingSpace;		// actually part of the dwelling but sampled here
	private int costOfResidence;	// sample this here, put last cost of residence in DwellingRow to be able
									// to calculate costs of dwelling for the next household
	private short residentialSatisfactionThreshMod;
	private byte householdSize;		// only for ease-of-use, not really needed
	
	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
	}

	/**
	 * @return the householdId
	 */
	public int getHouseholdId() {
		return householdId;
	}

	/**
	 * @return the spatialUnitId
	 */
	public int getSpatialUnitId() {
		return spatialUnitId;
	}

	/**
	 * @param spatialUnitId the spatialUnitId to set
	 */
	public void setSpatialUnitId(int spatialUnitId) {
		this.spatialUnitId = spatialUnitId;
	}

	/**
	 * @return the dwellingId
	 */
	public int getDwellingId() {
		return dwellingId;
	}

	/**
	 * @param dwellingId the dwellingId to set
	 */
	public void setDwellingId(int dwellingId) {
		this.dwellingId = dwellingId;
	}

	/**
	 * @return the livingSpace
	 */
	public short getLivingSpace() {
		return livingSpace;
	}

	/**
	 * @param livingSpace the livingSpace to set
	 */
	public void setLivingSpace(short livingSpace) {
		this.livingSpace = livingSpace;
	}

	/**
	 * @return the costOfResidence
	 */
	public int getCostOfResidence() {
		return costOfResidence;
	}

	/**
	 * @param costOfResidence the costOfResidence to set
	 */
	public void setCostOfResidence(int costOfResidence) {
		this.costOfResidence = costOfResidence;
	}

	/**
	 * @return the residentialSatisfactionThreshMod
	 */
	public short getResidentialSatisfactionThreshMod() {
		return residentialSatisfactionThreshMod;
	}

	/**
	 * @param residentialSatisfactionThreshMod the residentialSatisfactionThreshMod to set
	 */
	public void setResidentialSatisfactionThreshMod(
			short residentialSatisfactionThreshMod) {
		this.residentialSatisfactionThreshMod = residentialSatisfactionThreshMod;
	}

	/**
	 * @return the householdSize
	 */
	public byte getHouseholdSize() {
		return householdSize;
	}

	/**
	 * @param householdSize the householdSize to set
	 */
	public void setHouseholdSize(byte householdSize) {
		this.householdSize = householdSize;
	}
}
