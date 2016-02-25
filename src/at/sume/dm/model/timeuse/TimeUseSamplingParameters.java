/**
 * 
 */
package at.sume.dm.model.timeuse;

/**
 * Callback functions to supply the parameters needed for sampling of time use activities
 * @author Alexander Remesch
 */
public interface TimeUseSamplingParameters {
	/**
	 * Get the origin cell for travel time sampling
	 * @return
	 */
	public int getOrigin();
	/**
	 * Get the destination cell for travel time sampling
	 * @return
	 */
	public int getDestination();
	/**
	 * Get the person id (as database foreign key)
	 * @return
	 */
	public int getPersonId();
	/**
	 * Get the education status
	 * @return
	 */
	public boolean isInEducation();
	/**
	 * Get the employment status
	 * @return
	 */
	public boolean isEmployed();
}
