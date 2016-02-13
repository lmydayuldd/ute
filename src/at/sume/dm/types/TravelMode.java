/**
 * 
 */
package at.sume.dm.types;

/**
 * @author Alexander Remesch
 *
 */
public enum TravelMode {
	MOTORIZED_INDIVIDUAL_TRANSPORT(2),
	PUBLIC_TRANSPORT(1);

    private final int value;
    private TravelMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
