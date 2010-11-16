/**
 * 
 */
package net.remesch.util;

/**
 * @author Alexander Remesch
 *
 */
public class MathUtil {
	/**
	 * Calculate the median of a given list of values - requires the array to be sorted!
	 * @param m sorted array of values
	 * @return the median, i.e. the value separating the higher half of the array from the lower half
	 */
	public static double median(Double[] m) {
	    int middle = m.length/2;  // subscript of middle element
	    if (m.length%2 == 1) {
	        // Odd number of elements -- return the middle one.
	        return m[middle];
	    } else {
	       // Even number -- return average of middle two
	       // Must cast the numbers to double before dividing.
	       return (m[middle-1] + m[middle]) / 2.0;
	    }
	}

	/**
	 * Calculate the median of a given list of values - requires the array to be sorted
	 * @param m sorted array of values
	 * @return the median, i.e. the value separating the higher half of the array from the lower half
	 */
	public static long median(Long[] m) {
	    int middle = m.length/2;  // subscript of middle element
	    if (m.length%2 == 1) {
	        // Odd number of elements -- return the middle one.
	        return m[middle];
	    } else {
	       // Even number -- return average of middle two
	       // Must cast the numbers to double before dividing.
	       return (m[middle-1] + m[middle]) / 2;
	    }
	}
}
