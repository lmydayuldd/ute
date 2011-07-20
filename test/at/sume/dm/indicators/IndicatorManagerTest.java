/**
 * 
 */
package at.sume.dm.indicators;

import static org.junit.Assert.assertEquals;

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
import at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager;

/**
 * @author Alexander Remesch
 *
 */
public class IndicatorManagerTest {

	/**
	 * Setup for unit test of {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	@Before
	public void setUp() throws SQLException, ClassNotFoundException {
		Database db = Common.openDatabase();
		// Household 1: 2 persons, 30000 + 20000
		Households hh;
		hh = new Households();
		hh.setDb(db);
		HouseholdRow hhr = new HouseholdRow();
		hhr.setId(1);
//		hhr.setHouseholdSize((short)2);
		DwellingRow dr = new DwellingRow();
		hhr.setDwelling(dr);
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
//		pr.setHouseholdRepresentative(true);
//		pr.setPersonNrInHousehold((short)1);
		pr.setYearlyIncome(30000);
		hhr.addMember(pr);
		
		pr = new PersonRow();
		pr.setId(2);
		pr.setAge((byte)40);
		pr.setSex((byte)1);
//		pr.setHouseholdRepresentative(false);
//		pr.setPersonNrInHousehold((short)2);
		pr.setYearlyIncome(20000);
		hhr.addMember(pr);
		hhr.determineInitialHouseholdType(true);
		AllHouseholdsIndicatorManager.addHousehold(hhr);
		
		// Household 1: 2 persons + 1 child, 30000 + 0
		hhr = new HouseholdRow();
		hhr.setId(2);
//		hhr.setHouseholdSize((short)3);
		dr = new DwellingRow();
		hhr.setDwelling(dr);
		dr.setSpatialunitId(90101);
		dr.setTotalYearlyDwellingCosts(5000);
		dr.setDwellingSize((short) 120);
		
		pr = new PersonRow();
		pr.setId(3);
		pr.setAge((byte)35);
		pr.setSex((byte)2);
//		pr.setHouseholdRepresentative(true);
//		pr.setPersonNrInHousehold((short)1);
		pr.setYearlyIncome(30000);
		hhr.addMember(pr);
		
		pr = new PersonRow();
		pr.setId(4);
		pr.setAge((byte)37);
		pr.setSex((byte)1);
//		pr.setHouseholdRepresentative(false);
//		pr.setPersonNrInHousehold((short)2);
		pr.setYearlyIncome(0);
		hhr.addMember(pr);
		
		pr = new PersonRow();
		pr.setId(5);
		pr.setAge((byte)6);
		pr.setSex((byte)1);
//		pr.setHouseholdRepresentative(false);
//		pr.setPersonNrInHousehold((short)3);
		pr.setYearlyIncome(0);
		hhr.addMember(pr);
		hhr.determineInitialHouseholdType(true);
		AllHouseholdsIndicatorManager.addHousehold(hhr);
	}

	/**
	 * Test method for {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 */
	@Test
	public void testGetAvgHouseholdIncome() {
		assertEquals("Avg household income", 40000, AllHouseholdsIndicatorsPerSpatialUnit.getAvgHouseholdIncome(90101));
	}

	/**
	 * Test method for {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 */
	@Test
	public void testGetAvgHouseholdIncomePerMember() {
		assertEquals("Avg household income", 17500, AllHouseholdsIndicatorsPerSpatialUnit.getAvgHouseholdIncomePerMember(90101));
	}

	/**
	 * Test method for {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 */
	@Test
	public void testGetAvgHouseholdIncomePerMemberWeighted() {
		assertEquals("Avg household income", 18500, AllHouseholdsIndicatorsPerSpatialUnit.getAvgHouseholdIncomePerMemberWeighted(90101));
	}

	/**
	 * Test method for {@link at.sume.dm.indicators.managers.AllHouseholdsIndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 */
	@Test
	public void testGetAvgPersonIncome() {
		assertEquals("Avg household income", 16000, AllHouseholdsIndicatorsPerSpatialUnit.getAvgPersonIncome(90101));
	}

}
