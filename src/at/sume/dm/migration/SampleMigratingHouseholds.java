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
import at.sume.dm.types.AgeGroup20;
import at.sume.dm.types.IncomeGroup;
import at.sume.dm.types.MigrationRealm;
import at.sume.sampling.ExactDistribution;
import net.remesch.util.Random;

/**
 * @author Alexander Remesch
 */
public class SampleMigratingHouseholds {
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
	private String migrationPerAgeSexScenarioName;
	private boolean migrationPerAgeSexConstant = false;
	
	public SampleMigratingHouseholds(String migrationScenarioName, String migrationPerAgeSexScenarioName, String migrationHouseholdSizeScenarioName, String migrationIncomeScenarioName) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String selectStatement;

		this.migrationPerAgeSexScenarioName = migrationPerAgeSexScenarioName;

		selectStatement = "SELECT id, householdSize, share " +
			"FROM _DM_MigrationHouseholdSize " +
			"WHERE scenarioName = '" + migrationHouseholdSizeScenarioName + "' " +
			"ORDER BY householdSize";
		migrationHouseholdSize = Common.db.select(MigrationHouseholdSize.class, selectStatement);
		assert migrationHouseholdSize.size() > 0 : "No rows selected from _DM_MigrationHouseholdSize (scenarioName = " + migrationHouseholdSizeScenarioName + ")";
		migrationHouseholdSizeShareTotal = (Integer)Common.db.lookupSql("select sum(share) from _DM_MigrationHouseholdSize where scenarioName = '" + migrationHouseholdSizeScenarioName + "'");
		
		if (!Common.isUseMigrationSaldo())
			totalMigrationsPerYear = new TotalMigrationPerYear(migrationScenarioName);
		
		selectStatement = "SELECT incomeGroupId, probability from _DM_MigrationIncome " + 
			"where scenarioName = '" + migrationIncomeScenarioName + "' order by incomeGroupId";
		migrationIncome = new ExactDistribution<MigrationIncomeRow>(Common.db.select(MigrationIncomeRow.class, selectStatement), "probability");
		// Sampling of workplaces
		immigrationWorkplaceShare = Byte.parseByte(Common.getSysParam("ImmigrationWorkplaceShare"));
	}
	
	private void loadMigrationAgeSexDistribution(int modelYear) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, InstantiationException, SQLException {
		if (Common.isUseMigrationSaldo()) {
			// Use the difference between incoming & outgoing only for immigration numbers
			String selectStatement = "SELECT id, ageGroupId, sex, incoming - outgoing as share " +
					"FROM _DM_MigrationAgeSex " +
					"WHERE scenarioName = '" + migrationPerAgeSexScenarioName + "' AND year = " + modelYear + 
					" AND incoming > outgoing ORDER BY ageGroupId, sex";
			List<MigrationsPerAgeSex> baseData = Common.db.select(MigrationsPerAgeSex.class, selectStatement);
			if (baseData.size() == 0) 
				System.out.println(Common.printInfo() + ": no immigration records (incoming > outgoing) found in _DM_MigrationAgeSex for scenario " + migrationPerAgeSexScenarioName + " and year " + modelYear);
			migrationsPerAgeSex = new ExactDistribution<MigrationsPerAgeSex>(baseData, "share");
		} else {
			if (!migrationPerAgeSexConstant) {
				String selectStatement = "SELECT id, ageGroupId, sex, incoming as share " +
						"FROM _DM_MigrationAgeSex " +
						"WHERE scenarioName = '" + migrationPerAgeSexScenarioName + "' AND year = " + modelYear + 
						" ORDER BY ageGroupId, sex";
				List<MigrationsPerAgeSex> baseData = Common.db.select(MigrationsPerAgeSex.class, selectStatement);
				if (baseData.size() == 0) {
					selectStatement = "SELECT id, ageGroupId, sex, incoming as share " +
							"FROM _DM_MigrationAgeSex " +
							"WHERE scenarioName = '" + migrationPerAgeSexScenarioName + "' AND year is null" + 
							" ORDER BY ageGroupId, sex";
					baseData = Common.db.select(MigrationsPerAgeSex.class, selectStatement);
					migrationPerAgeSexConstant = true;
				}
				assert baseData.size() > 0 : "No rows selected from _DM_MigrationAgeSex (scenarioName = " + migrationPerAgeSexScenarioName + ", year = " + modelYear + ")";
				migrationsPerAgeSex = new ExactDistribution<MigrationsPerAgeSex>(baseData, "share");
			}
		}
	}
	
	public ArrayList<HouseholdRow> sample(int modelYear) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, InstantiationException, SQLException {
		ArrayList<HouseholdRow> result = new ArrayList<HouseholdRow>();
		
		loadMigrationAgeSexDistribution(modelYear);
		
		// 1) Get number of persons immigrating in that year
		long numImmigrants = 0;
		if (Common.isUseMigrationSaldo()) {
			numImmigrants = migrationsPerAgeSex.getMaxThreshold();
			migrationsPerAgeSex.buildExactThresholds();
		} else {
			numImmigrants = totalMigrationsPerYear.getImmigration(modelYear, MigrationRealm.NATIONAL_INCOMING) + totalMigrationsPerYear.getImmigration(modelYear, MigrationRealm.INTERNATIONAL_INCOMING);
			//    and calculate the exact age & sex distribution
			migrationsPerAgeSex.buildExactThresholds(numImmigrants);
		}
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
			int index = migrationsPerAgeSex.randomExactSample();
			MigrationsPerAgeSex m = migrationsPerAgeSex.get(index);
			if (householdSize == 1) { // Sample an adult for single households!
				short age = AgeGroup20.sampleAge(m.getAgeGroup20Id());
				while (age < 18) {
					index = migrationsPerAgeSex.randomExactSample();
					m = migrationsPerAgeSex.get(index);
					age = AgeGroup20.sampleAge(m.getAgeGroup20Id());
				}
				person.setAge(age);
			} else {
				person.setAge(AgeGroup20.sampleAge(m.getAgeGroup20Id()));
			}
			person.setAgeGroupId(m.getAgeGroup20Id());
			migrationsPerAgeSex.modifyDistribution(index);
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
