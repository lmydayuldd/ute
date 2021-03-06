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
	// Immigration/emigration counters (households/persons)
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
	// Children leaving parents counters
	private int leftParentsOriginCount;
	private int leftParentsDestinationCount;
	// Household/person cohabitation counters
	private int householdCohabitationOriginCount;
	private int personCohabitationOriginCount;
	private int householdCohabitationDestinationCount;
	private int personCohabitationDestinationCount;
	// Counters of potential immigration
	private int householdPotentialNationalImmigrationCount;
	private int personPotentialNationalImmigrationCount;
	private int householdPotentialIntlImmigrationCount;
	private int personPotentialIntlImmigrationCount;
	// Potential children leaving parents counter
	private int potentialLeftParentsOriginCount;
	
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
	public void addChildLeftParentsOrigin() {
		leftParentsOriginCount++;
	}
	public void addChildLeftParentsDestination() {
		leftParentsDestinationCount++;
	}
	public void addCohabitationOrigin(byte householdMemberCount) {
		householdCohabitationOriginCount++;
		personCohabitationOriginCount += householdMemberCount;
	}
	public void addCohabitationDestination(byte householdMemberCount) {
		householdCohabitationDestinationCount++;
		personCohabitationDestinationCount += householdMemberCount;
	}
	public void addPotentialImmigrationCounters(int householdCount, int householdMemberCount, MigrationRealm migrationRealm) {
		switch (migrationRealm) {
		case LOCAL:
			throw new AssertionError("Not possible for local migration");
		case NATIONAL:
			householdPotentialNationalImmigrationCount += householdCount;
			personPotentialNationalImmigrationCount += householdMemberCount;
			break;
		case INTERNATIONAL:
			householdPotentialIntlImmigrationCount += householdCount;
			personPotentialIntlImmigrationCount += householdMemberCount;
			break;
		}
	}
	public void addPotentialLeftParentsOriginCount(int personCount) {
		potentialLeftParentsOriginCount += personCount;
	}
	@Override
	public String toCsvHeadline(String delimiter) {
		return "SpatialUnit" + delimiter + "HHfromLocal" + delimiter + "HHfromCountry" + delimiter + "HHfromIntl" + delimiter + 
			"PersFromLocal" + delimiter + "PersFromCountry" + delimiter + "PersFromIntl" + delimiter +
			"HHtoLocal" + delimiter + "HHtoCountry" + delimiter + "HHtoIntl" + delimiter +
			"PersToLocal" + delimiter + "PersToCountry" + delimiter + "PersToIntl" + delimiter + "LeftParentsFrom" + delimiter +
			"LeftParentsTo" + delimiter + "HHCohabitationFrom" + delimiter + "HHCohabitationTo" + delimiter + "PersCohabitationFrom" +
			delimiter + "PersCohabitationTo" + delimiter + "PotentialHHfromCountry" + delimiter + "PotentialHHfromIntl" + delimiter +
			"PotentialPersFromCountry" + delimiter + "PotentialPersFromIntl" + delimiter + "PotentialLeftParentsFrom";
	}
	@Override
	public String toString(String delimiter) {
		return spatialUnitId + delimiter + householdLocalImmigrationCount + delimiter + householdNationalImmigrationCount + delimiter + householdIntlImmigrationCount + delimiter +
			personLocalImmigrationCount + delimiter + personNationalImmigrationCount + delimiter + personIntlImmigrationCount + delimiter +
			householdLocalEmigrationCount + delimiter + householdNationalEmigrationCount + delimiter + householdIntlEmigrationCount + delimiter +
			personLocalEmigrationCount + delimiter + personNationalEmigrationCount + delimiter + personIntlEmigrationCount + delimiter + leftParentsOriginCount + delimiter +
			leftParentsDestinationCount + delimiter + householdCohabitationOriginCount + delimiter + personCohabitationOriginCount + delimiter + householdCohabitationDestinationCount +
			delimiter + personCohabitationDestinationCount + delimiter + householdPotentialNationalImmigrationCount + delimiter + householdPotentialIntlImmigrationCount + delimiter +
			personPotentialNationalImmigrationCount + delimiter + personPotentialIntlImmigrationCount + delimiter + potentialLeftParentsOriginCount;
	}

	@Override
	public int compareTo(Integer spatialUnitId) {
		return ((Integer)this.spatialUnitId).compareTo(spatialUnitId);
	}
}
