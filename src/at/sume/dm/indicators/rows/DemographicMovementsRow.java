/**
 * 
 */
package at.sume.dm.indicators.rows;

import at.sume.dm.model.output.Fileable;

/**
 * @author Alexander Remesch
 *
 */
public class DemographicMovementsRow implements Comparable<Integer>, Fileable {
	private int spatialUnitId;
	private int birthCount;
	private int deathCount;
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
	public void addBirthCount() {
		birthCount++;
	}
	public void addDeathCount() {
		deathCount++;
	}
	@Override
	public String toCsvHeadline(String delimiter) {
		return "ModelRun" + delimiter + "SpatialUnit" + delimiter + "BirthCount" + delimiter + "DeathCount";
	}
	@Override
	public String toString(int modelRun, String delimiter) {
		return modelRun + delimiter + spatialUnitId + delimiter + birthCount + delimiter + deathCount;
	}

	@Override
	public int compareTo(Integer spatialUnitId) {
		return ((Integer)this.spatialUnitId).compareTo(spatialUnitId);
	}
}
