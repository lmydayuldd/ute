/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.db.RecordSetRow;

/**
 * List of spatial units in the model including information about the area size and land use.
 * This information was provided by GIS data mainly from the �IR.
 * 
 * @author Alexander Remesch
 */
public class SpatialUnitRow extends RecordSetRow<SpatialUnits> {
	private long spatialUnitId;
	private double totalArea;
	private short areaShareContinousAndDiscontinousUrbanFabric;
	private short areaShareIndustrialCommercialConstructionInfrastructure;
	private short areaShareArtificialVegetation;
	private short areaShareAgricultural;
	private short areaShareForest;
	private short areaShareWater;
	
	/**
	 * @param rowList
	 */
	public SpatialUnitRow() {
		super();
	}

	/**
	 * @return the spatialUnitId
	 */
	public long getSpatialUnitId() {
		return spatialUnitId;
	}

	/**
	 * @param spatialUnitId the spatialUnitId to set
	 */
	public void setSpatialUnitId(long spatialUnitId) {
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
	public short getAreaShareContinousAndDiscontinousUrbanFabric() {
		return areaShareContinousAndDiscontinousUrbanFabric;
	}

	/**
	 * @param areaShareContinousAndDiscontinousUrbanFabric the areaShareContinousAndDiscontinousUrbanFabric to set
	 */
	public void setAreaShareContinousAndDiscontinousUrbanFabric(
			short areaShareContinousAndDiscontinousUrbanFabric) {
		this.areaShareContinousAndDiscontinousUrbanFabric = areaShareContinousAndDiscontinousUrbanFabric;
	}

	/**
	 * @return the areaShareIndustrialCommercialConstructionInfrastructure
	 */
	public short getAreaShareIndustrialCommercialConstructionInfrastructure() {
		return areaShareIndustrialCommercialConstructionInfrastructure;
	}

	/**
	 * @param areaShareIndustrialCommercialConstructionInfrastructure the areaShareIndustrialCommercialConstructionInfrastructure to set
	 */
	public void setAreaShareIndustrialCommercialConstructionInfrastructure(
			short areaShareIndustrialCommercialConstructionInfrastructure) {
		this.areaShareIndustrialCommercialConstructionInfrastructure = areaShareIndustrialCommercialConstructionInfrastructure;
	}

	/**
	 * @return the areaShareArtificialVegetation
	 */
	public short getAreaShareArtificialVegetation() {
		return areaShareArtificialVegetation;
	}

	/**
	 * @param areaShareArtificialVegetation the areaShareArtificialVegetation to set
	 */
	public void setAreaShareArtificialVegetation(short areaShareArtificialVegetation) {
		this.areaShareArtificialVegetation = areaShareArtificialVegetation;
	}

	/**
	 * @return the areaShareAgricultural
	 */
	public short getAreaShareAgricultural() {
		return areaShareAgricultural;
	}

	/**
	 * @param areaShareAgricultural the areaShareAgricultural to set
	 */
	public void setAreaShareAgricultural(short areaShareAgricultural) {
		this.areaShareAgricultural = areaShareAgricultural;
	}

	/**
	 * @return the areaShareForest
	 */
	public short getAreaShareForest() {
		return areaShareForest;
	}

	/**
	 * @param areaShareForest the areaShareForest to set
	 */
	public void setAreaShareForest(short areaShareForest) {
		this.areaShareForest = areaShareForest;
	}

	/**
	 * @return the areaShareWater
	 */
	public short getAreaShareWater() {
		return areaShareWater;
	}

	/**
	 * @param areaShareWater the areaShareWater to set
	 */
	public void setAreaShareWater(short areaShareWater) {
		this.areaShareWater = areaShareWater;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.DatabaseRecord#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	@Deprecated
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("SpatialUnitId")) {
			setSpatialUnitId(rs.getLong(name));
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
