/**
 * 
 */
package at.sume.dm.model.residential_satsifaction;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import net.remesch.util.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.model.residential_satisfaction.CostEffectiveness;


/**
 * @author Alexander Remesch
 *
 */
public class CostEffectivenessTest {
	CostEffectiveness costEffectiveness;
	Database db;
	Households hh;
	HouseholdRow hhr1, hhr2;

	@Test(expected=AssertionError.class)
	  public void testAssertionsEnabled() {
	    assert(false);
	  }

	/**
	 * Setup for unit test of {@link at.sume.dm.indicators.HouseholdIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 */
	@Before
	public void setUp() throws SQLException {
		db = Common.openDatabase();
		Common.init();
		// Household 1: 2 persons, 30000 + 20000
		hh = new Households();
		hh.setDb(db);
		hhr1 = new HouseholdRow(hh);
		hhr1.setId(1);
		hhr1.setHouseholdSize((short)2);
		hhr1.setSpatialunitId(90101);
		hhr1.setCostOfResidence(12000);
		hhr1.setLivingSpace(90);
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
		
		// Household 1: 2 persons + 1 child, 30000 + 0
		hhr2 = new HouseholdRow(hh);
		hhr2.setId(2);
		hhr2.setHouseholdSize((short)3);
		hhr2.setSpatialunitId(90101);
		hhr2.setCostOfResidence(15000);
		hhr2.setLivingSpace(120);
		
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
	}

	/**
	 * Test method for {@link at.sume.dm.model.residential_satisfaction.UDPClassification#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.SpatialUnitRow, int)}.
	 */
	@Test
	public void testCalc() {
		costEffectiveness = new CostEffectiveness();

		SpatialUnits su = new SpatialUnits();
		su.setDb(db);
		SpatialUnitRow sur = new SpatialUnitRow(su);
		sur.setSpatialUnitId(92222);
		double residentialSatisfaction1 = costEffectiveness.calc(hhr1, sur, 2001);
//		System.out.println(residentialSatisfaction1);
		assertEquals("Residential satisfaction hh1", 0.615, residentialSatisfaction1, 0.0);
		double residentialSatisfaction2 = costEffectiveness.calc(hhr2, sur, 2001);
//		System.out.println(residentialSatisfaction2);
		assertEquals("Residential satisfaction hh2", 0.656, residentialSatisfaction2, 0.0);
	}
}
