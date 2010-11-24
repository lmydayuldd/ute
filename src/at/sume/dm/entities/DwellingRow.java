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
	private long dwellingId;
	private long spatialunitId;
	@Ignore // Ignore is not really necessary here because spatialUnit is private anyway
	private SpatialUnitRow spatialunit;
	private int dwellingSize;
	private long dwellingCosts;
	private short livingSpaceGroup6Id;
	private short costOfResidenceGroupId;
	private short constructionPeriod7Id;
	@Ignore
	private HouseholdRow household;
	@Ignore
	private Dwellings dwellings;
	
	public DwellingRow() {
		
	}
	public DwellingRow(Dwellings dwellings) {
		this.dwellings = dwellings;
	}
	/**
	 * @return the dwellingId
	 */
	public long getDwellingId() {
		return dwellingId;
	}

	/**
	 * @param dwellingId the dwellingId to set
	 */
	public void setDwellingId(long dwellingId) {
		this.dwellingId = dwellingId;
		this.id = dwellingId;
	}

	/**
	 * @return the spatialunitId
	 */
	public long getSpatialunitId() {
		return spatialunitId;
	}

	/**
	 * @param spatialunitId the spatialunitId to set
	 */
	public void setSpatialunitId(long spatialunitId) {
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
	public int getDwellingSize() {
		return dwellingSize;
	}

	/**
	 * @param dwellingSize the total living space of the dwelling to set
	 */
	public void setDwellingSize(int dwellingSize) {
		this.dwellingSize = dwellingSize;
	}

	/**
	 * @return the yearly total costs of the dwelling
	 */
	public long getDwellingCosts() {
		return dwellingCosts;
	}

	/**
	 * @param dwellingCosts the yearly total costs of the dwelling to set
	 */
	public void setDwellingCosts(long dwellingCosts) {
		this.dwellingCosts = dwellingCosts;
	}

	/**
	 * @return the livingSpaceGroup6Id
	 */
	public short getLivingSpaceGroup6Id() {
		return livingSpaceGroup6Id;
	}

	/**
	 * @param livingSpaceGroup6Id the livingSpaceGroup6Id to set
	 */
	public void setLivingSpaceGroup6Id(short livingSpaceGroup6Id) {
		this.livingSpaceGroup6Id = livingSpaceGroup6Id;
	}

	/**
	 * @return the costOfResidenceGroupId
	 */
	public short getCostOfResidenceGroupId() {
		return costOfResidenceGroupId;
	}

	/**
	 * @param costOfResidenceGroupId the costOfResidenceGroupId to set
	 */
	public void setCostOfResidenceGroupId(short costOfResidenceGroupId) {
		this.costOfResidenceGroupId = costOfResidenceGroupId;
	}

	/**
	 * @return the constructionPeriod7Id
	 */
	public short getConstructionPeriod7Id() {
		return constructionPeriod7Id;
	}

	/**
	 * @param constructionPeriod7Id the constructionPeriod7Id to set
	 */
	public void setConstructionPeriod7Id(short constructionPeriod7Id) {
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
