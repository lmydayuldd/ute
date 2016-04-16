/**
 * 
 */
package at.sume.dm.indicators.rows;

import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public class MigrationDetailsRow implements Comparable<MigrationDetailsRow>, Fileable {
	private int spatialUnitIdFrom;
	private int spatialUnitIdTo;
	private HouseholdType householdType;
	private MigrationRealm migrationRealm;
	private int householdCount;
	private int personCount;
	
	public MigrationDetailsRow() {
		spatialUnitIdFrom = 0;
		spatialUnitIdTo = 0;
	}
	/**
	 * @return the spatialUnitIdFrom
	 */
	public int getSpatialUnitIdFrom() {
		return spatialUnitIdFrom;
	}

	/**
	 * @param spatialUnitIdFrom the spatialUnitIdFrom to set
	 */
	public void setSpatialUnitIdFrom(Integer spatialUnitIdFrom) {
		if (spatialUnitIdFrom == null)
			this.spatialUnitIdFrom = 0;
		else
			this.spatialUnitIdFrom = spatialUnitIdFrom;
	}

	/**
	 * @return the spatialUnitIdTo
	 */
	public int getSpatialUnitIdTo() {
		return spatialUnitIdTo;
	}

	/**
	 * @param spatialUnitIdTo the spatialUnitIdTo to set
	 */
	public void setSpatialUnitIdTo(Integer spatialUnitIdTo) {
		if (spatialUnitIdTo == null)
			this.spatialUnitIdTo = 0;
		else
			this.spatialUnitIdTo = spatialUnitIdTo;
	}

	/**
	 * @return the householdType
	 */
	public HouseholdType getHouseholdType() {
		return householdType;
	}

	/**
	 * @param householdType the householdType to set
	 */
	public void setHouseholdType(HouseholdType householdType) {
		this.householdType = householdType;
	}

	/**
	 * @return the migrationRealm
	 */
	public MigrationRealm getMigrationRealm() {
		return migrationRealm;
	}

	/**
	 * @param migrationRealm the migrationRealm to set
	 */
	public void setMigrationRealm(MigrationRealm migrationRealm) {
		this.migrationRealm = migrationRealm;
	}

	/**
	 * @return the householdCount
	 */
	public int getHouseholdCount() {
		return householdCount;
	}

	/**
	 * @param householdCount the householdCount to set
	 */
	public void setHouseholdCount(int householdCount) {
		this.householdCount = householdCount;
	}

	/**
	 * @return the personCount
	 */
	public int getPersonCount() {
		return personCount;
	}

	/**
	 * @param personCount the personCount to set
	 */
	public void setPersonCount(int personCount) {
		this.personCount = personCount;
	}

	@Override
	public int compareTo(MigrationDetailsRow o) {
		int result = ((Integer)spatialUnitIdFrom).compareTo(o.spatialUnitIdFrom);
		if (result != 0)
			return result;
		result = ((Integer)spatialUnitIdTo).compareTo(o.spatialUnitIdTo);
		if (result != 0)
			return result;
		result = migrationRealm.compareTo(o.migrationRealm);
		if (result != 0)
			return result;
		result = householdType.compareTo(o.householdType);
		return result;
	}

	@Override
	public String toCsvHeadline(String delimiter) {
		return "ModelRun" + delimiter + "From" + delimiter + "To" + delimiter + "HouseholdType" + delimiter + "MigrationType" + delimiter + "HouseholdCount" + delimiter + "PersonCount";
	}

	@Override
	public String toString(int modelRun, String delimiter) {
		return modelRun + delimiter + spatialUnitIdFrom + delimiter + spatialUnitIdTo + delimiter + householdType + delimiter + migrationRealm + delimiter + householdCount + delimiter + personCount;
	}
}
