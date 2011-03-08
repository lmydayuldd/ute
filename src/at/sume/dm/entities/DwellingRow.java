/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.remesch.util.Random;

import net.remesch.db.Sequence;
import net.remesch.db.schema.Ignore;
import at.sume.db.RecordSetRowFileable;
import at.sume.dm.Common;
import at.sume.dm.model.residential_mobility.RentPerSpatialUnit;
import at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionDwellingProperties;
import at.sume.dm.types.LivingSpaceGroup6;

/**
 * @author Alexander Remesch
 *
 */
public class DwellingRow extends RecordSetRowFileable<Dwellings> implements ResidentialSatisfactionDwellingProperties {
	private int dwellingId;
	private int spatialunitId;
	@Ignore
	private SpatialUnitRow spatialunit;
	private short dwellingSize;
	private int totalYearlyDwellingCosts;
	private byte livingSpaceGroup6Id;
	private byte costOfResidenceGroupId;
	private byte constructionPeriod7Id;
	@Ignore
	private HouseholdRow household;
	@Ignore
	private static Sequence dwellingIdSeq = null;
	
	public DwellingRow() {
		if (dwellingIdSeq != null) {
			setDwellingId(dwellingIdSeq.getNext());
		}
	}
//	public DwellingRow(Dwellings dwellings) {
//		this.rowList = dwellings;
//	}
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
		calcTotalYearlyDwellingCosts(false);
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
		if (spatialunitId != spatialunit.getSpatialUnitId())
			setSpatialunitId(spatialunit.getSpatialUnitId());
	}

	/**
	 * @return the total living space of the dwelling
	 */
	public short getDwellingSize() {
		return dwellingSize;
	}

	/**
	 * @param i the total living space of the dwelling to set
	 */
	public void setDwellingSize(short i) {
		this.dwellingSize = i;
		calcTotalYearlyDwellingCosts(false);
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
	 * Calculate the total yearly dwelling costs from the dwelling size and the spatial unit
	 * 
	 * @param forceCalculation force calculation of total yearly dwelling costs even if they are != 0
	 */
	public void calcTotalYearlyDwellingCosts(boolean forceCalculation) {
		if (((totalYearlyDwellingCosts == 0) || forceCalculation) && (spatialunitId != 0) && (dwellingSize != 0)) {
			Random r = new Random();
			RentPerSpatialUnit rentPerSpatialUnit = RentPerSpatialUnit.getInstance();
			long yearlyRentPer100Sqm = rentPerSpatialUnit.getYearlyAverageRentPer100Sqm(spatialunitId);
			assert yearlyRentPer100Sqm > 0 : "yearly rent per 100m² must be > 0";
			double dwellingPriceRange = ((double) Common.getDwellingPriceRange()) / 100;
			double randomFactor = r.nextGaussianRange(1);
			yearlyRentPer100Sqm += Math.round(yearlyRentPer100Sqm * randomFactor * dwellingPriceRange);
			int dwellingCosts = Math.round((dwellingSize * yearlyRentPer100Sqm) / 100);
			assert dwellingCosts > 0 : "dwelling costs must be > 0";
			totalYearlyDwellingCosts = dwellingCosts;
		}
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
		dwellingSize = LivingSpaceGroup6.sampleLivingSpace(livingSpaceGroup6Id);
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
	/**
	 * @param dwellingIdSeq the dwellingIdSeq to set
	 */
	public static void setDwellingIdSeq(Sequence dwellingIdSeq) {
		DwellingRow.dwellingIdSeq = dwellingIdSeq;
	}
	@Override
	public String toCsvHeadline(String delimiter) {
		return "DwellingId" + delimiter + "SpatialunitId" + delimiter + "DwellingSize" + delimiter + "TotalYearlyDwellingCosts" + delimiter + "HouseholdId";
	}
	@Override
	public String toString(String delimiter) {
		if (getHousehold() != null) 
			return getDwellingId() + delimiter + getSpatialunit().getSpatialUnitId() + delimiter + getDwellingSize() + delimiter + 
				getTotalYearlyDwellingCosts() + delimiter + getHousehold().getHouseholdId();
		else
			return getDwellingId() + delimiter + getSpatialunit().getSpatialUnitId() + delimiter + getDwellingSize() + delimiter + 
			getTotalYearlyDwellingCosts() + delimiter + "0";
	}
}
