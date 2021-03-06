/**
 * 
 */
package at.sume.dm.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.sume.db.RecordSetRowFileable;
import at.sume.dm.Common;
import at.sume.dm.indicators.simple.DemographyObservable;
import at.sume.dm.indicators.simple.DemographyObserver;
import at.sume.dm.indicators.simple.MigrationDetailsObserver;
import at.sume.dm.indicators.simple.MigrationObserver;
import at.sume.dm.model.residential_mobility.DwellingsOnMarket;
import at.sume.dm.model.timeuse.SampleTimeUse;
import at.sume.dm.model.timeuse.TimeUseType;
import at.sume.dm.model.timeuse.TimeUseTypeDesignator;
import at.sume.dm.model.timeuse.TravelTimeSamplingParameters;
import at.sume.dm.tracing.ObjectSource;
import at.sume.dm.types.AgeGroup16;
import at.sume.dm.types.AgeGroup20;
import at.sume.dm.types.MigrationRealm;
import net.remesch.db.Database;
import net.remesch.db.Sequence;
import net.remesch.db.schema.Ignore;

/**
 * @author Alexander Remesch
 *
 */
public class PersonRow extends RecordSetRowFileable<Persons> implements DemographyObservable {
	private int householdId;
	private byte sex;
//	private int yearBorn;
	private byte ageGroupId;
	private short age;
//	private boolean householdRepresentative;
	private int yearlyIncome;
	private HouseholdRow household;
//	private short personNrInHousehold;
	private int workplaceCellId;
	@Ignore
	private boolean livingWithParents;	// is the person a child living with her/his parents
										// necessary to be able to move the child out of the parental home 
	@Ignore
	private static Sequence personIdSeq = null;
	private List<TimeUseRow> timeUse;
	@Ignore
	private static SampleTimeUse sampleTimeUse;
	@Ignore
	private static TimeUseTypeDesignator determineTimeUseType;
	@Ignore
	private ObjectSource src;
	@Ignore
	private float oecdWeight;
	
	public PersonRow(ObjectSource src) {
		super();
		this.src = src;
		if (personIdSeq != null) {
			setPersonId(personIdSeq.getNext());
		}
		livingWithParents = false;
	}

	/**
	 * Set class for sampling time use for later use
	 * @param sampleTimeUse
	 */
	public static void setSampleTravelTimes(SampleTimeUse sampleTimeUse) {
		PersonRow.sampleTimeUse = sampleTimeUse;
	}
	/**
	 * Set the time use type designator for later use
	 * @param t
	 */
	public static void setTimeUseTypeDesignator(TimeUseTypeDesignator t) {
		determineTimeUseType = t;
	}
	/**
	 * Return the Time-Use Type for the person
	 * @return
	 */
	public TimeUseType getTimeUseType() {
		return determineTimeUseType.getTimeUseType(this);
	}
	/**
	 * @return the personId
	 */
	public int getPersonId() {
		return id;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(int personId) {
		this.id = personId;
	}

	/**
	 * @return the householdId
	 */
	public int getHouseholdId() {
		return householdId;
	}

	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
	}

	/**
	 * @return the sex (1 = female, 2 = male)
	 */
	public byte getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set (1 = female, 2 = male)
	 */
	public void setSex(byte sex) {
		this.sex = sex;
	}

//	/**
//	 * @return the yearBorn
//	 */
//	public int getYearBorn() {
//		return yearBorn;
//	}
//
//	/**
//	 * @param yearBorn the yearBorn to set
//	 */
//	public void setYearBorn(int yearBorn) {
//		this.yearBorn = yearBorn;
//	}

	/**
	 * @return the ageGroupId
	 */
	public byte getAgeGroupId() {
		if (ageGroupId == 0) {
			ageGroupId = AgeGroup20.getAgeGroupId(age);
		}
		assert ageGroupId > 0 : "ageGroupId <= 0";
		return ageGroupId;
	}

