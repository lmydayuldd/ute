/**
 * 
 */
package at.sume.dm.model.output;

import at.sume.dm.entities.DwellingRow;

/**
 * @author Alexander Remesch
 *
 */
public class OutputDwellingRow implements OutputRow {
	private short modelYear;
	private int dwellingId;
	private int spatialunitId;
	private short dwellingSize;
	private int totalYearlyDwellingCosts;
	private int householdId;
	
	public OutputDwellingRow(short modelYear, DwellingRow dwelling) {
		this.modelYear = modelYear;
		this.dwellingId = dwelling.getDwellingId();
		this.spatialunitId = dwelling.getSpatialunit().getSpatialUnitId();
		this.dwellingSize = dwelling.getDwellingSize();
		this.totalYearlyDwellingCosts = dwelling.getTotalYearlyDwellingCosts();
		if (dwelling.getHousehold() != null) 
			this.householdId = dwelling.getHousehold().getHouseholdId();
		else
			this.householdId = 0;
	}
	/* (non-Javadoc)
	 * @see at.sume.dm.model.output.OutputRow#toCsv()
	 */
	@Override
	public String toCsv() {
		return modelYear + ";" + dwellingId + ";" + spatialunitId + ";" + dwellingSize + ";" + 
			totalYearlyDwellingCosts + ";" + householdId;
	}

}
