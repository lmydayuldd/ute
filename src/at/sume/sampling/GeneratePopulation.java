package at.sume.sampling;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.remesch.util.Database;
import net.remesch.util.DateUtil;
import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.sampling.distributions.CostOfResidenceDistributionRow;
import at.sume.sampling.distributions.HouseholdsPerSpatialUnit;
import at.sume.sampling.distributions.LivingSpaceDistributionRow;
import at.sume.sampling.distributions.PersonsPerAgeSexHouseholdsizePersonnr;

/**
 * Class for generation of synthetic population
 * @author Alexander Remesch
 */
public class GeneratePopulation {
	/**
	 * Generate synthetic households and persons for the SUME decision model
	 * @throws SQLException 
	 */
	private static void GenerateHouseholds(Database db) throws SQLException {
		Households households = new Households(db);
		HouseholdRow hh = new HouseholdRow(households);
		hh.prepareStatement();
		HouseholdsPerSpatialUnit hhpsu;
		LivingSpaceDistributionRow livingSpaceDistributionRow = null;
		SampleHouseholdLivingSpace livingSpace1 = new SampleHouseholdLivingSpace(db);
		SampleHouseholdLivingSpace livingSpace2 = new SampleHouseholdLivingSpace(db);
		SampleHouseholdLivingSpace livingSpace3 = new SampleHouseholdLivingSpace(db);
		SampleHouseholdLivingSpace livingSpace4 = new SampleHouseholdLivingSpace(db);
		SampleHouseholdCostOfResidence householdCostOfResidence = new SampleHouseholdCostOfResidence(db);
		livingSpace1.loadDistribution((short) 1);
		livingSpace2.loadDistribution((short) 2);
		livingSpace3.loadDistribution((short) 3);
		livingSpace4.loadDistribution((short) 4);
		
		SampleHouseholds.LoadDistribution(db);
		
		long total_households = SampleHouseholds.getNrHouseholdsTotalSum();
		short hh_size;
		// Sample households including persons
		for (long i = 0; i != total_households; i++) {
			// Household number
			hh.setHouseholdId(i + 1);
			// Household spatial unit
			int index = SampleHouseholds.determineLocationIndex();
			hhpsu = SampleHouseholds.GetSpatialUnitData(index);
			hh.setSpatialunitId(hhpsu.getSpatialUnitId());
			// Household size
			hh_size = SampleHouseholds.determineSize(index);
			hh.setHouseholdSize(hh_size);
			switch(hh_size) {
			case 1:
				livingSpaceDistributionRow = livingSpace1.determineLivingSpaceDistributionRow();
				hh.setLivingSpace(livingSpace1.determineLivingSpace(livingSpaceDistributionRow));
				break;
			case 2:
				livingSpaceDistributionRow = livingSpace2.determineLivingSpaceDistributionRow();
				hh.setLivingSpace(livingSpace2.determineLivingSpace(livingSpaceDistributionRow));
				break;
			case 3:
				livingSpaceDistributionRow = livingSpace3.determineLivingSpaceDistributionRow();
				hh.setLivingSpace(livingSpace3.determineLivingSpace(livingSpaceDistributionRow));
				break;
			case 4:
				livingSpaceDistributionRow = livingSpace4.determineLivingSpaceDistributionRow();
				hh.setLivingSpace(livingSpace4.determineLivingSpace(livingSpaceDistributionRow));
				break;
			}
			hh.setLivingSpaceGroupId(livingSpaceDistributionRow.getLivingSpaceGroup());
			householdCostOfResidence.loadDistribution(livingSpaceDistributionRow.getLivingSpaceGroup());
			CostOfResidenceDistributionRow costOfResidenceDistributionRow = householdCostOfResidence.determineCostOfResidenceDistributionRow();
			hh.setCostOfResidence(householdCostOfResidence.determineCostOfResidence(costOfResidenceDistributionRow) * hh.getLivingSpace() * 12);
			hh.setCostOfResidenceGroupId(costOfResidenceDistributionRow.getCostOfResidenceGroupId());
			hh.executeInsert();
			if ((i % 1000) == 0)
				System.out.println("Household i = " + i + " @ " + DateUtil.now());
		}
		SampleHouseholds.FreeDistribution();
	}
	