	public byte getAgeGroup16Id() {
		return AgeGroup16.getAgeGroupId(age);
	}
	/**
	 * @param ageGroupId the ageGroupId to set
	 */
	public void setAgeGroupId(byte ageGroupId) {
		assert ageGroupId > 0 : "ageGroupId <= 0";
		this.ageGroupId = ageGroupId;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(short age) {
		assert age >= 0 : "Age < 0 (" + age + ")";
		this.age = age;
		ageGroupId = AgeGroup20.getAgeGroupId(age);
	}

	/**
	 * @return the age
	 */
	public short getAge() {
		return age;
	}

	public void aging() {
		age++;
		assert age >= 0 : "Age < 0 (" + age + ")";
		ageGroupId = AgeGroup20.getAgeGroupId(age);
		// Create time use data for all households becoming 10 yrs
		if (age == 10)
			updateTimeUse();
	}
	
//	/**
//	 * @return the householdRepresentative
//	 */
//	public boolean isHouseholdRepresentative() {
//		return householdRepresentative;
//	}
//
//	/**
//	 * @param householdRepresentative the householdRepresentative to set
//	 */
//	public void setHouseholdRepresentative(boolean householdRepresentative) {
//		this.householdRepresentative = householdRepresentative;
//	}

	/**
	 * @return the yearlyIncome
	 */
	public int getYearlyIncome() {
		return yearlyIncome;
	}

	/**
	 * @param yearlyIncome the yearlyIncome to set
	 */
	public void setYearlyIncome(int yearlyIncome) {
		assert yearlyIncome >= 0 : "Yearly income must be > 0 (= " + yearlyIncome + ")"; 
		if (yearlyIncome > Common.getMaxYearlyPersonIncome())
			this.yearlyIncome = Common.getMaxYearlyPersonIncome();
		else
			this.yearlyIncome = yearlyIncome;
	}
	/**
	 * Return the relative OECD weight of the person in its household
	 * @return
	 */
	public float getOecdWeight() {
		return oecdWeight;
	}
	public void setOecdWeight(float oecdWeight) {
		this.oecdWeight = oecdWeight;
	}
	/**
	 * Return the OECD-weighed income for this person
	 * @return
	 */
	public int getYearlyOecdIncome() {
		return Math.round(household.getYearlyIncome() * getOecdWeight() / household.getTotalOecdWeight());
	}
	public HouseholdRow getHousehold() {
		return household;
	}

	public void setHousehold(HouseholdRow household) {
		boolean householdChange = false;
		if (this.household != null && household.hasDwelling())
			householdChange = true;
		this.household = household;
//		household.addMember(this);
		if (householdChange)
			updateTimeUse();
	}

//	/**
//	 * @return the personNrInHousehold
//	 */
//	public short getPersonNrInHousehold() {
//		return personNrInHousehold;
//	}
//
//	/**
//	 * @param personNrInHousehold the personNrInHousehold to set
//	 */
//	public void setPersonNrInHousehold(short personNrInHousehold) {
//		this.personNrInHousehold = personNrInHousehold;
//	}

	/**
	 * @return the livingWithParents
	 */
	public boolean isLivingWithParents() {
		return livingWithParents;
	}

	/**
	 * @param livingWithParents the livingWithParents to set
	 */
	public void setLivingWithParents(boolean livingWithParents) {
		this.livingWithParents = livingWithParents;
	}

	/**
	 * @return the workplace location (spatial unit id)
	 */
	public int getWorkplaceCellId() {
		return workplaceCellId;
	}

	/**
	 * @param workplaceCellId the workplace location (spatial unit id) to set
	 */
	public void setWorkplaceCellId(int workplaceId) {
		this.workplaceCellId = workplaceId;
	}

	/**
	 * @return the persons
	 */
	public Persons getPersons() {
		return recordSet;
	}

	/**
	 * @return the src
	 */
	public ObjectSource getSrc() {
		return src;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#set(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public void loadFromDatabase(ResultSet rs, String name) throws SQLException {
		if (name.equals("PersonId")) {
			setPersonId(rs.getInt(name));
		} else if (name.equals("HouseholdId")) {
			setHouseholdId(rs.getInt(name));
		} else if (name.equals("Sex")) {
			setSex(rs.getByte(name));
//		} else if (name.equals("YearBorn")) {
//			setYearBorn(rs.getInt(name));
		} else if (name.equals("Age")) {
			setAge(rs.getByte(name));
		} else if (name.equals("AgeGroupId")) {
//			setAgeGroupId(rs.getByte(name));
		} else if (name.equals("HouseholdRepresentative")) {
//			setHouseholdRepresentative(rs.getBoolean(name));
		} else if (name.equals("YearlyIncome")) {
			setYearlyIncome(rs.getInt(name));
		} else if (name.equals("PersonNrInHousehold")) {
//			setPersonNrInHousehold(rs.getShort(name));
		} else if (name.equals("WorkplaceId")) {
			setWorkplaceCellId(rs.getInt("WorkplaceId"));
		} else {
			throw new UnsupportedOperationException("Unknown field name " + name);
		}
	}
	
	/**
	 * Birth of a person in a household
	 * 
	 * @param household
	 * @return
	 */
	public static PersonRow giveBirth(HouseholdRow household) {
		PersonRow child = new PersonRow(ObjectSource.BIRTH);
		child.setHousehold(household);
		child.setLivingWithParents(true);
		household.addMember(child);
//		child.setAgeGroupId((byte) 1);
		child.setAge((byte)0);
		household.updateHouseholdTypeAfterBirth();
		child.notifyBirth(household.getSpatialunitId());
		return child;
	}
	
	/**
	 * Death of a person
	 */
	public void die() {
		notifyDeath(getHousehold().getSpatialunitId());
		household.removeMember(this);
		household.updateHouseholdTypeAfterDeathOrMemberLeaving();
		household = null;
		remove(ObjectSource.DIED, true);
	}
	
	/**
	 * Remove this record from the list of persons and the list of household members
	 */
	@Override
	public void remove() {
//		recordSet.remove(this);
		super.remove();
	}
	public void remove(ObjectSource src, boolean removeFromHousehold) {
		this.src = src;
		if (household != null && removeFromHousehold)
			household.removeMember(this);
		super.remove();
	}
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetRow#saveToDatabase()
	 */
	@Override
	public void saveToDatabase() throws SQLException {
		// TODO: make this more sophisticated depending on whether a parameter is set or not
		// INSERT: "PersonId", "HouseholdId", "Sex", "YearBorn", "AgeGroupId", "HouseholdRepresentative", "YearlyIncome"
		if (psInsert != null) {
			psInsert.setString(1, Long.toString(id));
			psInsert.setString(2, Long.toString(householdId));
			psInsert.setString(3, Short.toString(sex));
//			psInsert.setString(4, Integer.toString(yearBorn));
			psInsert.setString(4, Integer.toString(age));
			psInsert.setString(5, Short.toString(ageGroupId));
//			if (householdRepresentative) {
//				psInsert.setString(6, "-1");
//			} else {
//				psInsert.setString(6, "0");
//			}
			psInsert.setString(7, Long.toString(yearlyIncome));
//			psInsert.setString(8, Short.toString(personNrInHousehold));
		}
		// UPDATE: "HouseholdId", "Sex", "YearBorn", "AgeGroupId", "HouseholdRepresentative", "YearlyIncome", "PersonId"
		if (psUpdate != null) {
			psUpdate.setString(1, Long.toString(householdId));
			psUpdate.setString(2, Short.toString(sex));
//			psUpdate.setString(3, Integer.toString(yearBorn));
			psUpdate.setString(3, Integer.toString(age));
			psUpdate.setString(4, Short.toString(ageGroupId));
//			psUpdate.setString(5, Boolean.toString(householdRepresentative));
			psUpdate.setString(6, Long.toString(yearlyIncome));
//			psUpdate.setString(7, Short.toString(personNrInHousehold));
			// UPDATE: WHERE
			psUpdate.setString(8, Long.toString(id));
		}
	}

	/**
	 * @param personIdSeq the personIdSeq to set
	 */
	public static void setPersonIdSeq(Sequence personIdSeq) {
		PersonRow.personIdSeq = personIdSeq;
	}

	@Override
	public String toCsvHeadline(String delimiter) {
		return "ModelRun" + delimiter + 
				"PersonId" + delimiter + 
				"HouseholdId" + delimiter + 
				"Sex" + delimiter + 
				"Age" + delimiter + 
				"YearlyIncome" + delimiter + 
				"Workplace" + delimiter +
				"TimeUseType";
	}

	@Override
	public String toString(int modelRun, String delimiter) {
		return modelRun + delimiter + 
				getPersonId() + delimiter + 
				getHousehold().getHouseholdId() + delimiter + 
				getSex() + delimiter + 
				getAge() + delimiter + 
				getYearlyIncome() + delimiter +
				getWorkplaceCellId() + delimiter +
				determineTimeUseType.getTimeUseType(this).toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(0, ";");
	}

	private static ArrayList<DemographyObserver> demographyObservers = new ArrayList<DemographyObserver>();

	@Override
	public void registerDemographyObserver(DemographyObserver o) {
		int i = demographyObservers.indexOf(o);
		// Add observer only once!
		if (i < 0)
			demographyObservers.add(o);
	}

	@Override
	public void removeDemographyObserver(DemographyObserver o) {
		int i = demographyObservers.indexOf(o);
		if (i >= 0)
			demographyObservers.remove(i);
	}

	@Override
	public void notifyBirth(Integer spatialUnitId) {
		for (DemographyObserver obs : demographyObservers) {
			obs.addBirth(spatialUnitId);
		}
	}

	@Override
	public void notifyDeath(Integer spatialUnitId) {
		for (DemographyObserver obs : demographyObservers) {
			obs.addDeath(spatialUnitId);
		}
	}
	
	/**
	 * Load the time use for this person from the database
	 * @param db
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public void loadTimeUse(Database db) throws InstantiationException, IllegalAccessException, SQLException {
		String sqlStatement = "SELECT Activity, MinutesPerDay FROM _DM_TimeUse WHERE PersonId = " + id + " ORDER BY ID;";
		timeUse = db.select(TimeUseRow.class, sqlStatement);
	}
	/**
	 * Add a time use record for this person
	 * @param timeUseRow
	 */
	public void addTimeUse(TimeUseRow timeUseRow) {
		if (timeUse == null) 
			timeUse = new ArrayList<TimeUseRow>();
		timeUse.add(timeUseRow);
	}
	public static void setSampleTimeUse(SampleTimeUse sampleTimeUse) {
		PersonRow.sampleTimeUse = sampleTimeUse;
	}
	/**
	 * @return the timeUse
	 */
	public List<TimeUseRow> getTimeUse() {
		return timeUse;
	}
	/**
	 * Update person time use (e.g. after residential relocation or workplace change)
	 */
	public void updateTimeUse() {
		if (age >= 10) {
			// Prepare parameters for sampling
			TravelTimeSamplingParameters p = new TravelTimeSamplingParameters();
			p.setOrigin(household.getDwelling().getSpatialunitId());
			p.setDestination(workplaceCellId);
			p.setPersonId(getPersonId());
			if (workplaceCellId != 0) {
				p.setEmployed(true);
				p.setInEducation(false);
			} else {
				p.setInEducation(true);
				p.setEmployed(false);
			}
			p.setModelYear(Common.getModelYear());
			timeUse = sampleTimeUse.randomSample(p);
		}
	}
	/**
	 * Is the person an adult?
	 */
	public boolean isAdult() {
		return age >= 18 ? true : false;
	}
	/**
	 * Emigration of person
	 * @param migrationRealm
	 */
	public void emigrate(DwellingsOnMarket dwellingsOnMarket, MigrationRealm migrationRealm) {
		notifyEmigration(migrationRealm);
		notifyMigration(null, migrationRealm);
		if (household.getMemberCount() == 1)
			household.remove(dwellingsOnMarket, ObjectSource.EMIGRATED);
		else
			remove(ObjectSource.EMIGRATED, true);
		household = null;
	}
	public void notifyEmigration(MigrationRealm migrationRealm) {
		for (MigrationObserver obs : HouseholdRow.migrationObservers) {
			obs.addEmigration(household.getSpatialunitId(), (short) 1, migrationRealm);
		}
	}
	public void notifyMigration(Integer spatialUnitIdTo, MigrationRealm migrationRealm) {
		for (MigrationDetailsObserver obs : HouseholdRow.migrationDetailsObservers) {
			obs.addSingleMigration(household.getSpatialunitId(), spatialUnitIdTo, migrationRealm, this);
		}
	}
}
