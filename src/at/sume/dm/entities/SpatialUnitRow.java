/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.db.RecordSetRow;
import at.sume.dm.tracing.ObjectSource;
import net.remesch.db.schema.Ignore;

/**
 * List of spatial units in the model including information about the area size and land use.
 * This information was provided by GIS data mainly from the ÖIR.
 * 
 * @author Alexander Remesch
 */
public class SpatialUnitRow extends RecordSetRow<SpatialUnits> {
	private int spatialUnitId;
	private double totalArea;
	private byte areaShareContinousAndDiscontinousUrbanFabric;
	private byte areaShareIndustrialCommercialConstructionInfrastructure;
	private byte areaShareArtificialVegetation;
	private byte areaShareAgricultural;
	private byte areaShareForest;
	private byte areaShareWater;
	private boolean freeDwellingsAlwaysAvailable;
	@Ignore
	private ObjectSource src;
	
	/**
	 * @param src 
	 * @param rowList
	 */
	public SpatialUnitRow() {
		super();
		this.src = ObjectSource.INIT;
	}

	/**
	 * @param src 
	 * @param rowList
	 */
	public SpatialUnitRow(ObjectSource src) {
		super();
		this.src = src;
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
		this.id = spatialUnitId;
	}

	/**
	 * @return the totalArea
	 */
	public double getTotalArea() {
		return totalArea;
	}

	/**
	 * @param totalArea the totalArea to set
	 */
	public void setTotalArea(double totalArea) {
		this.totalArea = totalArea;
	}

	/**
	 * @return the areaShareContinousAndDiscontinousUrbanFabric
	 */
	public byte getAreaShareContinousAndDiscontinousUrbanFabric() {
		return areaShareContinousAndDiscontinousUrbanFabric;
	}

	/**
	 * @param areaShareContinousAndDiscontinousUrbanFabric the areaShareContinousAndDiscontinousUrbanFabric to set
	 */
	public void setAreaShareContinousAndDiscontinousUrbanFabric(
			byte areaShareContinousAndDiscontinousUrbanFabric) {
		this.areaShareContinousAndDiscontinousUrbanFabric = areaShareContinousAndDiscontinousUrbanFabric;
	}

	/**
	 * @return the areaShareIndustrialCommercialConstructionInfrastructure
	 */
	public byte getAreaShareIndustrialCommercialConstructionInfrastructure() {
		return areaShareIndustrialCommercialConstructionInfrastructure;
	}

	/**
	 * @param areaShareIndustrialCommercialConstructionInfrastructure the areaShareIndustrialCommercialConstructionInfrastructure to set
	 */
	public void setAreaShareIndustrialCommercialConstructionInfrastructure(
			byte areaShareIndustrialCommercialConstructionInfrastructure) {
		this.areaShareIndustrialCommercialConstructionInfrastructure = areaShareIndustrialCommercialConstructionInfrastructure;
	}

	/**
	 * @return the areaShareArtificialVegetation
	 */
	public byte getAreaShareArtificialVegetation() {
		return areaShareArtificialVegetation;
	}

	/**
	 * @param areaShareArtificialVegetation the areaShareArtificialVegetation to set
	 */
	public void setAreaShareArtificialVegetation(byte areaShareArtificialVegetation) {
		this.areaShareArtificialVegetation = areaShareArtificialVegetation;
	}

	/**
	 * @return the areaShareAgricultural
	 */
	public byte getAreaShareAgricultural() {
		return areaShareAgricultural;
	}

	/**
	 * @param areaShareAgricultural the areaShareAgricultural to set
	 */
	public void setAreaShareAgricultural(byte areaShareAgricultural) {
		this.areaShareAgricultural = areaShareAgricultural;
	}

	/**
	 * @return the areaShareForest
	 */
	public byte getAreaShareForest() {
		return areaShareForest;
	}

	/**
	 * @param areaShareForest the areaShareForest to set
	 */
	public void setAreaShareForest(byte areaShareForest) {
		this.areaShareForest = areaShareForest;
	}

	/**
	 * @return the areaShareWater
	 */
	public byte getAreaShareWater() {
		return areaShareWater;
	}

	/**
	 * @param areaShareWater the areaShareWater to set
	 */
	public void setAreaShareWater(byte areaShareWater) {
		this.areaShareWater = areaShareWater;
	}

	/**
	 * @param freeDwellingsAlwaysAvailable the freeDwellingsAlwaysAvailable to set
	 */
	public void setFreeDwellingsAlwaysAvailable(boolean freeDwellingsAlwaysAvailable) {
		this.freeDwellingsAlwaysAvailable = freeDwellingsAlwaysAvailable;
	}

	/**
	 * @return the freeDwellingsAlwaysAvailable
	 */
	public boolean isFreeDwellingsAlwaysAvailable() {
		return freeDwellingsAlwaysAvailable;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.DatabaseRecord#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	@Deprecated
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("SpatialUnitId")) {
			setSpatialUnitId(rs.getInt(name));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#remove()
	 */
	@Override
	@Deprecated
	public void remove() {
		throw new IllegalArgumentException("SpatialUnitRow.remove() not allowed");
	}

//	/* (non-Javadoc)
//	 * @see at.sume.db.RecordSetRow#compareTo(at.sume.db.RecordSetRow)
//	 */
//	@Override
//	public int compareTo(RecordSetRow<SpatialUnits> row) {
//		return ((Long) spatialUnitId).compareTo(((SpatialUnitRow)row).getSpatialUnitId());
//	}
}
