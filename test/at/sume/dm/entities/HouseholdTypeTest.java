package at.sume.dm.entities;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import net.remesch.db.Database;

import org.junit.Before;
import org.junit.Test;

import at.sume.dm.Common;
import at.sume.dm.tracing.ObjectSource;
import at.sume.dm.types.HouseholdType;

public class HouseholdTypeTest {
	Database db;
	HouseholdRow hh;
	PersonRow p;
	
	@Test(expected=AssertionError.class)
	public void testAssertionsEnabled() {
	    assert(false);
	}

	@Before
	public void setUp() throws SQLException, ClassNotFoundException {
		db = Common.openDatabase();
		Common.init();
		hh = new HouseholdRow(ObjectSource.INIT);
	}
	
	@Test
	public void testSingleYoung() {
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)45);
		p.setSex((byte)2);
		hh.addMember(p);
		HouseholdType result = hh.determineInitialHouseholdType(true);
		assertEquals("Single male 45 yrs.", HouseholdType.SINGLE_YOUNG, result);
	}
	
	@Test
	public void testCoupleYoung() {
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)45);
		p.setSex((byte)2);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)35);
		p.setSex((byte)1);
		hh.addMember(p);
		HouseholdType result = hh.determineInitialHouseholdType(true);
		assertEquals("Couple, fem = 35 yrs., male = 45 yrs.", HouseholdType.COUPLE_YOUNG, result);
	}
	
	@Test
	public void testSmallFamily() {
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)45);
		p.setSex((byte)2);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)35);
		p.setSex((byte)1);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)10);
		p.setSex((byte)1);
		hh.addMember(p);
		HouseholdType result = hh.determineInitialHouseholdType(true);
		assertEquals("Couple, fem = 35 yrs., male = 45 yrs., 1 child", HouseholdType.SMALL_FAMILY, result);
	}

	@Test
	public void testLargeFamily1() {
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)45);
		p.setSex((byte)2);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)35);
		p.setSex((byte)1);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)10);
		p.setSex((byte)1);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)8);
		p.setSex((byte)1);
		hh.addMember(p);
		HouseholdType result = hh.determineInitialHouseholdType(true);
		assertEquals("Couple, fem = 35 yrs., male = 45 yrs., 2 children", HouseholdType.LARGE_FAMILY, result);
	}
	
	@Test
	public void testLargeFamily2() {
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)45);
		p.setSex((byte)2);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)35);
		p.setSex((byte)1);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)10);
		p.setSex((byte)1);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)8);
		p.setSex((byte)1);
		hh.addMember(p);
		p = new PersonRow(ObjectSource.INIT);
		p.setAge((byte)6);
		p.setSex((byte)1);
		hh.addMember(p);
		HouseholdType result = hh.determineInitialHouseholdType(true);
		assertEquals("Couple, fem = 35 yrs., male = 45 yrs., 3 children", HouseholdType.LARGE_FAMILY, result);
	}
}