	/**
	 * Generate synthetic persons for a household
	 * 
	 * Do this in an extra run because we may process all persons in a certain spatial unit and with a certain
	 * household size in one pass thus having to load the distribution only once for each group of persons.
	 *
	 * Runtime: ~3,5 hrs. for household-representatives only (AMD Turion 64 X2 2,1GHz in VirtualBox/XP)
	 * @throws SQLException 
	 */
	private static void GeneratePersonsPerHousehold(Database db) throws SQLException {
		Persons persons = new Persons(db);
		PersonRow pers = new PersonRow(persons);
		pers.prepareStatement();
		SamplePersonIncome income = new SamplePersonIncome(db);
		SamplePersons samplePersons1 = new SamplePersons();
		SamplePersons samplePersons2 = new SamplePersons();
		SamplePersons samplePersons3 = new SamplePersons();
		SamplePersons samplePersons4 = new SamplePersons();
		PersonsPerAgeSexHouseholdsizePersonnr pc = null;
		ResultSet rs = db.executeQuery("SELECT HouseholdId, SpatialunitId, HouseholdSize FROM _DM_Households " +
										"ORDER BY SpatialunitId, HouseholdSize;");
		long personId = 1;
		long spatialUnitId_prev = 0, spatialUnitId_curr = 0;
		short householdSize_prev = 0, householdSize_curr = 0;
		while (rs.next()) {
			householdSize_curr = rs.getShort("HouseholdSize");
			spatialUnitId_curr = rs.getLong("SpatialunitId");
			if ((spatialUnitId_curr != spatialUnitId_prev) || (householdSize_curr != householdSize_prev)) {
				// new spatial unit or household size -> different person sex/age distribution necessary
				samplePersons1.FreeDistribution();
				samplePersons2.FreeDistribution();
				samplePersons3.FreeDistribution();
				samplePersons4.FreeDistribution();
				samplePersons1.LoadDistribution(db, (short) 1, householdSize_curr, spatialUnitId_curr);
				samplePersons2.LoadDistribution(db, (short) 2, householdSize_curr, spatialUnitId_curr);
				samplePersons3.LoadDistribution(db, (short) 3, householdSize_curr, spatialUnitId_curr);
				samplePersons4.LoadDistribution(db, (short) 4, householdSize_curr, spatialUnitId_curr);
			}
			long householdId = rs.getLong("HouseholdId");
			for (short i = 0; i != householdSize_curr; i++, personId++) {
				// TODO: create 2nd person age & sex dependent on 1st person 
				// Person number
				pers.setPersonId(personId);
				// Household representative if first person
				if (i == 0)
					pers.setHouseholdRepresentative(true);
				else
					pers.setHouseholdRepresentative(false);
				pers.setPersonNrInHousehold((short)(i + 1));
				// Person sex & age: load distribution
				// Person sex
				switch (i) {
				case 0:
					pc = samplePersons1.getPersonData(samplePersons1.determinePersonDataIndex());
					break;
				case 1:
					pc = samplePersons2.getPersonData(samplePersons2.determinePersonDataIndex());
					break;
				case 2:
					pc = samplePersons3.getPersonData(samplePersons3.determinePersonDataIndex());
					break;
				default:
					if (i >= 3) {
						pc = samplePersons4.getPersonData(samplePersons4.determinePersonDataIndex());
					} else {
						throw new IllegalArgumentException("Illegal person number " + i);
					}
					break;
				}
				pers.setSex(pc.getSex());
				// Person age
				pers.setAgeGroupId(pc.getAgeGroupId());
				pers.setAge(pc.getAge());
				// Household-Id
				pers.setHouseholdId(householdId);
				// Yearly income
				income.loadDistribution(spatialUnitId_curr, pers.getSex(), pers.getAgeGroupId());
				pers.setYearlyIncome(income.determineIncome());
				
				pers.executeInsert();
				// Sample dwelling of the current household
				// TODO: vacant dwellings must be sampled somewhere else
				if ((personId % 1000) == 0)
					System.out.println("Person personId = " + personId + " @ " + DateUtil.now());
			}
			spatialUnitId_prev = spatialUnitId_curr;
			householdSize_prev = householdSize_curr;
		}
	}
	
	/**
	 * Generate synthetic population for the SUME decision model
	 * @param args
	 */
	public static void main(String[] args) {
		int ret = JOptionPane.showConfirmDialog(null, "Do you really want to start the generation of the synthetic population of the SUME model? " +
				" All currently existing population data will be lost.", "Generate Population", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		if (ret != 0)
			return;
		
		System.out.println("Start @ " + DateUtil.now());
		Database db = new Database(Common.getDbLocation());

		// TODO: put into a table-class, method truncate
		db.execute("delete * from _DM_Households");
		db.execute("delete * from _DM_Persons");
		
		try {
			GenerateHouseholds(db);
			GeneratePersonsPerHousehold(db);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
        System.out.println("End @ " + DateUtil.now());
	}

}
