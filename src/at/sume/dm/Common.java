/**
 * 
 */
package at.sume.dm;

import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Properties;

import net.remesch.db.Database;
import net.remesch.probability.SingleProbability;
import net.remesch.util.DateUtil;
import at.sume.dm.entities.SpatialUnitLevel;

/**
 * Global functions, variables and parameters
 * 
 * @author Alexander Remesch
 *
 */
public class Common {
	public final static String INI_FILENAME = "sume_dm.ini";
	public static Database db, odb;
	private static short scenarioId;
	private static int residentialSatisfactionThreshold;
	private static int searchAreaSize;
	private static int searchAreaSizeIncrement;
	private static int dwellingsConsideredPerYear;
	private static int dwellingsOnMarketShare;
	private static int alwaysLookForDwellings = 0;
	private static short modelStartYear;
	private static short movingDecisionMin;
	private static String pathOutput;
	private static SpatialUnitLevel spatialUnitLevel;
	private static int residentialSatisfactionEstimateRange;
	private static short personMaxAge;
	private static boolean activeResidentialMobility = false;
	private static SingleProbability movingProbability;
	private static SingleProbability movingOutProbability;
	private static boolean outputFullData = false;
	private static byte dwellingPriceRange;
	private static byte cohabitationRate;
	private static SingleProbability leavingParentsProbability;
	
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

	public static int getSearchAreaSizeIncement() {
		return searchAreaSizeIncrement;
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

	public static short getMovingDecisionMin() {
		return movingDecisionMin;
	}
	
	public static String getPathOutput() {
		return pathOutput;
	}
	
	public static SpatialUnitLevel getSpatialUnitLevel() {
		return spatialUnitLevel;
	}
	
	public static int getResidentialSatisfactionEstimateRange() {
		return residentialSatisfactionEstimateRange;
	}
	
	public static short getPersonMaxAge() {
		return personMaxAge;
	}
	
	public static boolean isActiveResidentialMobility() {
		return activeResidentialMobility;
	}

	/**
	 * @return the movingProbability
	 */
	public static SingleProbability getMovingProbability() {
		return movingProbability;
	}

	/**
	 * @return the movingOutProbability
	 */
	public static SingleProbability getMovingOutProbability() {
		return movingOutProbability;
	}
	
	/**
	 * @return the outputFullData
	 */
	public static boolean isOutputFullData() {
		return outputFullData;
	}

	/**
	 * @return the dwellingPriceRange
	 */
	public static byte getDwellingPriceRange() {
		return dwellingPriceRange;
	}

	/**
	 * @return the cohabitationRate
	 */
	public static byte getCohabitationRate() {
		return cohabitationRate;
	}

	/**
	 * @return the leavingParentsProbability
	 */
	public static SingleProbability getLeavingParentsProbability() {
		return leavingParentsProbability;
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

	/**
	 * Get the location of the output database from the INI-file
	 * @return pathname of the database
	 */
	public static String getOutputDbLocation() {
	    try {
	        Properties p = new Properties();
	        p.load(new FileInputStream(INI_FILENAME));
	        return(p.getProperty("OutputDbLocation"));
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	
		return null;
	}
	
	public static Database openOutputDatabase() {
		odb = new Database(Common.getOutputDbLocation());
		return(odb);
	}
	
	public static void init() {
		scenarioId = Short.parseShort(getSysParam("DefaultScenario"));
		residentialSatisfactionThreshold = Integer.parseInt(getSysParam("THR_ResSatisfaction"));
		dwellingsConsideredPerYear = Integer.parseInt(getSysParam("HouseholdDwellingsConsideredPerYear"));
		dwellingsOnMarketShare = Integer.parseInt(getSysParam("DwellingsOnMarketShare"));
		alwaysLookForDwellings = Integer.parseInt(getSysParam("AlwaysLookForDwellings"));
		modelStartYear = Short.parseShort(getSysParam("ModelStartYear"));
		movingDecisionMin = Short.parseShort(getSysParam("HouseholdMovingDecisionMin"));
		pathOutput = getSysParam("PathOutput");
		String sysParam = getSysParam("SpatialUnitLevel");
		if (SpatialUnitLevel.ZB.compareTo(sysParam) == 0) {
			spatialUnitLevel = SpatialUnitLevel.ZB;
			searchAreaSize = Integer.parseInt(getSysParam("HouseholdSearchAreaSizeZB"));
			searchAreaSizeIncrement = Integer.parseInt(getSysParam("HouseholdSearchAreaSizeIncrementZB"));
		} else if (SpatialUnitLevel.SGT.compareTo(sysParam) == 0) {
			spatialUnitLevel = SpatialUnitLevel.SGT;
			searchAreaSize = Integer.parseInt(getSysParam("HouseholdSearchAreaSizeSGT"));
			searchAreaSizeIncrement = Integer.parseInt(getSysParam("HouseholdSearchAreaSizeIncrementSGT"));
		} else {
			throw new AssertionError("Systemparameter SpatialUnitLevel must be ZB or SGT (is " + sysParam);
		}
		residentialSatisfactionEstimateRange = Integer.parseInt(getSysParam("ResidentialSatisfactionEstimateRange"));
		personMaxAge = Short.parseShort(getSysParam("PersonMaxAge"));
		movingProbability = new SingleProbability(Byte.parseByte(getSysParam("ProbabilityForMoving")), 100);
		movingOutProbability = new SingleProbability(Byte.parseByte(getSysParam("ProbabilityForMovingOut")), 100);
		if (Short.parseShort(getSysParam("OutputFullData")) == -1) {
			outputFullData = true;
		}
		dwellingPriceRange = Byte.parseByte(getSysParam("DwellingPriceRange"));
		cohabitationRate = Byte.parseByte(getSysParam("CohabitationRate"));
		leavingParentsProbability = new SingleProbability(Byte.parseByte(getSysParam("ProbabilityLeavingParents")), 100);
	}
	
	/**
	 * Retrieve the value of a system parameter (from the table SysParamsRuntime)
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

	/**
	 * Retrieve the value of a system parameter (from the table SysParamsDataPreparation)
	 * @param paramName
	 * @return
	 */
	public static String getSysParamDataPreparation(String paramName) {
		Object rv = null;
		try {
			rv = db.lookupSql("select wert from SysParamsDataPreparation where name='" + paramName + "'");
			if (rv != null)
				return (String)rv;
		} catch (SQLException e) {
			System.err.println("getSysParam: error looking up parameter " + paramName);
			e.printStackTrace();
		}
		return null;
	}

	public static String printInfo() {
		return DateUtil.now() + " (usedmem=" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 + "m)";
	}
}
