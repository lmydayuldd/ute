/**
 * 
 */
package at.sume.dm.demography;

import java.sql.SQLException;

import net.remesch.util.Database;


/**
 * Implementation of mortality probability per age and sex
 * @author Alexander Remesch
 */
public class Mortality extends ProbabilityDistribution<MortalityProbabilityRow> {

	public Mortality(Database db) throws SQLException {
		super(db);
	}

	@Override
	public MortalityProbabilityRow createProbabilityItem() {
		return new MortalityProbabilityRow();
	}

	@Override
	public String[] keyFields() {
		String s[] = { "AgeGroupId", "sex" };
		return s;
	}

	@Override
	public String selectStatement() {
		return "SELECT AgeGroupId, Avg(p_male) AS p, 2 AS sex " +
					"FROM MZ_AgeGroups, StatA_Sterbetafel_W_2009 " +
					"WHERE age>=MinAge And age<=MaxAge " +
					"GROUP BY AgeGroupId, AgeGroup, 2 " +
			   "UNION SELECT AgeGroupId, Avg(p_female) AS p, 1 AS sex " +
				"FROM MZ_AgeGroups, StatA_Sterbetafel_W_2009 " +
				"WHERE age>=MinAge And age<=MaxAge " +
				"GROUP BY AgeGroupId, AgeGroup, 1 ";
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.ProbabilityDistribution#valueField()
	 */
	@Override
	public String valueField() {
		return "p";
	}
	
	/**
	 * Return the probability of death for a given age-group and sex
	 * @param ageGroupId
	 * @param sex
	 * @return
	 */
	public double probability(short ageGroupId, short sex) {
		MortalityProbabilityRow mpi = new MortalityProbabilityRow();
		mpi.setAgeGroupId(ageGroupId);
		mpi.setSex(sex);
		return probability(mpi);
	}
}
