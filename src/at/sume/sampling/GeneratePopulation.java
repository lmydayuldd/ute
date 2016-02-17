package at.sume.sampling;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import at.sume.dm.Common;
import at.sume.dm.model.residential_mobility.RentPerSpatialUnit;
import at.sume.sampling.distributions.HouseholdsPerSpatialUnit;
import at.sume.sampling.entities.DbHouseholdRow;
import at.sume.sampling.entities.DbPersonRow;
import at.sume.sampling.entities.DbTimeUseRow;
import at.sume.sampling.entities.SampleDbHouseholds;
import at.sume.sampling.timeuse.TimeUseRow;
import net.remesch.db.Database;
import net.remesch.util.FileUtil;

/**
 * Class for generation of synthetic population
 * 
 * @author Alexander Remesch
 */
public class GeneratePopulation {
	private static List<DbHouseholdRow> households;
	private static List<DbPersonRow> persons;
	private static List<DbTimeUseRow> timeUse;
	private static int householdCount = 0;
	private static int personCount = 0;
	
	private static int modelRuns;
	private static int modelRun;

	private static String timeUseSummaryFileName = "TimeUseSummary.csv";

	/**
	 * Generate synthetic households and persons for the SUME decision model
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchFieldException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 */
	private static void GenerateHouseholds(Database db) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		byte householdSizeGroups = Byte.parseByte(Common.getSysParam("HouseholdSizeGroups"));
		byte dwellingsOnMarketShare = Byte.parseByte(Common.getSysParamDataPreparation("DwellingsOnMarketShare"));
		int sampleInstitutionalHouseholds = Integer.parseInt(Common.getSysParamDataPreparation("SampleInstitutionalHouseholds"));
		
		SampleHouseholds sampleHouseholds = null;
		if (sampleInstitutionalHouseholds == 0) {
			sampleHouseholds = new SampleHouseholds(db, "HouseholdSize <= " + householdSizeGroups);
		} else {
			sampleHouseholds = new SampleHouseholds(db);
		}
		SampleDbHouseholds sampleDbHouseholds = new SampleDbHouseholds(db, householdSizeGroups, dwellingsOnMarketShare);
		// Sample households including persons
		for (HouseholdsPerSpatialUnit householdsPerSpatialUnit : sampleHouseholds) {
			households = new ArrayList<DbHouseholdRow>();
			persons = new ArrayList<DbPersonRow>();
			timeUse = new ArrayList<DbTimeUseRow>();
			
			if (householdsPerSpatialUnit.householdSize > householdSizeGroups)
				System.out.println(Common.printInfo(modelRun) + ": creating " + householdsPerSpatialUnit.householdCount + " households for spatial unit " + householdsPerSpatialUnit.spatialUnitId + " (institutional households)");
			else
				System.out.println(Common.printInfo(modelRun) + ": creating " + householdsPerSpatialUnit.householdCount + " households for spatial unit " + householdsPerSpatialUnit.spatialUnitId + ", size " + householdsPerSpatialUnit.householdSize);
			sampleDbHouseholds.setSpatialUnit(householdsPerSpatialUnit.spatialUnitId);
			for (int i = 0; i != householdsPerSpatialUnit.householdCount; i++) {
				DbHouseholdRow household = sampleDbHouseholds.randomSample(householdsPerSpatialUnit, i);
				households.add(household);
				persons.addAll(household.getMembers());
				for (DbPersonRow p : household.getMembers()) {
					if (p.getAge() >= 10) { // ZVE 2008/2009 covers only persons of age 10 and above
						List<DbTimeUseRow> t = p.getTimeUse();
						if (t != null)
							timeUse.addAll(t);
					}
				}
			}
			householdCount += households.size();
			personCount += persons.size();
			if (households.size() > 0) {
				db.insertSql(households, "_DM_Households");
				db.con.commit();
				db.insertSql(persons, "_DM_Persons");
				db.con.commit();
				db.insertSql(timeUse, "_DM_TimeUse");
				db.con.commit();
			}
		}
	}
	
	/**
	 * Generate synthetic population for the SUME decision model
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		int ret = JOptionPane.showConfirmDialog(null, "Do you really want to start the generation of the synthetic population of the SUME model? " +
				" All currently existing population data will be lost.", "Generate Population", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		if (ret != 0)
			return;

	    System.out.println(Common.printInfo() + ": start");

		Database db = Common.openDatabase();
		db.con.setAutoCommit(false);
		Common.init();
	    modelRuns = Integer.parseInt(Common.getSysParamDataPreparation("ParmeterizationRuns"));
        for (modelRun = 0; modelRun != modelRuns; modelRun++) {
            System.out.println(Common.printInfo(modelRun) + ": ======================= paremterization run " + (modelRun + 1) + " of " + modelRuns);
			@SuppressWarnings("unused")
			RentPerSpatialUnit rentPerSpatialUnit = RentPerSpatialUnit.getInstance("", Common.getSpatialUnitLevel());
		
			// TODO: put into a table-class, method truncate
			db.execute("delete from _DM_Households");
			db.execute("delete from _DM_Persons");
			db.execute("delete from _DM_TimeUse");
			db.con.commit();
			
			GenerateHouseholds(db);
			
			// TODO: save summary population & time use values here
			// - time use per activity
			String sqlStatement = "SELECT Activity, SUM(MinutesPerDay) AS avgTimeUse FROM _DM_TimeUse GROUP BY Activity;";
			List<TimeUseRow> timeUse = db.select(TimeUseRow.class, sqlStatement);
			saveTimeUseSummary(timeUseSummaryFileName, timeUse);
			// - persons per cell
			// - households per cell
			
			System.out.println(Common.printInfo(modelRun) + ": created " + householdCount + " households and " + personCount + " persons");
        }
	    System.out.println(Common.printInfo() + ": end");
        System.exit(0);
	}

	public static void saveTimeUseSummary(String timeUseSummaryFileName, List<TimeUseRow> timeUse) throws IOException {
		String pathName = Common.createPathName(timeUseSummaryFileName);
		FileUtil.rotateFile(pathName);
		Path timeUseSummaryFile = Paths.get(pathName);
		List<String> sl = timeUse.stream().map(e -> modelRun + ":" + e.toString()).collect(Collectors.toList());
		Files.write(timeUseSummaryFile, sl, Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}
}
