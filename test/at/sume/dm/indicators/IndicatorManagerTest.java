/**
 * 
 */
package at.sume.dm.indicators;

import static org.junit.Assert.*;

import java.sql.SQLException;

import net.remesch.util.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.indicators.IndicatorManager;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;

/**
 * @author Alexander Remesch
 *
 */
public class IndicatorManagerTest {

	/**
	 * Setup for unit test of {@link at.sume.dm.indicators.IndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 * @throws SQLException
	 */
	@Before
	public void setUp() throws SQLException {
		Database db = Common.openDatabase();
		// Household 1: 2 persons, 30000 + 20000
		Households hh;
		hh = new Households();
		hh.setDb(db);
		HouseholdRow hhr = new HouseholdRow(hh);
		hhr.setId(1);
		hhr.setHouseholdSize((short)2);
		hhr.setSpatialunitId(90101);
		hhr.setCostOfResidence(3000);
		hhr.setLivingSpace(90);
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
		hhr.addMember(pr);
		
		pr = new PersonRow(p);
		pr.setId(2);
		pr.setAge((short)40);
		pr.setSex((short)1);
		pr.setHouseholdRepresentative(false);
		pr.setPersonNrInHousehold((short)2);
		pr.setYearlyIncome(20000);
		hhr.addMember(pr);
		IndicatorManager.addHousehold(hhr);
		
		// Household 1: 2 persons + 1 child, 30000 + 0
		hhr = new HouseholdRow(hh);
		hhr.setId(2);
		hhr.setHouseholdSize((short)3);
		hhr.setSpatialunitId(90101);
		hhr.setCostOfResidence(5000);
		hhr.setLivingSpace(120);
		
		pr = new PersonRow(p);
		pr.setId(3);
		pr.setAge((short)35);
		pr.setSex((short)2);
		pr.setHouseholdRepresentative(true);
		pr.setPersonNrInHousehold((short)1);
		pr.setYearlyIncome(30000);
		hhr.addMember(pr);
		
		pr = new PersonRow(p);
		pr.setId(4);
		pr.setAge((short)37);
		pr.setSex((short)1);
		pr.setHouseholdRepresentative(false);
		pr.setPersonNrInHousehold((short)2);
		pr.setYearlyIncome(0);
		hhr.addMember(pr);
		
		pr = new PersonRow(p);
		pr.setId(5);
		pr.setAge((short)6);
		pr.setSex((short)1);
		pr.setHouseholdRepresentative(false);
		pr.setPersonNrInHousehold((short)3);
		pr.setYearlyIncome(0);
		hhr.addMember(pr);
		IndicatorManager.addHousehold(hhr);
	}

	/**
	 * Test method for {@link at.sume.dm.indicators.IndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 */
	@Test
	public void testGetAvgHouseholdIncome() {
		assertEquals("Avg household income", 40000, IndicatorsPerSpatialUnit.getAvgHouseholdIncome(90101));
	}

	/**
	 * Test method for {@link at.sume.dm.indicators.IndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 */
	@Test
	public void testGetAvgHouseholdIncomePerMember() {
		assertEquals("Avg household income", 17500, IndicatorsPerSpatialUnit.getAvgHouseholdIncomePerMember(90101));
	}

	/**
	 * Test method for {@link at.sume.dm.indicators.IndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 */
	@Test
	public void testGetAvgHouseholdIncomePerMemberWeighted() {
		assertEquals("Avg household income", 18500, IndicatorsPerSpatialUnit.getAvgHouseholdIncomePerMemberWeighted(90101));
	}

	/**
	 * Test method for {@link at.sume.dm.indicators.IndicatorManager#IndicatorManager(java.lang.String, java.lang.Class)}.
	 */
	@Test
	public void testGetAvgPersonIncome() {
		assertEquals("Avg household income", 16000, IndicatorsPerSpatialUnit.getAvgPersonIncome(90101));
	}

}
