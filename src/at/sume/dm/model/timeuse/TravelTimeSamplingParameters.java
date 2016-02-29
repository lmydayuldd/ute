/**
 * 
 */
package at.sume.dm.model.timeuse;

/**
 * Sampling parameters for travel time sampling
 * 
 * @author Alexander Remesch
 */
public class TravelTimeSamplingParameters implements TimeUseSamplingParameters {
	private int origin;
	private int destination;
	private int personId;
	private boolean inEducation;
	private boolean employed;
	private short modelYear;
	
	/**
	 * @param emploayed the employed to set
	 */
	public void setEmployed(boolean employed) {
		this.employed = employed;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(int origin) {
		this.origin = origin;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(int destination) {
		this.destination = destination;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(int personId) {
		this.personId = personId;
	}

	/**
	 * @param inEducation the inEducation to set
	 */
	public void setInEducation(boolean inEducation) {
		this.inEducation = inEducation;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.TimeUseSamplingParameters#getOrigin()
	 */
	@Override
	public int getOrigin() {
		return origin;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.TimeUseSamplingParameters#getDestination()
	 */
	@Override
	public int getDestination() {
		return destination;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.TimeUseSamplingParameters#getPersonId()
	 */
	@Override
	public int getPersonId() {
		return personId;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.TimeUseSamplingParameters#isInEducation()
	 */
	@Override
	public boolean isInEducation() {
		return inEducation;
	}

	/* (non-Javadoc)
	 * @see at.sume.dm.model.timeuse.TimeUseSamplingParameters#isEmployed()
	 */
	@Override
	public boolean isEmployed() {
		return employed;
	}

	/**
	 * @return the modelYear
	 */
	public short getModelYear() {
		return modelYear;
	}

	/**
	 * @param modelYear the modelYear to set
	 */
	public void setModelYear(short modelYear) {
		this.modelYear = modelYear;
	}

}
