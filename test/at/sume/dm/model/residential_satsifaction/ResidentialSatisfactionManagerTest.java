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
import at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager;
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
	 * Setup for unit test of {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	@Before
	public void setUp() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		db = Common.openDatabase();
		Common.init();
		// Household 1: 2 persons, 30000 + 20000
		hh = new Households();
		hh.setDb(db);
		dw = new Dwellings();
		dw.setDb(db);
		hhr1 = new HouseholdRow();
		hhr1.setId(1);
		dr = new DwellingRow();
		hhr1.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setTotalYearlyDwellingCosts(12000);
		dr.setDwellingSize((short) 90);
		dw.add(dr);
		Persons p;
		p = new Persons();
		p.setDb(db);
		
		PersonRow pr = new PersonRow();
		pr.setId(1);
		pr.setAge((byte)45);
		pr.setSex((byte)2);
		pr.setYearlyIncome(30000);
		hhr1.addMember(pr);
		
		pr = new PersonRow();
		pr.setId(2);
		pr.setAge((byte)40);
		pr.setSex((byte)1);
		pr.setYearlyIncome(20000);
		hhr1.addMember(pr);
		hhr1.determineInitialHouseholdType(true);
		hh.add(hhr1);
		
		// Household 1: 2 persons + 1 child, 30000 + 0
		hhr2 = new HouseholdRow();
		hhr2.setId(2);
		dr = new DwellingRow();
		hhr2.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setTotalYearlyDwellingCosts(15000);
		dr.setDwellingSize((short) 120);
		dw.add(dr);
		
		pr = new PersonRow();
		pr.setId(3);
		pr.setAge((byte)35);
		pr.setSex((byte)2);
		pr.setYearlyIncome(30000);
		hhr2.addMember(pr);
		
		pr = new PersonRow();
		pr.setId(4);
		pr.setAge((byte)37);
		pr.setSex((byte)1);
		pr.setYearlyIncome(0);
		hhr2.addMember(pr);
		
		pr = new PersonRow();
		pr.setId(5);
		pr.setAge((byte)6);
		pr.setSex((byte)1);
		pr.setYearlyIncome(0);
		hhr2.addMember(pr);
		hhr2.determineInitialHouseholdType(true);
		hh.add(hhr2);
		
		spatialUnits = new SpatialUnits(db, Common.getSpatialUnitLevel());
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
