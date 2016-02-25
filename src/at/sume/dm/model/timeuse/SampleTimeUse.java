/**
 * 
 */
package at.sume.dm.model.timeuse;

import java.util.ArrayList;
import java.util.List;

import at.sume.dm.entities.TimeUseRow;

/**
 * General class for sampling of time use. Currently only used for various travel times.
 * 
 * @author Alexander Remesch
 */
public class SampleTimeUse {
	private List<SampleActivity> sampleClasses;
	
	public SampleTimeUse() {
		sampleClasses = new ArrayList<SampleActivity>();
	}
	/**
	 * Add a sample activity to be used for time use sampling 
	 * @param sampleActivity
	 */
	public void registerSampleActivity(SampleActivity sampleActivity) {
		sampleClasses.add(sampleActivity);
	}
	/**
	 * Perform sampling of daily activities for one person. Sampling result will be an ArrayList of activities and
	 * the total daily time use for each. A day may have more than 24 hrs (i.e. "long days") to reflect parallel activities
	 * and their total footprint. 
	 * @return
	 */
	public List<TimeUseRow> randomSample(TimeUseSamplingParameters timeUseSamplingParameters) {
		List<TimeUseRow> result = null;
		result = new ArrayList<TimeUseRow>();
		for (SampleActivity a : sampleClasses) {
			a.setSamplingParameterSource(timeUseSamplingParameters);
			TimeUseRow row = new TimeUseRow(a);
			result.add(row);
		}
		return result;
	}
}
