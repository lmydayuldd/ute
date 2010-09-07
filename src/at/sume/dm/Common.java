/**
 * 
 */
package at.sume.dm;

import java.sql.SQLException;
import java.util.*;
import java.io.*;

import net.remesch.util.Database;

/**
 * Global functions, variables and parameters
 * 
 * @author Alexander Remesch
 *
 */
public class Common {
	public final static String INI_FILENAME = "sume_dm.ini";
	public static Database db;
	
	/**
	 * Get the location of the database from the INI-file
	 * @return pathname of the database
	 */
	public static String getDbLocation() {
	    try {
	        Properties p = new Properties();
	        p.load(new FileInputStream(INI_FILENAME));
	        return(p.getProperty("DbLocation"));
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	
		return null;
	}
	
	public static Database openDatabase() {
		db = new Database(Common.getDbLocation());
		return(db);
	}

	/**
	 * Retrieve the value of a system parameter (from the table Systemparameter)
	 * @param paramName
	 * @return
	 */
	public static String getSysParam(String paramName) {
		String rv = null;
		try {
			rv = (String)db.lookupSql("select wert from systemparameter where name='" + paramName + "'");
		} catch (SQLException e) {
			System.err.println("getSysParam: error looking up parameter " + paramName);
			e.printStackTrace();
		}
		return(rv);
	}
}
