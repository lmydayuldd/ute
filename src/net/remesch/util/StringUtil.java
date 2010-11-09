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
	        for (int i = 1; i < a.length; i++) {
	            result.append(separator);
	            result.append(a[i]);
	        }
	    }
	    return result.toString();
	}
	
	/**
	 * Repeat string s n times
	 * @param s string to repeat
	 * @param n number of times to repeat string s
	 * @param separator separator in result string between elements
	 * @return
	 */
	public static String repeat(String s, int n, String separator) {
	    StringBuffer result = new StringBuffer();
	    if (n > 0) {
	    	result.append(s);
			for (int i = 1; i != n; i++) {
	            result.append(separator);
				result.append(s);
			}
	    }
	    return result.toString();
	}
	/**
	 * Concatenate all given strings using the given separator
	 * @param separator
	 * @param string
	 * @return
	 */
	public static String concat(String separator, String ... string) {
		int i = 0;
		String result = string[i++];
		while (i != string.length) {
			result += separator + string[i++];
		}
		return result;
	}
}
