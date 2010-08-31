/**
 * 
 */
package at.sume.dm;

import java.util.*;
import java.io.*;

/**
 * Global functions, variables and parameters
 * 
 * @author Alexander Remesch
 *
 */
public class Common {
	public final static String INI_FILENAME = "sume_dm.ini";
	// TODO: put the following into the database (table system parameters)
	public final static int MODEL_ITERATIONS = 1;
	
	/**
	 * Get the location of the database from the INI-file
	 * @return pathname of the database
	 */
	public static String GetDbLocation()
	{
	    try {
	        Properties p = new Properties();
	        p.load(new FileInputStream(INI_FILENAME));
	        return(p.getProperty("DbLocation"));
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	
		return null;
	}
}
