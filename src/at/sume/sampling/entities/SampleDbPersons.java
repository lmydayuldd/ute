/**
 * 
 */
package at.sume.sampling.entities;

import java.sql.SQLException;

import net.remesch.db.Database;
import net.remesch.db.Sequence;
import at.sume.sampling.PersonDistributionAgeSex;
import at.sume.sampling.SamplePersonIncome;

/**
 * @author Alexander Remesch
 *
 */
public class SampleDbPersons {
	private PersonDistributionAgeSex distributionPersonAgeSex;
	private Sequence personNr = new Sequence();
	private SamplePersonIncome income;
	private int spatialUnitId;
	private Database db;
	
	public SampleDbPersons(Database db) throws SQLException  {
		income = new SamplePersonIncome(db);
		this.db = db;
	}

	public void setSpatialUnit(int spatialUnitId) throws SecurityException, IllegalArgumentException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		distributionPersonAgeSex = new PersonDistributionAgeSex(db, spatialUnitId);
		this.spatialUnitId = spatialUnitId;
	}
	
	public DbPersonRow randomSample(int householdId, boolean householdRepresentative) throws SQLException {
		DbPersonRow result = new DbPersonRow();
		result.setPersonId(personNr.getNext());
		distributionPersonAgeSex.randomSample(householdRepresentative);
		result.setAge(distributionPersonAgeSex.getSampledAge());
		result.setSex(distributionPersonAgeSex.getSampledSex());
		// Yearly income
		income.loadDistribution(spatialUnitId, result.getSex(), distributionPersonAgeSex.getSampledAgeGroupId());
		result.setYearlyIncome(income.determineIncome());
		return result;
	}
}
