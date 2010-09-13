/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import at.sume.db.RecordSetRow;

/**
 * List of spatial units in the model
 * @author Alexander Remesch
 * 
 * TODO: must include properties of each spatial unit, must include geo-coordinates for graphical representation 
 */
public class SpatialUnitRow extends RecordSetRow<SpatialUnits> {
	private double avgRentBlw60;	// Average rent < 60 m² [€/m²]
	private double avgRentAbv60;	// Average rent >= 60 m² [€/m²]
	private double rngRentBlw60;	// Max. range of rent < 60 m² depending on the quality of the dwelling
	private double rngRentAbv60;	// Max. range of rent >= 60 m² depending on the quality of the dwelling
	
	/**
	 * @param rowList
	 */
	public SpatialUnitRow(SpatialUnits rowList) {
		super(rowList);
	}

	/**
	 * @return the spatialUnitId
	 */
	public long getSpatialUnitId() {
		return id;
	}

	/**
	 * @param spatialUnitId the spatialUnitId to set
	 */
	public void setSpatialUnitId(long spatialUnitId) {
		this.id = spatialUnitId;
	}

	/**
	 * @return the avgRentBlw60
	 */
	public double getAvgRentBlw60() {
		return avgRentBlw60;
	}

	/**
	 * @param avgRentBlw60 the avgRentBlw60 to set
	 */
	public void setAvgRentBlw60(double avgRentBlw60) {
		this.avgRentBlw60 = avgRentBlw60;
	}

	/**
	 * @return the avgRentAbv60
	 */
	public double getAvgRentAbv60() {
		return avgRentAbv60;
	}

	/**
	 * @param avgRentAbv60 the avgRentAbv60 to set
	 */
	public void setAvgRentAbv60(double avgRentAbv60) {
		this.avgRentAbv60 = avgRentAbv60;
	}

	/**
	 * @return the rngRentBlw60
	 */
	public double getRngRentBlw60() {
		return rngRentBlw60;
	}

	/**
	 * @param rngRentBlw60 the rngRentBlw60 to set
	 */
	public void setRngRentBlw60(double rngRentBlw60) {
		this.rngRentBlw60 = rngRentBlw60;
	}

	/**
	 * @return the rngRentAbv60
	 */
	public double getRngRentAbv60() {
		return rngRentAbv60;
	}

	/**
	 * @param rngRentAbv60 the rngRentAbv60 to set
	 */
	public void setRngRentAbv60(double rngRentAbv60) {
		this.rngRentAbv60 = rngRentAbv60;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.DatabaseRecord#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("SpatialUnitId")) {
			setSpatialUnitId(rs.getLong(name));
		} else if (name.equals("Miete_Avg_bis60")) {
			setAvgRentBlw60(rs.getDouble(name));
		} else if (name.equals("Miete_Avg_ab60")) {
			setAvgRentAbv60(rs.getDouble(name));
		} else if (name.equals("Miete_Range_bis60")) {
			setRngRentBlw60(rs.getDouble(name));
		} else if (name.equals("Miete_Range_ab60")) {
			setRngRentAbv60(rs.getDouble(name));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#remove()
	 */
	@Override
	public void remove() {
		throw new IllegalArgumentException("SpatialUnitRow.remove() not allowed");
	}
}
