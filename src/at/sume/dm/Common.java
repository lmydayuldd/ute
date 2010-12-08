/**
 * 
 */
package at.sume.dm;

import java.sql.SQLException;
import java.util.*;
import java.io.*;

import net.remesch.db.Database;

/**
 * Global functions, variables and parameters
 * 
 * @author Alexander Remesch
 *
 */
public class Common {
	public final static String INI_FILENAME = "sume_dm.ini";
	public static Database db;
	private static short scenarioId;
	private static int residentialSatisfactionThreshold;
	private static int searchAreaSize;
	private static int dwellingsConsideredPerYear;
	private static int dwellingsOnMarketShare;
	private static int alwaysLookForDwellings = 0;
	private static short modelStartYear;
	
	/**
	 * @return the iniFilename
	 */
	public static String getIniFilename() {
		return INI_FILENAME;
	}

	/**
	 * @return the db
	 */
	public static Database getDb() {
		return db;
	}

	/**
	 * @return the scenarioId
	 */
	public static short getScenarioId() {
		return scenarioId;
	}

	/**
	 * @return the residentialSatisfactionThreshold
	 */
	public static int getResidentialSatisfactionThreshold() {
		return residentialSatisfactionThreshold;
	}

	/**
	 * @return the searchAreaSize
	 */
	public static int getSearchAreaSize() {
		return searchAreaSize;
	}

	/**
	 * @return the dwellingsConsideredPerYear
	 */
	public static int getDwellingsConsideredPerYear() {
		return dwellingsConsideredPerYear;
	}

	/**
	 * @return the dwellingsOnMarketShare
	 */
	public static int getDwellingsOnMarketShare() {
		return dwellingsOnMarketShare;
	}

	/**
	 * @return the alwaysLookForDwellings
	 */
	public static int getAlwaysLookForDwellings() {
		return alwaysLookForDwellings;
	}

	/**
	 * @return the modelStartYear
	 */
	public static short getModelStartYear() {
		return modelStartYear;
	}

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

	public static void init() {
		scenarioId = Short.parseShort(getSysParam("DefaultScenario"));
		residentialSatisfactionThreshold = Integer.parseInt(getSysParam("THR_ResSatisfaction"));
		searchAreaSize = Integer.parseInt(getSysParam("HouseholdSearchAreaSize"));
		dwellingsConsideredPerYear = Integer.parseInt(getSysParam("HouseholdDwellingsConsideredPerYear"));
		dwellingsOnMarketShare = Integer.parseInt(getSysParam("DwellingsOnMarketShare"));
		alwaysLookForDwellings = Integer.parseInt(getSysParam("AlwaysLookForDwellings"));
		modelStartYear = Short.parseShort(getSysParam("ModelStartYear"));
	}
	
	/**
	 * Retrieve the value of a system parameter (from the table Systemparameter)
	 * @param paramName
	 * @return
	 */
	public static String getSysParam(String paramName) {
		Object rv = null;
		try {
			rv = db.lookupSql("select wert from SysParamsRuntime where name='" + paramName + "'");
			if (rv != null)
				return (String)rv;
		} catch (SQLException e) {
			System.err.println("getSysParam: error looking up parameter " + paramName);
			e.printStackTrace();
		}
		return null;
	}
}
