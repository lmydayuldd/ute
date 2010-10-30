/**
 * 
 */
package at.sume.dm.model.residential_satsifaction;

import java.sql.SQLException;

import net.remesch.db.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.Dwellings;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.indicators.AllHouseholdsIndicatorManager;
import at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionManager;

/**
 * @author Alexander Remesch
 *
 */
public class ResidentialSatisfactionManagerTest {
	Database db;
	Households hh;
	Dwellings dw;
	HouseholdRow hhr1, hhr2;
	SpatialUnits spatialUnits;
	DwellingRow dr;

	@Test(expected=AssertionError.class)
	  public void testAssertionsEnabled() {
	    assert(false);
	  }

	/**
	 * Setup for unit test of {@link at.sume.dm.indicators.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Before
	public void setUp() throws SQLException, InstantiationException, IllegalAccessException {
		db = Common.openDatabase();
		Common.init();
		// Household 1: 2 persons, 30000 + 20000
		hh = new Households();
		hh.setDb(db);
		dw = new Dwellings();
		dw.setDb(db);
		hhr1 = new HouseholdRow(hh);
		hhr1.setId(1);
		hhr1.setHouseholdSize((short)2);
		dr = new DwellingRow();
		hhr1.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setDwellingCosts(12000);
		dr.setDwellingSize(90);
		dw.add(dr);
		Persons p;
		p = new Persons();
		p.setDb(db);
		
		PersonRow pr = new PersonRow(p);
		pr.setId(1);
		pr.setAge((short)45);
		pr.setSex((short)2);
		pr.setHouseholdRepresentative(true);
		pr.setPersonNrInHousehold((short)1);
		pr.setYearlyIncome(30000);
		hhr1.addMember(pr);
		
		pr = new PersonRow(p);
		pr.setId(2);
		pr.setAge((short)40);
		pr.setSex((short)1);
		pr.setHouseholdRepresentative(false);
		pr.setPersonNrInHousehold((short)2);
		pr.setYearlyIncome(20000);
		hhr1.addMember(pr);
		hhr1.determineInitialHouseholdType();
		hh.add(hhr1);
		
		// Household 1: 2 persons + 1 child, 30000 + 0
		hhr2 = new HouseholdRow(hh);
		hhr2.setId(2);
		hhr2.setHouseholdSize((short)3);
		dr = new DwellingRow();
		hhr2.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setDwellingCosts(15000);
		dr.setDwellingSize(120);
		dw.add(dr);
		
		pr = new PersonRow(p);
		pr.setId(3);
		pr.setAge((short)35);
		pr.setSex((short)2);
		pr.setHouseholdRepresentative(true);
		pr.setPersonNrInHousehold((short)1);
		pr.setYearlyIncome(30000);
		hhr2.addMember(pr);
		
		pr = new PersonRow(p);
		pr.setId(4);
		pr.setAge((short)37);
		pr.setSex((short)1);
		pr.setHouseholdRepresentative(false);
		pr.setPersonNrInHousehold((short)2);
		pr.setYearlyIncome(0);
		hhr2.addMember(pr);
		
		pr = new PersonRow(p);
		pr.setId(5);
		pr.setAge((short)6);
		pr.setSex((short)1);
		pr.setHouseholdRepresentative(false);
		pr.setPersonNrInHousehold((short)3);
		pr.setYearlyIncome(0);
		hhr2.addMember(pr);
		hhr2.determineInitialHouseholdType();
		hh.add(hhr2);
		
		spatialUnits = new SpatialUnits(db);
		dw.linkSpatialUnits(spatialUnits);

		AllHouseholdsIndicatorManager.resetIndicators();
		for (HouseholdRow household : hh) {
			AllHouseholdsIndicatorManager.addHousehold(household);
		}
	}

	/**
	 * Test method for {@link at.sume.dm.model.residential_satisfaction.ResidentialSatisfactionManager#calcResidentialSatisfaction(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.SpatialUnitRow, int)}.
	 */
	@Test
	public void testCalcResidentialSatisfactionHouseholdRowSpatialUnitRowInt() {
		int residentialSatisfaction;
		for (SpatialUnitRow su : spatialUnits) {
			residentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(hhr1, su, 2001);
			System.out.println(su.getId() + " " + residentialSatisfaction);
			residentialSatisfaction = ResidentialSatisfactionManager.calcResidentialSatisfaction(hhr2, su, 2001);
			System.out.println(su.getId() + " " + residentialSatisfaction);
		}
	}

}
