/**
 * 
 */
package at.sume.dm.model.timeuse;

import at.sume.dm.types.TravelMode;

/**
 * Base interface for sampling of time use for a certain activity
 * @author Alexander Remesch
 */
public interface SampleActivity {
	/**
	 * Set the source class where the parameters needed for time use sampling may be obtained from
	 * @param timeUseSamplingParameters
	 */
	public void setSamplingParameterSource(TimeUseSamplingParameters timeUseSamplingParameters);
	/**
	 * Sample the time use in minutes per day
	 * @return
	 */
	public int sampleMinutesPerDay();
	/**
	 * Sample the time use in hours per day
	 * @return
	 */
	public double sampleHoursPerDay();
	/**
	 * Get the travel mode (for travel activities only)
	 * @return
	 */
	public TravelMode getTravelMode();
	/**
	 * Get the name of the time use activity
	 * @return
	 */
	public String getActivityName();
}
