/**
 * 
 */
package at.sume.dm.migration;

import java.sql.SQLException;
import java.util.ArrayList;

import at.sume.dm.Common;
import at.sume.dm.types.MigrationRealm;

/**
 * @author Alexander Remesch
 *
 */
public class TotalMigrationPerYear {
	public static class MigrationsPerYear implements Comparable<MigrationsPerYear> {
		private int id;
		private int modelYear;
		private int immigrationInternational;
		private int immigrationNational;
		private int outMigrationInternational;
		private int outMigrationNational;
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
		 * @return the modelYear
		 */
		public int getModelYear() {
			return modelYear;
		}
		/**
		 * @param modelYear the modelYear to set
		 */
		public void setModelYear(int modelYear) {
			this.modelYear = modelYear;
		}
		/**
		 * @return the immigrationInternational
		 */
		public int getImmigrationInternational() {
			return immigrationInternational;
		}
		/**
		 * @param immigrationInternational the immigrationInternational to set
		 */
		public void setImmigrationInternational(int immigrationInternational) {
			this.immigrationInternational = immigrationInternational;
		}
		/**
		 * @return the immigrationNational
		 */
		public int getImmigrationNational() {
			return immigrationNational;
		}
		/**
		 * @param immigrationNational the immigrationNational to set
		 */
		public void setImmigrationNational(int immigrationNational) {
			this.immigrationNational = immigrationNational;
		}
		/**
		 * @param outMigrationInternational the outMigrationInternational to set
		 */
		public void setOutMigrationInternational(int outMigrationInternational) {
			this.outMigrationInternational = outMigrationInternational;
		}
		/**
		 * @return the outMigrationInternational
		 */
		public int getOutMigrationInternational() {
			return outMigrationInternational;
		}
		/**
		 * @return the outMigrationNational
		 */
		public int getOutMigrationNational() {
			return outMigrationNational;
		}
		/**
		 * @param outMigrationNational the outMigrationNational to set
		 */
		public void setOutMigrationNational(int outMigrationNational) {
			this.outMigrationNational = outMigrationNational;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(MigrationsPerYear o) {
			return ((Integer) modelYear).compareTo(o.modelYear);
		}
	}

	private ArrayList<MigrationsPerYear> migrationsPerYear;
	
	public TotalMigrationPerYear(String scenarioName) throws SQLException, InstantiationException, IllegalAccessException {
		String selectStatement = "SELECT id, modelYear, immigrationInternational, immigrationNational, outMigrationInternational, outMigrationNational " +
			"FROM _DM_Migration " +
			"WHERE scenarioName = '" + scenarioName + "' " +
			"ORDER BY modelYear";
		migrationsPerYear = Common.db.select(MigrationsPerYear.class, selectStatement);
		assert migrationsPerYear.size() > 0 : "No rows selected from _DM_Migration (scenarioName = " + scenarioName + ")";
	}
	/**
	 * Get total immigration (national & international in nr. of persons) for the given year
	 * @param modelYear
	 * @return
	 */
	public int getImmigration(int modelYear, MigrationRealm migrationRealm) {
		return get_v2(modelYear, migrationRealm);
	}
	/**
	 * Get total international out migration (nr. of persons) for the given year
	 * @param modelYear
	 * @return
	 */
	public int getOutMigrationInternational(int modelYear) {
		int pos = lookupYear(modelYear);
		MigrationsPerYear m = migrationsPerYear.get(pos);
		assert m.getModelYear() == modelYear : "Calculated index for year " + modelYear + " returns data for year " + m.getModelYear(); 
		return m.getOutMigrationInternational();
	}
	/**
	 * Get total national out migration (nr. of persons) for the given year
	 * @param modelYear
	 * @return
	 */
	public int getOutMigrationNational(int modelYear) {
		int pos = lookupYear(modelYear);
		MigrationsPerYear m = migrationsPerYear.get(pos);
		assert m.getModelYear() == modelYear : "Calculated index for year " + modelYear + " returns data for year " + m.getModelYear(); 
		return m.getOutMigrationNational();
	}
//	private long get_v1(int modelYear) {
//		for (MigrationsPerYear m : migrationsPerYear) {
//			if (m.getModelYear() == modelYear) {
//				return m.getImmigrationInternational() + m.getImmigrationNational();
//			}
//		}
//		throw new AssertionError("Model year " + modelYear + " not included in migrationsPerYear (_DM_Migration) for this scenario");
//	}
	private int lookupYear(int modelYear) {
		int index = modelYear - migrationsPerYear.get(0).getModelYear();
		assert (index >= 0) && (index < migrationsPerYear.size()) : "Model year " + modelYear + " not included in migrationsPerYear (_DM_Migration) for this scenario";
		return index;
	}
	private int get_v2(int modelYear, MigrationRealm migrationRealm) {
		int index = lookupYear(modelYear);
		MigrationsPerYear m = migrationsPerYear.get(index);
		assert m.getModelYear() == modelYear : "Calculated index for year " + modelYear + " returns data for year " + m.getModelYear();
		switch (migrationRealm) {
		case NATIONAL_INCOMING:
			return m.getImmigrationNational();
		case INTERNATIONAL_INCOMING:
			return m.getImmigrationInternational();
		default:
			throw new IllegalArgumentException("Unexpected migration realm " + migrationRealm.toString());
		}
	}
//	private long get_v3(int modelYear) {
//		MigrationsPerYear m = new MigrationsPerYear();
//		m.setModelYear(modelYear);
//		int index = Collections.binarySearch(migrationsPerYear, m);
//		if (index >= 0) {
//			m = migrationsPerYear.get(index);
//			return m.getImmigrationInternational() + m.getImmigrationNational();
//		} else {
//			throw new AssertionError("Model year " + modelYear + " not included in migrationsPerYear (_DM_Migration) for this scenario");
//		}
//	}
}
