/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.remesch.db.schema.Ignore;

import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public class DwellingRow extends RecordSetRow<Dwellings> {
	private int dwellingId;
	private int spatialunitId;
	@Ignore // Ignore is not really necessary here because spatialUnit is private anyway
	private SpatialUnitRow spatialunit;
	private short dwellingSize;
	private int totalYearlyDwellingCosts;
	private byte livingSpaceGroup6Id;
	private byte costOfResidenceGroupId;
	private byte constructionPeriod7Id;
	@Ignore
	private HouseholdRow household;
//	@Ignore
//	private Dwellings dwellings;
	
	public DwellingRow() {
		
	}
//	public DwellingRow(Dwellings dwellings) {
//		this.rowList = dwellings;
//	}
	/**
	 * @return the dwellingId
	 */
	public long getDwellingId() {
		return dwellingId;
	}

	/**
	 * @param dwellingId the dwellingId to set
	 */
	public void setDwellingId(int dwellingId) {
		this.dwellingId = dwellingId;
		this.id = dwellingId;
	}

	/**
	 * @return the spatialunitId
	 */
	public int getSpatialunitId() {
		return spatialunitId;
	}

	/**
	 * @param spatialunitId the spatialunitId to set
	 */
	public void setSpatialunitId(int spatialunitId) {
		this.spatialunitId = spatialunitId;
	}

	/**
	 * @return the spatialunit
	 */
	public SpatialUnitRow getSpatialunit() {
		return spatialunit;
	}

	/**
	 * @param spatialunit the spatialunit to set
	 */
	public void setSpatialunit(SpatialUnitRow spatialunit) {
		this.spatialunit = spatialunit;
	}

	/**
	 * @return the total living space of the dwelling
	 */
	public short getDwellingSize() {
		return dwellingSize;
	}

	/**
	 * @param dwellingSize the total living space of the dwelling to set
	 */
	public void setDwellingSize(short dwellingSize) {
		this.dwellingSize = dwellingSize;
	}

	/**
	 * @return the yearly total costs of the dwelling
	 */
	public int getTotalYearlyDwellingCosts() {
		return totalYearlyDwellingCosts;
	}

	/**
	 * @param totalYearlyDwellingCosts the yearly total costs of the dwelling to set
	 */
	public void setTotalYearlyDwellingCosts(int totalYearlyDwellingCosts) {
		this.totalYearlyDwellingCosts = totalYearlyDwellingCosts;
	}

	/**
	 * @return the livingSpaceGroup6Id
	 */
	public byte getLivingSpaceGroup6Id() {
		return livingSpaceGroup6Id;
	}

	/**
	 * @param livingSpaceGroup6Id the livingSpaceGroup6Id to set
	 */
	public void setLivingSpaceGroup6Id(byte livingSpaceGroup6Id) {
		this.livingSpaceGroup6Id = livingSpaceGroup6Id;
	}

	/**
	 * @return the costOfResidenceGroupId
	 */
	public byte getCostOfResidenceGroupId() {
		return costOfResidenceGroupId;
	}

	/**
	 * @param costOfResidenceGroupId the costOfResidenceGroupId to set
	 */
	public void setCostOfResidenceGroupId(byte costOfResidenceGroupId) {
		this.costOfResidenceGroupId = costOfResidenceGroupId;
	}

	/**
	 * @return the constructionPeriod7Id
	 */
	public byte getConstructionPeriod7Id() {
		return constructionPeriod7Id;
	}

	/**
	 * @param constructionPeriod7Id the constructionPeriod7Id to set
	 */
	public void setConstructionPeriod7Id(byte constructionPeriod7Id) {
		this.constructionPeriod7Id = constructionPeriod7Id;
	}

	/**
	 * @return the household
	 */
	public HouseholdRow getHousehold() {
		return household;
	}

	/**
	 * @param household the household to set
	 */
	public void setHousehold(HouseholdRow household) {
		this.household = household;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#loadFromDatabase(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	@Deprecated
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		// use Common.select() instead
		throw new AssertionError("DwellingRow.loadFromDatabase is depreceated");
	}
}
