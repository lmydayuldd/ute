/**
 * 
 */
package net.remesch.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author ar
 *
 */
public class DateUtil {
	/**
	 * 
	 * @param Format
	 * @return
	 */
    public static String now(String Format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Format);
        return sdf.format(cal.getTime());
    }
    
    public static String now()
    {
    	return now("dd.MM.yyyy HH:mm:ss.SSS");
    }
}
