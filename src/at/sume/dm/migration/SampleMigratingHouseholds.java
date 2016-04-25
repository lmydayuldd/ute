/**
 * 
 */
package at.sume.dm.migration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.tracing.ObjectSource;
import at.sume.dm.types.AgeGroup;
import at.sume.dm.types.IncomeGroup;
import at.sume.dm.types.MigrationRealm;
import at.sume.sampling.Distribution;
import net.remesch.util.Random;

/**
 * @author Alexander Remesch
 */
public class SampleMigratingHouseholds {
	public static class MigrationsPerAgeSex implements Comparable<MigrationsPerAgeSex> {
		private int id;
		private byte ageGroupId;
		private byte sex;
		public double share;
		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(int id) {
			this.id = id;
		}
		/**
		 * @return the ageGroupId
		 */
		public byte getAgeGroupId() {
			return ageGroupId;
		}
		/**
		 * @param ageGroupId the ageGroupId to set
		 */
		public void setAgeGroupId(byte ageGroupId) {
			this.ageGroupId = ageGroupId;
		}
		/**
		 * @return the sex
		 */
		public byte getSex() {
			return sex;
		}
		/**
		 * @param sex the sex to set
		 */
		public void setSex(byte sex) {
			this.sex = sex;
		}
		/**
		 * @return the share
		 */
		public double getShare() {
			return share;
		}
		/**
		 * @param share the share to set
		 */
		public void setShare(double share) {
			this.share = share;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(MigrationsPerAgeSex o) {
			return ((Integer)id).compareTo(o.id);
		}
	}
	public static class MigrationHouseholdSize implements Comparable<MigrationHouseholdSize> {
		private int id;
		private byte householdSize;
		private short share;
		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(int id) {
			this.id = id;
		}
		/**
		 * @return the householdSize
		 */
		public byte getHouseholdSize() {
			return householdSize;
		}
		/**
		 * @param householdSize the householdSize to set
		 */
		public void setHouseholdSize(byte householdSize) {
			this.householdSize = householdSize;
		}
		/**
		 * @return the share
		 */
		public short getShare() {
			return share;
		}
		/**
		 * @param share the share to set
		 */
		public void setShare(short share) {
			this.share = share;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(MigrationHouseholdSize o) {
			return ((Integer)id).compareTo(o.id);
		}
	}
	public static class MigrationIncomeRow {
		private byte incomeGroupId;
		public double probability;
		/**
		 * @return the incomeGroupId
		 */
		public byte getIncomeGroupId() {
			return incomeGroupId;
		}
		/**
		 * @param incomeGroupId the incomeGroupId to set
		 */
		public void setIncomeGroupId(byte incomeGroupId) {
			this.incomeGroupId = incomeGroupId;
		}
		/**
		 * @return the probability
		 */
		public double getProbability() {
			return probability;
		}
		/**
		 * @param probability the probability to set
		 */
		public void setProbability(double probability) {
			this.probability = probability;
		}
	}
	private TotalMigrationPerYear totalMigrationsPerYear;
	private Distribution<MigrationsPerAgeSex> migrationsPerAgeSex;
	private Distribution<MigrationIncomeRow> migrationIncome;
	private List<MigrationHouseholdSize> migrationHouseholdSize;
	private long migrationHouseholdSizeShareTotal = 0;
	private byte immigrationWorkplaceShare = 0;
	private Random r = new Random();
	// The following two Lists are needed in order not to skew the random distribution and remember already create persons for later use
	// Problem with this is that in sequential creation of household sizes you always tend to have large hangovers of either children or adults
	// so that you never can use all of them and always tend to have a bias in sex/age distributions of persons
	private List<PersonRow> spareAdults = new ArrayList<PersonRow>();
	private List<PersonRow> spareChildren = new ArrayList<PersonRow>();
	
	public SampleMigratingHouseholds(String migrationScenarioName, String migrationPerAgeSexScenarioName, String migrationHouseholdSizeScenarioName, String migrationIncomeScenarioName) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String selectStatement;
		selectStatement = "SELECT id, ageGroupId, sex, share " +
			"FROM _DM_MigrationAgeSex " +
			"WHERE scenarioName = '" + migrationPerAgeSexScenarioName + "' " +
			"ORDER BY ageGroupId, sex";
		migrationsPerAgeSex = new Distribution<MigrationsPerAgeSex>(Common.db.select(MigrationsPerAgeSex.class, selectStatement), "share");
		assert migrationsPerAgeSex.size() > 0 : "No rows selected from _DM_MigrationAgeSex (scenarioName = " + migrationPerAgeSexScenarioName + ")";

