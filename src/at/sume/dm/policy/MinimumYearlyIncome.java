/**
 * 
 */
package at.sume.dm.policy;


/**
 * Calculate the minimum yearly income for a household type in a certain model year
 * 
 * @author Alexander Remesch
 */
public class MinimumYearlyIncome {
    private static final MinimumYearlyIncome INSTANCE = new MinimumYearlyIncome();

    private MinimumYearlyIncome() {}

    public static MinimumYearlyIncome getInstance() {
        return INSTANCE;
    }
    /**
     * Get minimum yearly income for a household in the given model year with the given number of adults
     * and children
     * 
     * @param modelYear
     * @param numAdults
     * @param numChildren
     * @return
     */
    public int get(int modelYear, byte numAdults, byte numChildren) {
    	// TODO: get these values from the database (this is a simple experimental version!)
    	int result;
    	if (numAdults == 1) {
    		result = 8796;
    	} else {
    		result = numAdults * 6594;
    	}
    	result += numChildren * 1584;
    	return(result);
    }
}
