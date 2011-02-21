/**
 * 
 */
package at.sume.dm.indicators.rows;

import at.sume.dm.model.output.Fileable;
import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public class MigrationPerSpatialUnitRow implements Comparable<Integer>, Fileable {
	private int spatialUnitId;
	private int householdLocalImmigrationCount;
	private int householdNationalImmigrationCount;
	private int householdIntlImmigrationCount;
	private int personLocalImmigrationCount;
	private int personNationalImmigrationCount;
	private int personIntlImmigrationCount;
	private int householdLocalEmigrationCount;
	private int householdNationalEmigrationCount;
	private int householdIntlEmigrationCount;
	private int personLocalEmigrationCount;
	private int personNationalEmigrationCount;
	private int personIntlEmigrationCount;
	
	public void setSpatialUnidId(int spatialUnitId) {
		this.spatialUnitId = spatialUnitId;
	}
	public int getSpatialUnitId() {
		return spatialUnitId;
	}
	public void addImmigratingHousehold(byte householdMemberCount, MigrationRealm migrationRealm) {
		switch (migrationRealm) {
		case LOCAL:
			householdLocalImmigrationCount++;
			personLocalImmigrationCount += householdMemberCount;
			break;
		case NATIONAL:
			householdNationalImmigrationCount++;
			personNationalImmigrationCount += householdMemberCount;
			break;
		case INTERNATIONAL:
			householdIntlImmigrationCount++;
			personIntlImmigrationCount += householdMemberCount;
			break;
		}
	}
	public void addEmigratingHousehold(byte householdMemberCount, MigrationRealm migrationRealm) {
		switch (migrationRealm) {
		case LOCAL:
			householdLocalEmigrationCount++;
			personLocalEmigrationCount += householdMemberCount;
			break;
		case NATIONAL:
			householdNationalEmigrationCount++;
			personNationalEmigrationCount += householdMemberCount;
			break;
		case INTERNATIONAL:
			householdIntlEmigrationCount++;
			personIntlEmigrationCount += householdMemberCount;
			break;
		}
	}
	@Override
	public String toCsvHeadline(String delimiter) {
		return "SpatialUnit" + delimiter + "HHfromLocal" + delimiter + "HHfromCountry" + delimiter + "HHfromIntl" + delimiter + 
			"PersFromLocal" + delimiter + "PersFromCountry" + delimiter + "PersFromIntl" + delimiter +
			"HHtoLocal" + delimiter + "HHtoCountry" + delimiter + "HHtoIntl" + delimiter +
			"PersToLocal" + delimiter + "PersToCountry" + delimiter + "PersToIntl";
	}
	@Override
	public String toString(String delimiter) {
		return spatialUnitId + delimiter + householdLocalImmigrationCount + delimiter + householdNationalImmigrationCount + delimiter + householdIntlImmigrationCount + delimiter +
			personLocalImmigrationCount + delimiter + personNationalImmigrationCount + delimiter + personIntlImmigrationCount + delimiter +
			householdLocalEmigrationCount + delimiter + householdNationalEmigrationCount + delimiter + householdIntlEmigrationCount + delimiter +
			personLocalEmigrationCount + delimiter + personNationalEmigrationCount + delimiter + personIntlEmigrationCount;
	}

	@Override
	public int compareTo(Integer spatialUnitId) {
		return ((Integer)this.spatialUnitId).compareTo(spatialUnitId);
	}
}