		selectStatement = "SELECT id, householdSize, share " +
			"FROM _DM_MigrationHouseholdSize " +
			"WHERE scenarioName = '" + migrationHouseholdSizeScenarioName + "' " +
			"ORDER BY householdSize";
		migrationHouseholdSize = Common.db.select(MigrationHouseholdSize.class, selectStatement);
		assert migrationHouseholdSize.size() > 0 : "No rows selected from _DM_MigrationHouseholdSize (scenarioName = " + migrationHouseholdSizeScenarioName + ")";
		migrationHouseholdSizeShareTotal = (Integer)Common.db.lookupSql("select sum(share) from _DM_MigrationHouseholdSize where scenarioName = '" + migrationHouseholdSizeScenarioName + "'");
		
		totalMigrationsPerYear = new TotalMigrationPerYear(migrationScenarioName);
		
		selectStatement = "SELECT incomeGroupId, probability from _DM_MigrationIncome " + 
			"where scenarioName = '" + migrationIncomeScenarioName + "' order by incomeGroupId";
		migrationIncome = new Distribution<MigrationIncomeRow>(Common.db.select(MigrationIncomeRow.class, selectStatement), "probability");
		// Sampling of workplaces
		immigrationWorkplaceShare = Byte.parseByte(Common.getSysParam("ImmigrationWorkplaceShare"));
	}
	
