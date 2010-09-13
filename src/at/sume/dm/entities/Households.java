/**
 * 
 */
package at.sume.dm.entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

import net.remesch.util.Database;
import net.remesch.util.MathUtil;
import at.sume.db.RecordSetClonable;
import at.sume.db.RecordSetRow;

/**
 * @author Alexander Remesch
 *
 */
public class Households extends RecordSetClonable<HouseholdRow> {
//	private SpatialUnits spatialunits;
	
	/**
	 * @param db
	 * @throws SQLException
	 */
	public Households(Database db) throws SQLException {
		super(db);
	}

	/**
	 * 
	 */
	public Households() {
		super();
	}

	/**
	 * Link all households in the collection to their corresponding spatial unit according to the spatialunit-id
	 * @param spatialunits Collection of spatial units
	 */
	public void linkSpatialUnits(SpatialUnits spatialunits) {
//		this.spatialunits = spatialunits;
		
		for (RecordSetRow<Households> row : rowList) {
			HouseholdRow hh = (HouseholdRow) row;
			hh.setSpatialunit(spatialunits.lookup(hh.getSpatialunitId()));
		}
	}
	
	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#createDatabaseRecord()
	 */
	@Override
	public HouseholdRow createDatabaseRecord() {
		return new HouseholdRow(this);
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#fieldnames()
	 */
	@Override
	public String[] fieldnames() {
		String s[] = { "HouseholdId", "SpatialunitId", "HouseholdSize", "DwellingId", "LivingSpace", "CostOfResidence" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#primaryKeyFieldnames()
	 */
	@Override
	public String[] primaryKeyFieldnames() {
		String s[] = { "HouseholdId" };
		return s;
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSet#tablename()
	 */
	@Override
	public String tablename() {
		return "_DM_Households";
	}

	/* (non-Javadoc)
	 * @see at.sume.db.RecordSetClonable#factory()
	 */
	@Override
	public RecordSetClonable<HouseholdRow> factory() {
		return new Households();
	}

	/**
	 * Comparator class for the yearly household income
	 * @author Alexander Remesch
	 */
	class CompareYearlyIncome implements Comparator<HouseholdRow> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(HouseholdRow arg0, HouseholdRow arg1) {
			return ((Long)arg0.getYearlyIncome()).compareTo(arg1.getYearlyIncome());
		}
	}
	
	/**
	 * Calculate the median yearly income of all households
	 * @return
	 */
	public long calcMedianYearlyIncome() {
		// comparator version
//		CompareYearlyIncome compareYearlyIncome = new CompareYearlyIncome();
//		Collections.sort(rowList, compareYearlyIncome);
		Long yearlyIncomes[];
		yearlyIncomes = new Long[rowList.size()];
		int i = 0;
		for (HouseholdRow household : rowList) {
			yearlyIncomes[i++] = household.getYearlyIncome();
		}
		return MathUtil.median(yearlyIncomes);
	}

	/**
	 * Calculate the median yearly cost of living for all households
	 * @return
	 */
	public long calcMedianIncomeLeftForLiving() {
		Long yearlyIncomeLeftForLiving[];
		yearlyIncomeLeftForLiving = new Long[rowList.size()];
		int i = 0;
		for (HouseholdRow household : rowList) {
			yearlyIncomeLeftForLiving[i++] = household.getYearlyIncome() - household.getCostOfResidence();
		}
		return MathUtil.median(yearlyIncomeLeftForLiving);
	}

	/**
	 * Calculate the median yearly cost of living for a certain household type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void calcMedianIncomeLeftForLiving(Long[] medianIncomeLeftForLiving) {
		ArrayList<Long> yearlyIncomeLeftForLiving[] = (ArrayList<Long>[])new ArrayList[medianIncomeLeftForLiving.length];
		for (int i = 0; i != medianIncomeLeftForLiving.length; i++) {
			yearlyIncomeLeftForLiving[i] = new ArrayList<Long>();
		}
		for (HouseholdRow household : rowList) {
			int householdSize = household.getMembers().size();
			if (householdSize > 0) {
				if (householdSize > medianIncomeLeftForLiving.length)
					householdSize = medianIncomeLeftForLiving.length;
				yearlyIncomeLeftForLiving[householdSize - 1].add(household.getYearlyIncome() - household.getCostOfResidence());
			}
		}
		for (int i = 0; i != medianIncomeLeftForLiving.length; i++) {
			Long a[] = yearlyIncomeLeftForLiving[i].toArray(new Long[yearlyIncomeLeftForLiving[i].size()]);
			medianIncomeLeftForLiving[i] = MathUtil.median(a);
		}
	}
}
