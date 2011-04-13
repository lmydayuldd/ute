/**
 * 
 */
package at.sume.dm.demography;

import java.sql.SQLException;

import net.remesch.db.Database;

/**
 * Implementation of mortality probability per age and sex
 * @author Alexander Remesch
 */
public class Mortality extends ProbabilityDistribution<MortalityProbabilityRow> {
	short maxAge;
	
	/**
	 * @param db
	 * @throws SQLException
	 */
	public Mortality(Database db) throws SQLException {
		super(db);
		maxAge = (short) (rowList.size() / 2 - 1);
	}

	@Override
	public MortalityProbabilityRow createProbabilityItem() {
		return new MortalityProbabilityRow();
	}

	@Override
	public String[] primaryKeyFieldnames() {
		String s[] = { "age", "sex" };
		return s;
	}

	@Override
	public String selectStatement() {
		return "SELECT age, p_female AS p, 1 AS sex " +
					"FROM StatA_Sterbetafel_W_2009 " +
			   "UNION SELECT age, p_male AS p, 2 AS sex " +
				"FROM StatA_Sterbetafel_W_2009";
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "age", "sex", "p" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "StatA_Sterbetafel_W_2009";
	}

	/**
	 * Return the probability of death for a given age and sex
	 * @param age
	 * @param sex
	 * @return
	 */
	public double probability(short age, short sex) {
		if (age > maxAge)
			age = maxAge;
		MortalityProbabilityRow mpi = new MortalityProbabilityRow();
		mpi.setAge(age);
		mpi.setSex(sex);
		return probability(mpi);
	}
}