	public ArrayList<HouseholdRow> sample(int modelYear, MigrationRealm migrationRealm) {
		ArrayList<HouseholdRow> result = new ArrayList<HouseholdRow>();
		
		// 1) Get number of persons immigrating in that year
		long numImmigrants = totalMigrationsPerYear.getImmigration(modelYear, migrationRealm);
		//    and calculate the exact age & sex distribution
//		migrationsPerAgeSex.buildExactThresholds(numImmigrants);
//		migrationIncome.buildExactThresholds(numImmigrants);
		
		// 2) Calculate the number of households per household size
		int numHouseholds = 0;
		// immigrants with household size >= migrationHouseholdSize.size()
		int remainingNumImmigrants = Math.round(numImmigrants * migrationHouseholdSize.get(migrationHouseholdSize.size() - 1).getShare() / migrationHouseholdSizeShareTotal);
		for (short householdSize = 0; householdSize != 9; householdSize++) {
			if (householdSize < migrationHouseholdSize.size() - 1) {
				// calculate the share of households from the distribution migrationHouseholdSize
				int actualNumImmigrants = Math.round(numImmigrants * migrationHouseholdSize.get(householdSize).getShare() / migrationHouseholdSizeShareTotal);
				numHouseholds = Math.round(actualNumImmigrants / (householdSize + 1));
			} else {
				// distribute remainingNumImmigrants on all remaining household sizes
				if (remainingNumImmigrants > 0) {
					if (householdSize == 8) {
						if ((householdSize + 1) == remainingNumImmigrants) {
							numHouseholds = 1;
						} else {
							numHouseholds = Math.round(remainingNumImmigrants / (householdSize + 1));
						}
					} else {
						if ((householdSize + 1) == remainingNumImmigrants) {
							numHouseholds = 1;
						} else {
							int actualNumImmigrants = r.nextInt(remainingNumImmigrants);
							numHouseholds = Math.round(actualNumImmigrants / (householdSize + 1));
						}
						remainingNumImmigrants -= numHouseholds * (householdSize + 1);
					}
				} else {
					numHouseholds = 0;
				}
			}
			for (int i = 0; i != numHouseholds; i++) {
				HouseholdRow household = sampleHousehold((short) (householdSize + 1), modelYear);
				result.add(household);
			}
		}
		System.out.println(Common.printInfo() + ": Left over spare adults = " + spareAdults.size());
		System.out.println(Common.printInfo() + ": Left over spare children = " + spareChildren.size());
		return result;
	}
	/**
	 * Indicate whether an adult is needed during sampling for the given household parameters
	 * @param householdSize
	 * @param adultCount
	 * @param childrenCount
	 * @return
	 */
	private boolean adultNeeded(short householdSize, short adultCount) {
		boolean result;
		if (householdSize == 1) {
			result = true;
		} else {
			result =  adultCount < 1 ? true : false;
		}
		return result;
	}
	/**
	 * Indicate whether a child is needed during sampling for the given household parameters
	 * @param householdSize
	 * @param adultCount
	 * @return
	 */
	private boolean childNeeded(short householdSize, short adultCount) {
		return ((householdSize > 2) && (adultCount == 2)) ? true : false;
	}
	/**
	 * Sample a household
	 * 
	 * @param householdSize
	 * @return
	 */
	private HouseholdRow sampleHousehold(short householdSize, int modelYear) {
		ArrayList<PersonRow> members = new ArrayList<PersonRow>(householdSize);
		HouseholdRow result = new HouseholdRow(ObjectSource.IMMIGRATION);
		result.setMovingDecisionYear((short) modelYear);
		// sample persons
		short adultCount = 0;
		while (members.size() != householdSize) {
			PersonRow person;
			if (adultNeeded(householdSize, adultCount)) {
				if (spareAdults.size() > 0) {			// Adults available?
					person = spareAdults.remove(0);		// Yes -> get them from the list
				} else {								// No -> sample them
					person = samplePerson();
					if (!person.isAdult()) {
						// we don't have an adult -> put on list for later & sample next person
						spareChildren.add(person);
						continue;
					}
				}
			} else if (childNeeded(householdSize, adultCount)) {
				if (spareChildren.size() > 0) {
					person = spareChildren.remove(0);
				} else {
					person = samplePerson();
					if (person.isAdult()) {
						// we don't have a child -> put on list for later & sample next person
						spareAdults.add(person);
						continue;
					}
				}
			} else { // Nothing special needed -> supply any person
				if ((spareAdults.size() > 0) && (spareAdults.size() > spareChildren.size())) {
					person = spareAdults.remove(0);
				} else if (spareChildren.size() > 0) {
					person = spareChildren.remove(0);
				} else {
					person = samplePerson();
				}
			}
			// adds person to the list of household members for later addition to the household (below)
			person.setHousehold(result);
			members.add(person);
			if (person.isAdult()) adultCount++;
		}
		result.addMembers(members);
		assert result.getHouseholdSize() == householdSize : "Invalid household size: " + result.getHouseholdSize() + ", should be: " + householdSize;
		result.determineInitialHouseholdType(false);	// countAdults() was already done in addMembers()
		// calculate income for each household member
		for (PersonRow person : result.getMembers()) {
			int yearlyIncome = 0;
			// Alternative income calculation - just get the income from the income distribution of the present population as a quick solution to
			// have the income distribution of the current residents for the immigrants
			if (person.getAge() >= 15 && person.getAge() <= 64) {
				// The following calculation has a bias for very high household incomes (after 50 yrs.) about 70% of all households have an income > 200.000 €
				// Solution: calculate income by choosing an existing person and taking their income (each person has the same chance to be picked)
				// TODO: Is the above still true?
				if (r.nextInt(100) < immigrationWorkplaceShare) {
					int index = migrationIncome.randomSample();
					yearlyIncome = IncomeGroup.sampleIncome(migrationIncome.get(index).getIncomeGroupId());
					// set workplace temporarily (we cannot sample it here since we don't know where the household lives yet)
					person.setWorkplaceCellId(-1);
				}
			}
			person.setYearlyIncome(yearlyIncome);
		}
		// TODO: each household requires at least one adult (is this a good idea? - maybe not for single households)
		return result;
	}
	/**
	 * Sample a single person
	 * @return
	 */
	private PersonRow samplePerson() {
		PersonRow person = new PersonRow(ObjectSource.IMMIGRATION);
		int index = migrationsPerAgeSex.randomSample();
		MigrationsPerAgeSex m = migrationsPerAgeSex.get(index);
		person.setAgeGroupId(m.getAgeGroupId());
		person.setAge(AgeGroup.sampleAge(person.getAgeGroupId()));
		person.setSex(m.getSex());
		return person;
	}
	/**
	 * Get total international out migration (nr. of persons) for the given year
	 * @param modelYear
	 * @return
	 */
	public int getOutMigrationInternational(int modelYear) {
		return totalMigrationsPerYear.getOutMigrationInternational(modelYear);
	}
	/**
	 * Get total national out migration (nr. of persons) for the given year
	 * @param modelYear
	 * @return
	 */
	public int getOutMigrationNational(int modelYear) {
		return totalMigrationsPerYear.getOutMigrationNational(modelYear);
	}
}
