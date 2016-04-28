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
	private static boolean v = true;
	public static Database db, odb;
	private static short scenarioId;
	private static int residentialSatisfactionThreshold;
	private static int residentialSatisfactionThresholdMod;
	private static int searchAreaSize;
	private static int searchAreaSizeIncrement;
	private static int dwellingsConsideredPerYear;
//	private static int dwellingsConsideredPerCell;
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
	private static byte movingTogetherRate;
	private static SingleProbability leavingParentsProbability;
	private static byte childrenMaxAge;
	private static byte dwellingsOnMarketAutoAdjust;
	private static byte deathAdjustment;
	private static byte birthAdjustment;
	private static byte immigrationIncomeModifier;
	private static boolean demographyOnly;
	private static byte modelRuns;
	private static byte youngHouseholdAgeLimit;
	private static short modelYear;
	private static byte outputIncomeGroups;
	private static byte outputInterval;
	private static byte householdReductionFactor;
	private static int maxYearlyPersonIncome;
	private static boolean useMigrationSaldo;
	
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
	 * @return the residentialSatisfactionThresholdMod
	 */
	public static int getResidentialSatisfactionThresholdMod() {
		return residentialSatisfactionThresholdMod;
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

//	/**
//	 * 
//	 * @return the dwellingsConsideredPerCell
//	 */
//	public static int getDwellingsConsideredPerCell() {
//		return dwellingsConsideredPerCell;
//	}
	
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
	 * @return the modelYear
	 */
	public static short getModelYear() {
		return modelYear;
	}

	/**
	 * @param modelYear the modelYear to set
	 */
	public static void setModelYear(short modelYear) {
		Common.modelYear = modelYear;
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
	 * @return the movingTogetherRate
	 */
	public static byte getMovingTogetherRate() {
		return movingTogetherRate;
	}

	/**
	 * @return the leavingParentsProbability
	 */
	public static SingleProbability getLeavingParentsProbability() {
		return leavingParentsProbability;
	}

	/**
	 * @param childrenMaxAge the childrenMaxAge to set
	 */
	public static void setChildrenMaxAge(byte childrenMaxAge) {
		Common.childrenMaxAge = childrenMaxAge;
	}

	/**
	 * @return the childrenMaxAge
	 */
	public static byte getChildrenMaxAge() {
		return childrenMaxAge;
	}

	/**
	 * @return the dwellingsOnMarketAutoAdjust
	 */
	public static byte getDwellingsOnMarketAutoAdjust() {
		return dwellingsOnMarketAutoAdjust;
	}

	/**
	 * @return the deathAdjustment
	 */
	public static byte getDeathAdjustment() {
		return deathAdjustment;
	}

	/**
	 * @return the birthAdjustment
	 */
	public static byte getBirthAdjustment() {
		return birthAdjustment;
	}

	/**
	 * @return the immigrationIncomeModifier
	 */
	public static byte getImmigrationIncomeModifier() {
		return immigrationIncomeModifier;
	}

	public static boolean isDemographyOnly() {
		return demographyOnly;
	}

	public static byte getModelRuns() {
		return modelRuns;
	}

	/**
	 * @return the youngHouseholdAgeLimit
	 */
	public static byte getYoungHouseholdAgeLimit() {
		return youngHouseholdAgeLimit;
	}

	/**
	 * @return the outputIncomeGroups
	 */
	public static byte getOutputIncomeGroups() {
		return outputIncomeGroups;
	}

	/**
	 * @return the outputInterval
	 */
	public static byte getOutputInterval() {
		return outputInterval;
	}

	/**
	 * @return the householdReductionFactor
	 */
	public static byte getHouseholdReductionFactor() {
		return householdReductionFactor;
	}

	/**
	 * @return the maxYearlyPersonIncome
	 */
	public static int getMaxYearlyPersonIncome() {
		return maxYearlyPersonIncome;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isUseMigrationSaldo() {
		return useMigrationSaldo;
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

	public static Database openDatabase() throws SQLException, ClassNotFoundException {
		db = new Database(Common.getDbLocation());
//		db = new Database();
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
	
	public static Database openOutputDatabase() throws SQLException, ClassNotFoundException {
		odb = new Database(Common.getOutputDbLocation());
		return(odb);
	}
	
	public static void init() {
		init(true);
	}
	public static void init(boolean verbose) {
		v = verbose;
		scenarioId = Short.parseShort(getSysParam("DefaultScenario"));
		residentialSatisfactionThreshold = Integer.parseInt(getSysParam("THR_ResSatisfaction"));
		residentialSatisfactionThresholdMod = Integer.parseInt(getSysParam("ResidentialSatisfactionThreshMod"));
		dwellingsConsideredPerYear = Integer.parseInt(getSysParam("HouseholdDwellingsConsideredPerYear"));
//		dwellingsConsideredPerCell = Integer.parseInt(getSysParam("HouseholdDwellingsConsideredPerCell"));
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
		movingTogetherRate = Byte.parseByte(getSysParam("MoveTogetherRate"));
		leavingParentsProbability = new SingleProbability(Byte.parseByte(getSysParam("ProbabilityLeavingParents")), 100);
		setChildrenMaxAge(Byte.parseByte(getSysParam("ChildrenMaxAge")));
		dwellingsOnMarketAutoAdjust = Byte.parseByte(getSysParam("DwellingsOnMarketAutoAdjust"));
		birthAdjustment = Byte.parseByte(getSysParam("BirthProbabilityAdjustment"));
		deathAdjustment = Byte.parseByte(getSysParam("DeathProbabilityAdjustment"));
		immigrationIncomeModifier = Byte.parseByte(getSysParam("ImmigrationIncomeMod"));
		if (Short.parseShort(getSysParam("DemographyOnly")) == 0) {
			demographyOnly = false;
		} else {
			demographyOnly = true;
		}
		modelRuns = Byte.parseByte(getSysParam("ModelRuns"));
		youngHouseholdAgeLimit = Byte.parseByte(getSysParam("YoungHouseholdAgeLimit"));
		outputIncomeGroups = Byte.parseByte(getSysParam("OutputIncomeGroups"));
		outputInterval = Byte.parseByte(getSysParam("OutputInterval"));
		householdReductionFactor = Byte.parseByte(getSysParam("HouseholdReductionFactor"));
		maxYearlyPersonIncome = Integer.parseInt(getSysParam("MaxYearlyPersonIncome"));
		useMigrationSaldo = Boolean.parseBoolean(getSysParam("UseMigrationSaldo"));
	}
	
	/**
	 * Retrieve the value of a system parameter (from the table SysParamsRuntime)
	 * @param paramName
	 * @return
	 */
	public static String getSysParam(String paramName) {
		Object rv = null;
		String result = null;;
		try {
			rv = db.lookupSql("select wert from SysParamsRuntime where name='" + paramName + "'");
			if (rv != null)
				result = (String)rv;
		} catch (SQLException e) {
			System.err.println("getSysParam: error looking up parameter " + paramName);
			e.printStackTrace();
		}
		if (v)
			System.out.println("   Sysparam " + paramName + " = " + result);
		return result;
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

	public static String printInfo(int modelRun) {
		return DateUtil.now() + " (usedmem=" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 + "m)@" + (modelRun + 1);
	}

	public static String createPathName(String fileName) {
		String path = Common.getPathOutput();
		if (path == null) path = "";
		String pathName;
		if (path.endsWith("\\") || (path == ""))
			pathName = path + fileName;
		else
			pathName = path + "\\" + fileName;
		return pathName;
	}
}
