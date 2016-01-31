package at.sume.sampling;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import at.sume.dm.Common;
import at.sume.dm.model.residential_mobility.RentPerSpatialUnit;
import at.sume.sampling.distributions.HouseholdsPerSpatialUnit;
import at.sume.sampling.entities.DbHouseholdRow;
import at.sume.sampling.entities.DbPersonRow;
import at.sume.sampling.entities.DbTimeUseRow;
import at.sume.sampling.entities.SampleDbHouseholds;
import net.remesch.db.Database;

/**
 * Class for generation of synthetic population
 * 
 * @author Alexander Remesch
 */
public class GeneratePopulation {
//	private static ArrayList<DbHouseholdRow> households;
//	private static ArrayList<DbPersonRow> persons;
	private static int householdCount = 0;
	private static int personCount = 0;
	
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
//		int residentialSatisfactionThresholdRange = Integer.parseInt(Common.getSysParamDataPreparation("THR_ResSatisfactionRange"));
		byte dwellingsOnMarketShare = Byte.parseByte(Common.getSysParamDataPreparation("DwellingsOnMarketShare"));
		int sampleInstitutionalHouseholds = Integer.parseInt(Common.getSysParamDataPreparation("SampleInstitutionalHouseholds"));
		
//		SampleHouseholds sampleHouseholds = new SampleHouseholds(db, "SpatialUnitId = 91101 or SpatialUnitId = 91001");
		SampleHouseholds sampleHouseholds = null;
		if (sampleInstitutionalHouseholds == 0) {
			sampleHouseholds = new SampleHouseholds(db, "HouseholdSize <= " + householdSizeGroups);
		} else {
			sampleHouseholds = new SampleHouseholds(db);
		}
		SampleDbHouseholds sampleDbHouseholds = new SampleDbHouseholds(db, householdSizeGroups, dwellingsOnMarketShare);
//		if (residentialSatisfactionThresholdRange != 0)
//			sampleDbHouseholds.setResidentialSatisfactionThresholdRange(residentialSatisfactionThresholdRange);
		
		// Sample households including persons
		for (HouseholdsPerSpatialUnit householdsPerSpatialUnit : sampleHouseholds) {
			List<DbHouseholdRow> households = new ArrayList<DbHouseholdRow>();
			List<DbPersonRow> persons = new ArrayList<DbPersonRow>();
			List<DbTimeUseRow> timeUse = new ArrayList<DbTimeUseRow>();
			
			if (householdsPerSpatialUnit.householdSize > householdSizeGroups)
				System.out.println(Common.printInfo() + ": creating " + householdsPerSpatialUnit.householdCount + " households for spatial unit " + householdsPerSpatialUnit.spatialUnitId + " (institutional households)");
			else
				System.out.println(Common.printInfo() + ": creating " + householdsPerSpatialUnit.householdCount + " households for spatial unit " + householdsPerSpatialUnit.spatialUnitId + ", size " + householdsPerSpatialUnit.householdSize);
			sampleDbHouseholds.setSpatialUnit(householdsPerSpatialUnit.spatialUnitId);
			for (int i = 0; i != householdsPerSpatialUnit.householdCount; i++) {
				DbHouseholdRow household = sampleDbHouseholds.randomSample(householdsPerSpatialUnit, i);
				households.add(household);
				persons.addAll(household.getMembers());
				for (DbPersonRow p : persons) {
					List<DbTimeUseRow> t = p.getTimeUse();
					if (t != null)
						timeUse.addAll(t);
				}
//				if ((i % 1000 == 0) && (i > 0)) {
//					System.out.println(Common.printInfo() + ": creating household " + i + " of " + householdsPerSpatialUnit.householdCount);
//				}
			}
			householdCount += households.size();
			personCount += persons.size();
			if (households.size() > 0) {
//				System.out.println(Common.printInfo() + ": writing " + households.size() + " households and " + persons.size() + " persons to the db");
//				db.insertFieldMap(households, "select HouseholdId, HouseholdSize, SpatialUnitId, DwellingId, LivingSpace, CostOfResidence, HouseholdType from _DM_Households", true);
				db.insertSql(households, "_DM_Households");
				db.con.commit();
//				db.insertFieldMap(persons, "select PersonId, HouseholdId, Sex, Age, YearlyIncome from _DM_Persons", true);
				db.insertSql(persons, "_DM_Persons");
				db.con.commit();
				db.insertSql(timeUse, "_DM_TimeUse");
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
		@SuppressWarnings("unused")
		RentPerSpatialUnit rentPerSpatialUnit = RentPerSpatialUnit.getInstance("", Common.getSpatialUnitLevel());

		// TODO: put into a table-class, method truncate
		db.execute("delete from _DM_Households");
		db.execute("delete from _DM_Persons");
		db.execute("delete from _DM_TimeUse");
//			db.con.setAutoCommit(false);
		db.con.commit();
		
		GenerateHouseholds(db);
		
		System.out.println(Common.printInfo() + ": created " + householdCount + " households and " + personCount + " persons");
		
        System.out.println(Common.printInfo() + ": end");
        System.exit(0);
	}

}
