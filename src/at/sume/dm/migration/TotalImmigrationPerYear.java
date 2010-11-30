/**
 * 
 */
package at.sume.dm.migration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import at.sume.dm.Common;

/**
 * @author Alexander Remesch
 *
 */
public class TotalImmigrationPerYear {
	public static class MigrationsPerYear implements Comparable<MigrationsPerYear> {
		private long id;
		private int modelYear;
		private long immigrationInternational;
		private long immigrationNational;
		/**
		 * @return the id
		 */
		public long getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(long id) {
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
		public long getImmigrationInternational() {
			return immigrationInternational;
		}
		/**
		 * @param immigrationInternational the immigrationInternational to set
		 */
		public void setImmigrationInternational(long immigrationInternational) {
			this.immigrationInternational = immigrationInternational;
		}
		/**
		 * @return the immigrationNational
		 */
		public long getImmigrationNational() {
			return immigrationNational;
		}
		/**
		 * @param immigrationNational the immigrationNational to set
		 */
		public void setImmigrationNational(long immigrationNational) {
			this.immigrationNational = immigrationNational;
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
	
	public TotalImmigrationPerYear(String scenarioName) throws SQLException, InstantiationException, IllegalAccessException {
		String selectStatement = "SELECT id, modelYear, immigrationInternational, immigrationNational " +
			"FROM _DM_Migration " +
			"WHERE scenarioName = '" + scenarioName + "' " +
			"ORDER BY modelYear";
		migrationsPerYear = Common.db.select(MigrationsPerYear.class, selectStatement);
		assert migrationsPerYear.size() > 0 : "No rows selected from _DM_Migration (scenarioName = " + scenarioName + ")";
	}
	public long get(int modelYear) {
		return get_v2(modelYear);
	}
	public long get_v1(int modelYear) {
		for (MigrationsPerYear m : migrationsPerYear) {
			if (m.getModelYear() == modelYear) {
				return m.getImmigrationInternational() + m.getImmigrationNational();
			}
		}
		throw new AssertionError("Model year " + modelYear + " not included in migrationsPerYear (_DM_Migration) for this scenario");
	}
	public long get_v2(int modelYear) {
		int index = modelYear - migrationsPerYear.get(0).getModelYear();
		assert (index >= 0) && (index < migrationsPerYear.size()) : "Model year " + modelYear + " not included in migrationsPerYear (_DM_Migration) for this scenario";
		MigrationsPerYear m = migrationsPerYear.get(index);
		assert m.getModelYear() == modelYear : "Calculated index for year " + modelYear + " returns data for year " + m.getModelYear(); 
		return m.getImmigrationInternational() + m.getImmigrationNational();
	}
	public long get_v3(int modelYear) {
		MigrationsPerYear m = new MigrationsPerYear();
		m.setModelYear(modelYear);
		int index = Collections.binarySearch(migrationsPerYear, m);
		if (index >= 0) {
			m = migrationsPerYear.get(index);
			return m.getImmigrationInternational() + m.getImmigrationNational();
		} else {
			throw new AssertionError("Model year " + modelYear + " not included in migrationsPerYear (_DM_Migration) for this scenario");
		}
	}
}
