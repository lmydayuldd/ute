/**
 * 
 */
package net.remesch.util;

/**
 * @author Alexander Remesch
 *
 */
public class StringUtil {
	/**
	 * Concatenate all elements from string array using separator
	 * @param a elements to concatenate
	 * @param separator separator in result string between elements
	 * @return concatenated elements separated by separator
	 */
	public static String arrayToString(String[] a, String separator) {
	    StringBuffer result = new StringBuffer();
	    if (a.length > 0) {
	        result.append(a[0]);
	        for (int i=1; i<a.length; i++) {
	            result.append(separator);
	            result.append(a[i]);
	        }
	    }
	    return result.toString();
	}
}
