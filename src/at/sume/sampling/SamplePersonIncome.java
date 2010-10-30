/**
 * 
 */
package at.sume.sampling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import net.remesch.db.Database;
import at.sume.sampling.distributions.IncomeDistributionRow;


/**
 * Sampling of income distribution for Monte Carlo-sampling of synthetic persons, 
 * data from table "_DM_Income per age, sex, spatial unit"
 * 
 * TODO: this data needs to be cached in RAM for speed reasons
 * 
 * @author Alexander Remesch
 */
public class SamplePersonIncome extends SamplingDistribution<IncomeDistributionRow> {
	private Long spatialUnitId;
	private short sex;
	private short ageGroupId;
	
	public SamplePersonIncome(Database db) throws SQLException {
		super(db);
	}
	
	public void loadDistribution(long spatialUnitId, short sex, short ageGroupId) throws SQLException {
		this.spatialUnitId = spatialUnitId;
		this.sex = sex;
		this.ageGroupId = ageGroupId;
		try {
			super.loadDistribution();
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage() + " for spatialUnitId = " + spatialUnitId + ", sex = " + sex + ", ageGroupId = " + ageGroupId);
		}
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createRecordSetRow()
	 */
	@Override
	public IncomeDistributionRow createRecordSetRow() {
		return new IncomeDistributionRow(this);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "SpatialunitId_AD", "Sex", "AgeGroupId", "IncomeGroupId", "MinIncome", "MaxIncome" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	public String[] primaryKeyFieldnames() {
		String s[] = { "SpatialunitId_AD", "Sex", "AgeGroupId", "IncomeGroupId" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_Income per age, sex, spatial unit";
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#selectStatement()
	 */
	@Override
	public String selectStatement() {
		return "SELECT SpatialunitId_AD, Sex, AgeGroupId, IncomeGroupId, MinIncome, MaxIncome, PersonCount FROM [_DM_Income per age, sex, spatial unit] AS distr INNER JOIN _DM_IncomeGroup AS ig ON (distr.IncomeGroupId = ig.ID) " + 
			"WHERE SpatialunitId_AD = ? AND Sex = ? AND AgeGroupId = ? " +
			"ORDER BY IncomeGroupId"; 
	}

	@Override
	public ResultSet getResultSet(PreparedStatement ps) throws SQLException {
		ps.setString(1, spatialUnitId.toString().substring(0, 3));
		ps.setString(2, Short.toString(sex));
		ps.setString(3, Short.toString(ageGroupId));
		ResultSet rs = ps.executeQuery();
		return rs;
	}
	
	public short determineIncomeGroup() {
		if (rowList.size() > 0) {
			return rowList.get(randomSample()).getIncomeGroup();
		} else {
			return 0;
		}
	}
	
	public long determineIncome() {
		if (rowList.size() > 0) {
			IncomeDistributionRow row = rowList.get(randomSample());
			Random r = new Random();
			return (long) (row.getMinIncome() + (r.nextDouble() * (row.getMaxIncome() - row.getMinIncome())));
		} else {
			return 0;
		}
	}
}
