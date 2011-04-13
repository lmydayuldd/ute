/**
 * 
 */
package net.remesch.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility class concerning date-handling
 * 
 * @author Alexander Remesch
 *
 */
public class DateUtil {
	/**
	 * Get the current date and time in the format specified
	 * @param Format
	 * @return
	 */
    public static String now(String Format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Format);
        return sdf.format(cal.getTime());
    }
    
    /**
     * Get the current date and time in the format dd.MM.yyyy HH:mm:ss.SSS
     * @return
     */
    public static String now()
    {
    	return now("dd.MM.yyyy HH:mm:ss.SSS");
    }
}
