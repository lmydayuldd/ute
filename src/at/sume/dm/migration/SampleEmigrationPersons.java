/**
 * 
 */
package at.sume.dm.migration;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.sume.dm.Common;
import at.sume.dm.entities.PersonRow;
import at.sume.dm.entities.Persons;
import at.sume.dm.model.residential_mobility.DwellingsOnMarket;
import at.sume.dm.tracing.ObjectSource;
import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public class SampleEmigrationPersons {
	private Map<Byte,Long> emigrationCountMale = new HashMap<Byte,Long>();
	private Map<Byte,Long> emigrationCountFemale = new HashMap<Byte,Long>();
	private String migrationPerAgeSexScenarioName;
	private Persons persons;
	private List<MigrationsPerAgeSex> baseData;
	private long totalEmigrationCount = 0;
	
	public SampleEmigrationPersons(String migrationPerAgeSexScenarioName, Persons persons) {
		this.migrationPerAgeSexScenarioName = migrationPerAgeSexScenarioName;
		this.persons = persons;
	}
	
	private void loadMigrationAgeSexDistribution(int modelYear) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, InstantiationException, SQLException {
		if (Common.isUseMigrationSaldo()) {
			byte hRed = Common.getHouseholdReductionFactor();
			// Use the difference between incoming & outgoing only for immigration numbers
			String selectStatement = "SELECT id, ageGroupId, sex, (outgoing - incoming) / " + hRed + " as share " +
					"FROM _DM_MigrationAgeSex " +
					"WHERE scenarioName = '" + migrationPerAgeSexScenarioName + "' AND year = " + modelYear + 
					" AND incoming < outgoing ORDER BY ageGroupId, sex";
			baseData = Common.db.select(MigrationsPerAgeSex.class, selectStatement);
			if (baseData.size() == 0) 
				System.out.println(Common.printInfo() + ": no immigration records (incoming > outgoing) found in _DM_MigrationAgeSex for scenario " + migrationPerAgeSexScenarioName + " and year " + modelYear);
		} else {
			String selectStatement = "SELECT id, ageGroupId, sex, outgoing as share " +
					"FROM _DM_MigrationAgeSex " +
					"WHERE scenarioName = '" + migrationPerAgeSexScenarioName + "' AND year = " + modelYear + 
					" ORDER BY ageGroupId, sex";
			baseData = Common.db.select(MigrationsPerAgeSex.class, selectStatement);
			assert baseData.size() > 0 : "No rows selected from _DM_MigrationAgeSex (scenarioName = " + migrationPerAgeSexScenarioName + ", year = " + modelYear + ")";
		}
		// Build hash maps
		for (MigrationsPerAgeSex b : baseData) {
			Map<Byte,Long> countMap = b.getSex() == 1 ? emigrationCountFemale : emigrationCountMale;
			countMap.put(b.getAgeGroup20Id(), b.getShare());
			totalEmigrationCount += b.getShare();
		}
	}
	
	public long randomEmigration(int modelYear, DwellingsOnMarket dwellingsOnMarket) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, InstantiationException, SQLException {
		loadMigrationAgeSexDistribution(modelYear);
		
		for (long i = 0; i != totalEmigrationCount; i++) {
			// Choose random person
			PersonRow p = persons.getRandomPerson();
			byte ageGroupId = p.getAgeGroupId();
			byte sex = p.getSex();
			int j = 0;
			while (!emigratorsAvailable(ageGroupId, sex) || (p.getHousehold() == null)) {
				p = persons.getRandomPerson();
				ageGroupId = p.getAgeGroupId();
				sex = p.getSex();
				if (j++ > 1000) { // just emigrate any person if no suitable person found after 1000 iterations
					System.out.println("emigrating an arbitrary person after no suitable person was found");
					break;
				}
			}
			if (p.getHousehold() == null)
				continue;
			// Let person emigrate
			// TODO: check if other household members (if any) can emigrate too and emigrate the household (at a random probability)
			// TODO: migration realm is not correct here - all we do know is OUTGOING but not NATIONAL or INTERNATIONAL
			p.emigrate(dwellingsOnMarket, MigrationRealm.NATIONAL_OUTGOING);
			if (j <= 1000)
				if (!decrementEmigratorsCount(ageGroupId, sex))
					throw new IllegalArgumentException("decrementEmigratorsCount(ageGroupId = " + ageGroupId + ", sex = " + sex + " unexpecedtly failed");
		}
		return totalEmigrationCount;
	}
	/**
	 * Check whether there are still persons of a given age group and sex that will emigrate in the actual model year
	 * @param ageGroupId
	 * @param sex
	 * @return
	 */
	public boolean emigratorsAvailable(byte ageGroupId, byte sex) {
		Map<Byte,Long> resultMap = sex == 1 ? emigrationCountFemale : emigrationCountMale;
		Long actual = resultMap.get(ageGroupId);
		if (actual == null) return false;
		if (actual <= 0) return false;
		return true;
	}
	/**
	 * Decrement the number of emigrators in a given age group and sex
	 * @param ageGroupId
	 * @param sex
	 * @return
	 */
	public boolean decrementEmigratorsCount(byte ageGroupId, byte sex) {
		Map<Byte,Long> resultMap = sex == 1 ? emigrationCountFemale : emigrationCountMale;
		Long actual = resultMap.get(ageGroupId);
		if (actual == null) return false;
		if (actual <= 0) return false;
		resultMap.put(ageGroupId, --actual);
		return true;
	}
}
