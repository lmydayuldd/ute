/**
 * 
 */
package at.sume.dm.migration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.indicators.IncomePercentiles;
import at.sume.dm.indicators.managers.PercentileIndicatorManager;
import at.sume.dm.types.AgeGroup;
import at.sume.sampling.ExactDistribution;

/**
 * @author Alexander Remesch
 *
 */
public class SampleImmigratingHouseholds {
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
	private TotalImmigrationPerYear totalImmigrationsPerYear;
	private ExactDistribution<MigrationsPerAgeSex> migrationsPerAgeSex;
	private ArrayList<MigrationHouseholdSize> migrationHouseholdSize;
	private long migrationHouseholdSizeShareTotal = 0;
	
	public SampleImmigratingHouseholds(String scenarioName) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		String selectStatement;
		selectStatement = "SELECT id, ageGroupId, sex, share " +
			"FROM _DM_MigrationAgeSex " +
			"WHERE scenarioName = '" + scenarioName + "' " +
			"ORDER BY ageGroupId, sex";
		migrationsPerAgeSex = new ExactDistribution<MigrationsPerAgeSex>(Common.db.select(MigrationsPerAgeSex.class, selectStatement), "share");
		assert migrationsPerAgeSex.size() > 0 : "No rows selected from _DM_MigrationAgeSex (scenarioName = " + scenarioName + ")";

		selectStatement = "SELECT id, householdSize, share " +
			"FROM _DM_MigrationHouseholdSize " +
			// TODO: change this with scenario-handling class!
//			"WHERE scenarioName = '" + scenarioName + "' " +
			"WHERE scenarioName = 'NEUZUD' " +
			"ORDER BY householdSize";
		migrationHouseholdSize = Common.db.select(MigrationHouseholdSize.class, selectStatement);
		assert migrationHouseholdSize.size() > 0 : "No rows selected from _DM_MigrationHouseholdSize (scenarioName = " + scenarioName + ")";
		migrationHouseholdSizeShareTotal = Math.round((Double)Common.db.lookupSql("select sum(share) from _DM_MigrationHouseholdSize where scenarioName = 'NEUZUD'"));
		totalImmigrationsPerYear = new TotalImmigrationPerYear(scenarioName);
	}
	
	public ArrayList<HouseholdRow> sample(int modelYear) {
		ArrayList<HouseholdRow> result = new ArrayList<HouseholdRow>();
		Random r = new Random();
		
		// 1) Get number of persons immigrating in that year
		long numImmigrants = totalImmigrationsPerYear.get(modelYear);
		//    and calculate the exact age & sex distribution
		migrationsPerAgeSex.buildExactThresholds(numImmigrants);
		
		// 2) Calculate the number of households per household size
		int numHouseholds = 0;
		// immigrants with household size >= migrationHouseholdSize.size()
		int remainingNumImmigrants = Math.round(numImmigrants * migrationHouseholdSize.get(migrationHouseholdSize.size() - 1).getShare() / migrationHouseholdSizeShareTotal);
		for (short householdSize = 1; householdSize != 10; householdSize++) {
			if (householdSize < migrationHouseholdSize.size()) {
				// calculate the share of households from the distribution migrationHouseholdSize
				int actualNumImmigrants = Math.round(numImmigrants * migrationHouseholdSize.get(householdSize).getShare() / migrationHouseholdSizeShareTotal);
				numHouseholds = Math.round(actualNumImmigrants / householdSize);
			} else {
				// distribute remainingNumImmigrants on all remaining household sizes
				if (householdSize == 9) {
					numHouseholds = Math.round(remainingNumImmigrants / householdSize);
				} else {
					int actualNumImmigrants = r.nextInt(remainingNumImmigrants);
					remainingNumImmigrants -= actualNumImmigrants;
					numHouseholds = Math.round(actualNumImmigrants / householdSize);
				}
			}
			for (int i = 0; i != numHouseholds; i++) {
				result.add(sampleHousehold(householdSize, modelYear));
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
//		ArrayList<PersonRow> persons = new ArrayList<PersonRow>(householdSize);
		HouseholdRow result = new HouseholdRow();
		result.setMovingDecisionYear((short) modelYear);
		for (int i = 0; i != householdSize; i++) {
			PersonRow person = new PersonRow();
			int index = migrationsPerAgeSex.randomSample();
			MigrationsPerAgeSex m = migrationsPerAgeSex.get(index);
			person.setAgeGroupId(m.getAgeGroupId());
			person.setAge(AgeGroup.sampleAge(person.getAgeGroupId()));
			person.setSex(m.getSex());
			int yearlyIncome = 0;
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
					} else {
						minIncome = 0;
						maxIncome = ((IncomePercentiles)PercentileIndicatorManager.INCOME_PERCENTILES.getIndicator()).getPersonIncomePercentile((byte) 20);
					}
					yearlyIncome = minIncome + r.nextInt((int) (maxIncome - minIncome));
				}
			}
			person.setYearlyIncome(yearlyIncome);
//			persons.add(person);
			result.addMember(person);
		}
		result.determineInitialHouseholdType();
		// TODO: each household requires at least one adult (is this a good idea? - maybe not for single households)
		return result;
	}
}
