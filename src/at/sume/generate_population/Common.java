/**
 * 
 */
package at.sume.generate_population;

import java.util.*;
import java.io.*;

/**
 * @author ar
 *
 */
public class Common {
	public static String IniFileName = "generate_population.ini";
	
	/**
	 * Get the location of the database from the INI-file
	 * @return pathname of the database
	 */
	public static String GetDbLocation()
	{
	    try {
	        Properties p = new Properties();
	        p.load(new FileInputStream(IniFileName));
	        return(p.getProperty("DbLocation"));
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	
		return null;
	}
}
