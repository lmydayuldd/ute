/**
 * 
 */
package at.sume.dm.model.residential_mobility;

import net.remesch.db.Database;
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
	private Integer medianIncomeLeftForLiving[];
	private int thresholdMinIncomeLeftForLiving;
	private final int householdSizeGroups = Integer.parseInt(Common.getSysParam("HouseholdSizeGroups"));
	
	/**
	 * @param db
	 * @param entityDecisionManager
	 */
	public MinimumIncome(Database db,
			EntityDecisionManager<HouseholdRow, Households> entityDecisionManager, Households households) {
		super(db, entityDecisionManager);
		medianIncomeLeftForLiving = new Integer[householdSizeGroups];
		households.calcMedianIncomeLeftForLiving(medianIncomeLeftForLiving);
		// TODO: this could be individualized for each household - but I don't believe this would be of much benefit here
		thresholdMinIncomeLeftForLiving = Integer.parseInt(Common.getSysParam("THR_MinIncomeLeftForLivingAvg"));
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
	 * Estimate the minimum income left for living needed by a certain household
	 * by calculating a defined percentage (sysparam) of the median income left for living for the
	 * household size 
	 * @param entity
	 * @return
	 */
	public int estimateMinIncomeLeftForLiving(HouseholdRow entity) {
		int householdSize = entity.getMembers().size();
		assert householdSize > 0 : "MinimumIncome.decide(): Invalid household size: " + householdSize;
		if (householdSize > householdSizeGroups)
			householdSize = householdSizeGroups;
		return medianIncomeLeftForLiving[householdSize - 1] * thresholdMinIncomeLeftForLiving / 100;
	}
	
	/**
	 * Check if the household income is sufficient for the current dwelling
	 * @return true, if the household must take consequences (the income is insufficient);
	 *         false, if the income is sufficient
	 */
	@Override
	protected boolean decide(HouseholdRow entity) {
		// TODO: use method in HouseholdRow to calculate minincomeleftforliving
		long minIncomeLeftForLiving = estimateMinIncomeLeftForLiving(entity);
		long actualIncomeLeftForLiving = entity.getYearlyIncome() - entity.getCostOfResidence();
		if ( actualIncomeLeftForLiving < minIncomeLeftForLiving) {
			return true;
		} else {
			return false;
		}
	}
}
