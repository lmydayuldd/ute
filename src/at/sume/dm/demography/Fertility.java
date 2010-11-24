/**
 * 
 */
package at.sume.dm.demography;

import java.sql.SQLException;

import net.remesch.db.Database;

/**
 * Implementation of fertility probability per age and sex
 * @author Alexander Remesch
 */
public class Fertility extends ProbabilityDistribution<FertilityProbabilityRow> {

	/**
	 * @param db
	 * @throws SQLException
	 */
	public Fertility(Database db) throws SQLException {
		super(db);
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.ProbabilityDistribution#createProbabilityItem()
	 */
	@Override
	public FertilityProbabilityRow createProbabilityItem() {
		return new FertilityProbabilityRow();
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.demography.ProbabilityDistribution#selectStatement()
	 */
	@Override
	public String selectStatement() {
		return "SELECT AgeGroupId, Fertziff/1000 AS p " +
			"FROM StatA_FertZiff_W " +
			"WHERE Jahr = 2009";
	}

	/**
	 * Return the probability of birth for a given age-group of females
	 * @param ageGroupId
	 * @return
	 */
	public double probability(short ageGroupId) {
		FertilityProbabilityRow fpi = new FertilityProbabilityRow();
		fpi.setAgeGroupId(ageGroupId);
		return probability(fpi);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "AgeGroupId", "p" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	public String[] primaryKeyFieldnames() {
		String s[] = { "AgeGroupId" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "StatA_FertZiff_W";
	}
}
