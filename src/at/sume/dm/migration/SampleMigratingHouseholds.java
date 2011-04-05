/**
 * 
 */
package at.sume.dm.migration;

import java.sql.SQLException;
import java.util.ArrayList;

import net.remesch.util.Random;
import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.indicators.IncomePercentiles;
import at.sume.dm.indicators.managers.PercentileIndicatorManager;
import at.sume.dm.policy.MinimumYearlyIncome;
import at.sume.dm.types.AgeGroup;
import at.sume.dm.types.HouseholdType;
import at.sume.dm.types.MigrationRealm;
import at.sume.sampling.ExactDistribution;

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
	private TotalMigrationPerYear totalMigrationsPerYear;
	private ExactDistribution<MigrationsPerAgeSex> migrationsPerAgeSex;
	private ArrayList<MigrationHouseholdSize> migrationHouseholdSize;
	private long migrationHouseholdSizeShareTotal = 0;
	private Persons persons;
	
	public SampleMigratingHouseholds(String migrationScenarioName, String migrationHouseholdSizeScenarioName, Persons persons) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String selectStatement;
		selectStatement = "SELECT id, ageGroupId, sex, share " +
			"FROM _DM_MigrationAgeSex " +
			"WHERE scenarioName = '" + migrationScenarioName + "' " +
			"ORDER BY ageGroupId, sex";
		migrationsPerAgeSex = new ExactDistribution<MigrationsPerAgeSex>(Common.db.select(MigrationsPerAgeSex.class, selectStatement), "share");
		assert migrationsPerAgeSex.size() > 0 : "No rows selected from _DM_MigrationAgeSex (scenarioName = " + migrationScenarioName + ")";

		selectStatement = "SELECT id, householdSize, share " +
			"FROM _DM_MigrationHouseholdSize " +
			"WHERE scenarioName = '" + migrationHouseholdSizeScenarioName + "' " +
			"ORDER BY householdSize";
		migrationHouseholdSize = Common.db.select(MigrationHouseholdSize.class, selectStatement);
		assert migrationHouseholdSize.size() > 0 : "No rows selected from _DM_MigrationHouseholdSize (scenarioName = " + migrationScenarioName + ")";
		migrationHouseholdSizeShareTotal = Math.round((Double)Common.db.lookupSql("select sum(share) from _DM_MigrationHouseholdSize where scenarioName = '" + migrationHouseholdSizeScenarioName + "'"));
		totalMigrationsPerYear = new TotalMigrationPerYear(migrationScenarioName);
		this.persons = persons;
	}
	
	public ArrayList<HouseholdRow> sample(int modelYear, MigrationRealm migrationRealm) {
		ArrayList<HouseholdRow> result = new ArrayList<HouseholdRow>();
		Random r = new Random();
		
		// 1) Get number of persons immigrating in that year
		long numImmigrants = totalMigrationsPerYear.getImmigration(modelYear, migrationRealm);
		//    and calculate the exact age & sex distribution
		migrationsPerAgeSex.buildExactThresholds(numImmigrants);
		
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
				while (household.getHouseholdType() == HouseholdType.OTHER) {
					// TODO: Ignore OTHER households - this must be eliminated by the sampling algorithm in future
					household = sampleHousehold((short) (householdSize + 1), modelYear);
				}
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
	@SuppressWarnings("unused")
	private HouseholdRow sampleHousehold(short householdSize, int modelYear) {
		ArrayList<PersonRow> members = new ArrayList<PersonRow>(householdSize);
		HouseholdRow result = new HouseholdRow();
		result.setMovingDecisionYear((short) modelYear);
		// sample persons
		for (int i = 0; i != householdSize; i++) {
			PersonRow person = new PersonRow();
			// TODO: why are we not using randomExactSample() here when we use a ExactDistribution?
			int index = migrationsPerAgeSex.randomSample();
			MigrationsPerAgeSex m = migrationsPerAgeSex.get(index);
			person.setAgeGroupId(m.getAgeGroupId());
			person.setAge(AgeGroup.sampleAge(person.getAgeGroupId()));
			person.setSex(m.getSex());
			person.setHousehold(result);
			members.add(person);
		}
		result.addMembers(members);
		result.determineInitialHouseholdType(false);	// countAdults() was already done in addMembers()
		// calculate income for each household member
		for (PersonRow person : result.getMembers()) {
			int yearlyIncome = 0;
			if (true) {
				// Alternative income calculation - just get the income from the income distribution of the present population as a quick solution to
				// have the income distribution of the current residents for the immigrants
				if (person.getAge() > 17) {
					// The following calculation has a bias for very high household incomes (after 50 yrs.) about 70% of all households have an income > 200.000 €
					// Solution: calculate income by choosing an existing person and taking their income (each person has the same chance to be picked)
					Random r = new Random();
//					int minIncome = ((IncomePercentiles)PercentileIndicatorManager.INCOME_PERCENTILES.getIndicator()).getPersonIncomePercentile((byte) 0);
//					int maxIncome = ((IncomePercentiles)PercentileIndicatorManager.INCOME_PERCENTILES.getIndicator()).getPersonIncomePercentile((byte) 100);
//					// TODO: mode = median - not fully correct!
//					int modeIncome = ((IncomePercentiles)PercentileIndicatorManager.INCOME_PERCENTILES.getIndicator()).getPersonIncomePercentile((byte) 50);
//					assert maxIncome - minIncome > 0 : "SampleImmigratingHouseholds: Warning: maxIncome = " + maxIncome + ", minIncome = " + minIncome;
//					yearlyIncome = (int) r.triangular(minIncome, maxIncome, modeIncome);
					int numPersons = persons.size();
					int index = r.nextInt(numPersons);
					PersonRow t = persons.get(index);
					while (t.getAge() <= 17) {
						index = r.nextInt(numPersons);
						t = persons.get(index);
					}
					yearlyIncome = t.getYearlyIncome() * (100 + Common.getImmigrationIncomeModifier()) / 100; 
				}
			} else {
				// TODO: put this data into the database (but how?)
				// since Austria only seems to have statistics about origin, age, sex and destination of immigrants,
				// all of the data here comes from Germany:
				// Statistisches Bundesamt, Diehl, Claudia; Grobecker, Claire: Neuzuwanderer in Deutschland. Ergebnisse des Mikrozensus 2000 bis 2003, 2006, URL: http://www.destatis.de/jetspeed/portal/cms/Sites/destatis/Internet/DE/Content/Publikationen/Querschnittsveroeffentlichungen/WirtschaftStatistik/Bevoelkerung/NeuzuwandererDeutschland,property=file.pdf [11.11.2010]
				if ((person.getAge() >= 20) && (person.getAge() <= 70)) { // should be between 20 & 40, but we assume this to be similiar for simplicity in that larger age-group
					// 47,7% are employed
					Random r = new Random();
					if (r.nextInt(100) <= 48) {
						// estimates: 33% from western europe have a high income, 66% from the rest of the world have a low income (we would need more data here)
						// so: the 33% have a income in the range of the top 20% of the residents and the 66% have a income in the range of the lowest 20% of the residents
						int maxIncome, minIncome;
						if (r.nextInt(100) <= 33) {
							minIncome = ((IncomePercentiles)PercentileIndicatorManager.INCOME_PERCENTILES.getIndicator()).getPersonIncomePercentile((byte) 80);
							maxIncome = ((IncomePercentiles)PercentileIndicatorManager.INCOME_PERCENTILES.getIndicator()).getPersonIncomePercentile((byte) 100);
							assert maxIncome - minIncome > 0 : "SampleImmigratingHouseholds: Warning: maxIncome = " + maxIncome + ", minIncome = " + minIncome;
							yearlyIncome = minIncome + r.nextInt((int) (maxIncome - minIncome));
						} else {
							MinimumYearlyIncome minimumYearlyIncome = MinimumYearlyIncome.getInstance();
							minIncome = minimumYearlyIncome.get(modelYear, (byte) result.getAdultsCount(), (byte) (result.getMemberCount() - result.getAdultsCount()));
							maxIncome = Math.max(minIncome, ((IncomePercentiles)PercentileIndicatorManager.INCOME_PERCENTILES.getIndicator()).getPersonIncomePercentile((byte) 20));
							if (maxIncome <= minIncome) {
								yearlyIncome = minIncome;
							} else {
	//							assert maxIncome - minIncome >= 0 : "SampleImmigratingHouseholds: Warning: maxIncome = " + maxIncome + ", minIncome = " + minIncome;
								yearlyIncome = minIncome + r.nextInt((int) (maxIncome - minIncome));
							}
						}
					}
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
