/**
 * 
 */
package at.sume.dm.model.residential_satsifaction;

import static org.junit.Assert.*;

import java.sql.SQLException;

import net.remesch.db.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.model.residential_satisfaction.UDPCentrality;

/**
 * @author Alexander Remesch
 *
 */
public class UDPClassificationTest {
	UDPCentrality udpClassification;
	Database db;
	Households hh;
	HouseholdRow hhr1, hhr2;

	@Test(expected=AssertionError.class)
	  public void testAssertionsEnabled() {
	    assert(false);
	  }

	/**
	 * Setup for unit test of {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 */
	@Before
	public void setUp() throws SQLException {
		db = Common.openDatabase();
		Common.init();
		// Household 1: 2 persons, 30000 + 20000
		hh = new Households();
		hh.setDb(db);
		hhr1 = new HouseholdRow();
		hhr1.setId(1);
		DwellingRow dr = new DwellingRow();
		hhr1.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setTotalYearlyDwellingCosts(3000);
		dr.setDwellingSize((short) 90);
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
		
		// Household 1: 2 persons + 1 child, 30000 + 0
		hhr2 = new HouseholdRow();
		hhr2.setId(2);
		dr = new DwellingRow();
		hhr1.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setTotalYearlyDwellingCosts(5000);
		dr.setDwellingSize((short) 120);
		
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
	}

	/**
	 * Test method for {@link at.sume.dm.model.residential_satisfaction.UDPCentrality#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.SpatialUnitRow, int)}.
	 */
	@Test
	public void testCalc() {
		udpClassification = new UDPCentrality();

		SpatialUnits su = new SpatialUnits();
		su.setDb(db);
		SpatialUnitRow sur = new SpatialUnitRow();
		sur.setSpatialUnitId(92222);
		long residentialSatisfaction1 = udpClassification.calc(hhr1, sur, 2001);
		assertEquals("Residential satisfaction hh1", 500, residentialSatisfaction1);
		residentialSatisfaction1 = udpClassification.calc(hhr1, sur, 2010);
		assertEquals("Residential satisfaction hh1", 625, residentialSatisfaction1);
		long residentialSatisfaction2 = udpClassification.calc(hhr2, sur, 2001);
		assertEquals("Residential satisfaction hh2", 500, residentialSatisfaction2);
		residentialSatisfaction2 = udpClassification.calc(hhr2, sur, 2010);
		assertEquals("Residential satisfaction hh2", 750, residentialSatisfaction2);
	}
}
