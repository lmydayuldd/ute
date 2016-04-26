/**
 * 
 */
package at.sume.dm.migration;

import java.sql.SQLException;
import java.util.ArrayList;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.tracing.ObjectSource;
import at.sume.dm.types.AgeGroup16;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.IncomeGroup;
import at.sume.dm.types.MigrationRealm;
import at.sume.sampling.ExactDistribution;
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
	private ExactDistribution<MigrationsPerAgeSex> migrationsPerAgeSex;
	private ExactDistribution<MigrationIncomeRow> migrationIncome;
	private ArrayList<MigrationHouseholdSize> migrationHouseholdSize;
	private long migrationHouseholdSizeShareTotal = 0;
	private byte immigrationWorkplaceShare = 0;
	private Random r = new Random();
	
	public SampleMigratingHouseholds(String migrationScenarioName, String migrationPerAgeSexScenarioName, String migrationHouseholdSizeScenarioName, String migrationIncomeScenarioName) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String selectStatement;
		selectStatement = "SELECT id, ageGroupId, sex, share " +
			"FROM _DM_MigrationAgeSex " +
			"WHERE scenarioName = '" + migrationPerAgeSexScenarioName + "' " +
			"ORDER BY ageGroupId, sex";
		migrationsPerAgeSex = new ExactDistribution<MigrationsPerAgeSex>(Common.db.select(MigrationsPerAgeSex.class, selectStatement), "share");
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
		migrationIncome = new ExactDistribution<MigrationIncomeRow>(Common.db.select(MigrationIncomeRow.class, selectStatement), "probability");
		// Sampling of workplaces
		immigrationWorkplaceShare = Byte.parseByte(Common.getSysParam("ImmigrationWorkplaceShare"));
	}
	
	public ArrayList<HouseholdRow> sample(int modelYear, MigrationRealm migrationRealm) {
		ArrayList<HouseholdRow> result = new ArrayList<HouseholdRow>();
		
		// 1) Get number of persons immigrating in that year
		long numImmigrants = totalMigrationsPerYear.getImmigration(modelYear, migrationRealm);
		//    and calculate the exact age & sex distribution
		migrationsPerAgeSex.buildExactThresholds(numImmigrants);
		migrationIncome.buildExactThresholds(numImmigrants);
		
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
				// AR 160425 this seriously skews the sampling algorithm in a way that children are over-represented
				// so we need to get rid of this somehow
//				while (household.getHouseholdType() == HouseholdType.OTHER) {
//					// TODO: Ignore OTHER households - this must be eliminated by the sampling algorithm in future
//					household = sampleHousehold((short) (householdSize + 1), modelYear);
//				}
				result.add(household);
			}
		}
		
		return result;
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
		for (int i = 0; i != householdSize; i++) {
			PersonRow person = new PersonRow(ObjectSource.IMMIGRATION);
			int index = migrationsPerAgeSex.randomSample();
			MigrationsPerAgeSex m = migrationsPerAgeSex.get(index);
			migrationsPerAgeSex.modifyDistribution(index);
			person.setAgeGroupId(m.getAgeGroupId());
			if (householdSize == 1) {
				person.setAge(AgeGroup16.sampleAge(person.getAgeGroupId(), (short) 18));
			} else {
				person.setAge(AgeGroup16.sampleAge(person.getAgeGroupId()));
			}
			person.setSex(m.getSex());
			person.setHousehold(result);
			// adds a person to a household twice togehter with result.addMembers(members) below - AR 160411
			members.add(person);
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
