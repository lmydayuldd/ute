/**
 * 
 */
package at.sume.sampling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import at.sume.sampling.distributions.CostOfResidenceDistributionRow;
import net.remesch.util.Database;

/**
 * @author Alexander Remesch
 *
 */
public class SampleHouseholdCostOfResidence extends SamplingDistribution<CostOfResidenceDistributionRow> {
	private short householdLivingSpaceGroupId;
	
	/**
	 * @param db
	 */
	public SampleHouseholdCostOfResidence(Database db) {
		super(db);
	}

	public void loadDistribution(short householdLivingSpaceGroupId) throws SQLException {
		this.householdLivingSpaceGroupId = householdLivingSpaceGroupId;
		super.loadDistribution();
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createRecordSetRow()
	 */
	@Override
	public CostOfResidenceDistributionRow createRecordSetRow() {
		return new CostOfResidenceDistributionRow(this);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "LivingSpaceGroupId", "CostOfResidenceGroupId", "MinCosts", "MaxCosts" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	public String[] primaryKeyFieldnames() {
		String s[] = { "LivingSpaceGroupId", "CostOfResidenceGroupId" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_Cost of residence per living space";
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#selectStatement()
	 */
	@Override
	public String selectStatement() {
		// LivingSpaceGroupId wouldn't be necessary here in the SELECT part, but when it is included in primaryKeyFields()
		// we need to supply it here...
		return "SELECT LivingSpaceGroupId, distr.CostOfResidenceGroupId, MinCosts, MaxCosts, DwellingCount FROM [_DM_Cost of residence per living space] AS distr INNER JOIN ISIS_CostOfResidenceGroups AS crg ON (distr.CostOfResidenceGroupId = crg.ID) " + 
			"WHERE LivingSpaceGroupId = ? " +
			"ORDER BY distr.CostOfResidenceGroupId"; 
	}
	
	@Override
	public ResultSet getResultSet(PreparedStatement ps) throws SQLException {
		ps.setString(1, Short.toString(householdLivingSpaceGroupId));
		return ps.executeQuery();
	}
	
	public CostOfResidenceDistributionRow determineCostOfResidenceDistributionRow() {
		return rowList.get(randomSample());
	}
	
	public short determineCostOfResidenceGroupId() {
		return determineCostOfResidenceDistributionRow().getCostOfResidenceGroupId();
	}
	
	/**
	 * Determine cost of residence per m²
	 * @return
	 */
	public int determineCostOfResidence() {
		CostOfResidenceDistributionRow row = determineCostOfResidenceDistributionRow();
		Random r = new Random();
		return (int) (row.getMinCostOfResidence() + (r.nextDouble() * (row.getMaxCostOfResidence() - row.getMinCostOfResidence())));
	}

	/**
	 * Determine cost of residence per m²
	 * @return
	 */
	public int determineCostOfResidence(CostOfResidenceDistributionRow row) {
		Random r = new Random();
		return (int) (row.getMinCostOfResidence() + (r.nextDouble() * (row.getMaxCostOfResidence() - row.getMinCostOfResidence())));
	}

	/* (non-Javadoc)
	 * @see at.sume.sampling.SamplingDistribution#valueField()
	 */
	@Override
	public String valueField() {
		return "DwellingCount";
	}
}
