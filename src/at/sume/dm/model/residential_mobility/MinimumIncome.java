/**
 * 
 */
package at.sume.dm.model.residential_mobility;

import net.remesch.util.Database;
import at.sume.dm.Common;
import at.sume.dm.entities.HouseholdRow;
import at.sume.dm.entities.Households;
import at.sume.dm.model.core.EntityDecision;
import at.sume.dm.model.core.EntityDecisionManager;

/**
 * @author Alexander Remesch
 *
 */
public class MinimumIncome extends EntityDecision<HouseholdRow, Households> {
	// store the median income left for living for each household-size in the model
	private Long medianIncomeLeftForLiving[];
	private long thresholdMinIncomeLeftForLiving;
	private final int householdSizeGroups = Integer.parseInt(Common.getSysParam("HouseholdSizeGroups"));
	
	/**
	 * @param db
	 * @param entityDecisionManager
	 */
	public MinimumIncome(Database db,
			EntityDecisionManager<HouseholdRow, Households> entityDecisionManager, Households households) {
		super(db, entityDecisionManager);
		medianIncomeLeftForLiving = new Long[householdSizeGroups];
		households.calcMedianIncomeLeftForLiving(medianIncomeLeftForLiving);
		// TODO: this could be individualized for each household - but I don't believe this would be of much benefit here
		thresholdMinIncomeLeftForLiving = Long.parseLong(Common.getSysParam("THR_MinIncomeLeftForLivingAvg"));
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.core.EntityDecision#consequence(at.sume.db.RecordSetRow)
	 */
	@Override
	public void consequence(HouseholdRow entity) {
		// TODO Auto-generated method stub
		// Mark the household to look for a new dwelling (or start searching here????)
	}

	/**
	 * Check if the household income is sufficient for the current dwelling
	 * @return true, if the household must take consequences (the income is insufficient);
	 *         false, if the income is sufficient
	 */
	@Override
	protected boolean decide(HouseholdRow entity) {
		int householdSize = entity.getMembers().size();
		if (householdSize <= 0)
			throw new IllegalArgumentException("MinimumIncome.decide(): Household-size must not be 0");
		if (householdSize > householdSizeGroups)
			householdSize = householdSizeGroups;
		long minIncomeLeftForLiving = medianIncomeLeftForLiving[householdSize] * thresholdMinIncomeLeftForLiving / 100;
		long actualIncomeLeftForLiving = entity.getYearlyIncome() - entity.getCostOfResidence();
		if ( actualIncomeLeftForLiving < minIncomeLeftForLiving) {
			return true;
		} else {
			return false;
		}
	}
}