/**
 * 
 */
package at.sume.dm.entities;

/**
 * @author ar
 *
 */
public enum SpatialUnitLevel {
	SGT, ZB;
	
	public String toString() {
		switch(this) {
		case SGT:
			return "SGT";
		case ZB:
			return "ZB";
		}
		throw new AssertionError("Unknown spatial unit level (not ZB or SGT)");
	}
	
	public int compareTo(String spatialUnitLevel) {
		return spatialUnitLevel.compareTo(this.toString());
	}
}
