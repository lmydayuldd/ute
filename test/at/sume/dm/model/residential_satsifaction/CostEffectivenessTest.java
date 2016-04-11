/**
 * 
 */
package at.sume.dm.model.residential_satsifaction;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import net.remesch.db.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.entities.DwellingRow;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.entities.SpatialUnitRow;
import at.sume.dm.entities.SpatialUnits;
import at.sume.dm.model.residential_satisfaction.CostEffectiveness;
import at.sume.dm.tracing.ObjectSource;


/**
 * @author Alexander Remesch
 *
 */
public class CostEffectivenessTest {
	CostEffectiveness costEffectiveness;
	Database db;
//	Households hh;
	HouseholdRow hhr1, hhr2, hhr3, hhr4;
	SpatialUnitRow sur1, sur2;

	@Test(expected=AssertionError.class)
	  public void testAssertionsEnabled() {
	    assert(false);
	  }

	/**
	 * Setup for unit test of {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	@Before
	public void setUp() throws SQLException, ClassNotFoundException {
		db = Common.openDatabase();
		Common.init();
		sur1 = new SpatialUnitRow(ObjectSource.INIT);
		sur1.setSpatialUnitId(90101);
//		sur2 = new SpatialUnitRow();
//		sur2.setSpatialUnitId(90102)
		// Household 1: 2 persons, 30000 + 20000
//		hh = new Households();
//		hh.setDb(db);
		hhr1 = new HouseholdRow(ObjectSource.INIT);
		hhr1.setId(1);
		DwellingRow dr = new DwellingRow(ObjectSource.INIT);
		hhr1.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setSpatialunit(sur1);
		dr.setTotalYearlyDwellingCosts(12000);
		dr.setDwellingSize((short) 90);
		Persons p;
		p = new Persons();
		p.setDb(db);
		
		PersonRow pr = new PersonRow(ObjectSource.INIT);
		pr.setId(1);
		pr.setAge((byte)45);
		pr.setSex((byte)2);
		pr.setYearlyIncome(30000);
		hhr1.addMember(pr);
		
		pr = new PersonRow(ObjectSource.INIT);
		pr.setId(2);
		pr.setAge((byte)40);
		pr.setSex((byte)1);
		pr.setYearlyIncome(20000);
		hhr1.addMember(pr);
		hhr1.determineInitialHouseholdType(true);
		
		// Household 2: 2 persons + 1 child, 30000 + 0
		hhr2 = new HouseholdRow(ObjectSource.INIT);
		hhr2.setId(2);
		dr = new DwellingRow(ObjectSource.INIT);
		hhr2.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setSpatialunit(sur1);
		dr.setTotalYearlyDwellingCosts(15000);
		dr.setDwellingSize((short) 120);
		
		pr = new PersonRow(ObjectSource.INIT);
		pr.setId(3);
		pr.setAge((byte)35);
		pr.setSex((byte)2);
		pr.setYearlyIncome(30000);
		hhr2.addMember(pr);
		
		pr = new PersonRow(ObjectSource.INIT);
		pr.setId(4);
		pr.setAge((byte)37);
		pr.setSex((byte)1);
		pr.setYearlyIncome(0);
		hhr2.addMember(pr);
		
		pr = new PersonRow(ObjectSource.INIT);
		pr.setId(5);
		pr.setAge((byte)6);
		pr.setSex((byte)1);
		pr.setYearlyIncome(0);
		hhr2.addMember(pr);
		hhr2.determineInitialHouseholdType(true);
		
		// Household 3: immigration household - no current dwelling, 2 persons + 1 child, 4000 + 4000
		hhr3 = new HouseholdRow(ObjectSource.INIT);
		hhr3.setId(3);
		p = new Persons();
		p.setDb(db);
		
		pr = new PersonRow(ObjectSource.INIT);
		pr.setId(5);
		pr.setAge((byte)28);
		pr.setSex((byte)2);
		pr.setYearlyIncome(4000);
		hhr3.addMember(pr);
		
		pr = new PersonRow(ObjectSource.INIT);
		pr.setId(6);
		pr.setAge((byte)26);
		pr.setSex((byte)1);
		pr.setYearlyIncome(4000);
		hhr3.addMember(pr);
		hhr3.determineInitialHouseholdType(true);

		// Household 3: immigration household - no current dwelling, 2 persons + 1 child, 4000 + 4000
		hhr4 = new HouseholdRow(ObjectSource.INIT);
		hhr4.setId(4);
		p = new Persons();
		p.setDb(db);
		
		pr = new PersonRow(ObjectSource.INIT);
		pr.setId(7);
		pr.setAge((byte)80);
		pr.setSex((byte)1);
		pr.setYearlyIncome(32063);
		hhr4.addMember(pr);
		hhr4.determineInitialHouseholdType(true);
		dr = new DwellingRow(ObjectSource.INIT);
		hhr4.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setSpatialunit(sur1);
		dr.setTotalYearlyDwellingCosts(120);
		dr.setDwellingSize((short) 156);
	}

	/**
	 * Test method for {@link at.sume.dm.model.residential_satisfaction.UDPCentrality#calc(at.sume.dm.entities.HouseholdRow, at.sume.dm.entities.SpatialUnitRow, int)}.
	 */
	@Test
	public void testCalc() {
		costEffectiveness = new CostEffectiveness();

		SpatialUnits su = new SpatialUnits();
		su.setDb(db);
		SpatialUnitRow sur = new SpatialUnitRow(ObjectSource.INIT);
		sur.setSpatialUnitId(92222);
		long residentialSatisfaction1 = costEffectiveness.calc(hhr1, sur, 2001);
//		System.out.println(residentialSatisfaction1);
		// TODO: Values here are not checked!!!!
		assertEquals("Residential satisfaction hh1", 615, residentialSatisfaction1);
		long residentialSatisfaction2 = costEffectiveness.calc(hhr2, sur, 2001);
//		System.out.println(residentialSatisfaction2);
		// TODO: Values here are not checked!!!!
		assertEquals("Residential satisfaction hh2", 656, residentialSatisfaction2);
		long residentialSatisfaction3 = costEffectiveness.calc(hhr3, sur, 2001);
		assertEquals("Residential satisfaction hh3/3", 1000, residentialSatisfaction3);
//		System.out.println(residentialSatisfaction3);
		long residentialSatisfaction4 = costEffectiveness.calc(hhr3, hhr1.getDwelling(), 2001);
		assertEquals("Residential satisfaction hh3/4", 1000, residentialSatisfaction4);
//		System.out.println(residentialSatisfaction4);
		long residentialSatisfaction5 = costEffectiveness.calc(hhr4, sur, 2001);
		assertEquals("Residential satisfaction hh5", 1000, residentialSatisfaction5);
	}
}
