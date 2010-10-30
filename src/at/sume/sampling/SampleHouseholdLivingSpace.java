/**
 * 
 */
package at.sume.sampling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import net.remesch.db.Database;
import at.sume.sampling.distributions.LivingSpaceDistributionRow;

/**
 * @author Alexander Remesch
 *
 */
public class SampleHouseholdLivingSpace extends SamplingDistribution<LivingSpaceDistributionRow> {
	private short householdSize;
	
	/**
	 * @param db
	 */
	public SampleHouseholdLivingSpace(Database db) {
		super(db);
	}

	public void loadDistribution(short householdSize) throws SQLException {
		this.householdSize = householdSize;
		super.loadDistribution();
	}
	
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createRecordSetRow()
	 */
	@Override
	public LivingSpaceDistributionRow createRecordSetRow() {
		return new LivingSpaceDistributionRow(this);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "HouseholdSize", "LivingSpaceGroup", "MinSpace", "MaxSpace" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	public String[] primaryKeyFieldnames() {
		String s[] = { "HouseholdSize", "LivingSpaceGroup" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_Living space per household size";
	}
	
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#selectStatement()
	 */
	@Override
	public String selectStatement() {
		// HouseholdSize wouldn't be necessary here in the SELECT part, but when it is included in primaryKeyFields()
		// we need to supply it here...
		return "SELECT HouseholdSize, distr.LivingSpaceGroup, MinSpace, MaxSpace, HouseholdCount FROM [_DM_Living space per household size] AS distr INNER JOIN MZ_LivingSpaceGroups AS lsg ON (distr.LivingSpaceGroup = lsg.ID) " + 
			"WHERE HouseholdSize = ? " +
			"ORDER BY distr.LivingSpaceGroup"; 
	}

	@Override
	public ResultSet getResultSet(PreparedStatement ps) throws SQLException {
		ps.setString(1, Short.toString(householdSize));
		return ps.executeQuery();
	}
	
	public LivingSpaceDistributionRow determineLivingSpaceDistributionRow() {
		return rowList.get(randomSample());
	}
	
	public short determineLivingSpaceGroup() {
		return determineLivingSpaceDistributionRow().getLivingSpaceGroup();
	}
	
	public int determineLivingSpace() {
		LivingSpaceDistributionRow row = determineLivingSpaceDistributionRow();
		Random r = new Random();
		return (int) (row.getMinLivingSpace() + (r.nextDouble() * (row.getMaxLivingSpace() - row.getMinLivingSpace())));
	}

	public int determineLivingSpace(LivingSpaceDistributionRow row) {
		Random r = new Random();
		return (int) (row.getMinLivingSpace() + (r.nextDouble() * (row.getMaxLivingSpace() - row.getMinLivingSpace())));
	}
	
	/* (non-Javadoc)
	 * @see at.sume.sampling.SamplingDistribution#valueField()
	 */
	@Override
	public String valueField() {
		return "HouseholdCount";
	}
}
