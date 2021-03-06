/**
 * 
 */
package at.sume.sampling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class can be used to sample elements according to a distribution using the "Monte Carlo" method.
 * The difference to the Distribution<E> class is that this class is able to give the exact number of 
 * elements according to the given distribution.
 * 
 * The idea is that after sampling one element the probability of sampling another element of the same type
 * will be decreased. The outcome is a number of sampled elements according to a given distribution where only
 * the order of these elements may vary. 
 * 
 * Discussion: sampling an exact distribution is nothing else than creating each element in random order. However,
 * this does make sense in order to have a simple way of making sure, deviations in sampled elements can be kept small.
 * 
 * @author Alexander Remesch
 */
public class ExactDistribution<E> extends Distribution<E> {
	private List<Long> exactThresholdStore;
	private long maxExactThreshold;
	/**
	 * Construct an empty class
	 */
	public ExactDistribution() {
		super();
	}
	/**
	 * @param objectStore
	 * @param sourceFieldName
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public ExactDistribution(List<E> objectStore, String sourceFieldName)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		super(objectStore, sourceFieldName);
	}
	/**
	 * @param objectStore
	 * @param sourceFieldName
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public ExactDistribution(List<E> objectStore, String sourceFieldName, long maxExactThreshold)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		super(objectStore, sourceFieldName);
		buildExactThresholds(maxExactThreshold);
	}
	/**
	 * Constructor that reserves memory for a given number of records
	 * @param recordCount number of records memory will be reserved for
	 */
	public ExactDistribution(int recordCount) {
		super(recordCount);
	}
	/**
	 * Build exact distribution from given sample distribution and maximum element size
	 * @param maxExactThreshold
	 */
	public void buildExactThresholds(long maxExactThreshold) {
		assert maxExactThreshold > 0 : "maxExactThreshold = " + maxExactThreshold;
		exactThresholdStore = new ArrayList<Long>();
		for (int i = 0; i != thresholdStore.size(); i++) {
			exactThresholdStore.add(i, (long) Math.round(thresholdStore.get(i) * maxExactThreshold / maxThreshold));
		}
		this.maxExactThreshold = maxExactThreshold;
		assert exactThresholdStore.size() == objectStore.size() : "exactThresholdStore.size() != objectStore.size()";
	}
	/**
	 * Build exact distribution from given sample distribution and maximum element size
	 * @param maxExactThreshold
	 */
	public void buildExactThresholds() {
		exactThresholdStore = new ArrayList<Long>();
		for (int i = 0; i != thresholdStore.size(); i++) {
			exactThresholdStore.add(i, thresholdStore.get(i));
		}
		this.maxExactThreshold = maxThreshold;
		assert exactThresholdStore.size() == objectStore.size() : "exactThresholdStore.size() != objectStore.size()";
	}
	/**
	 * Decrease the number of elements in the distribution at position index and above by 1
	 * @param index The index of the last sampled element that was usable (by criteria unknown to this function) for the sample
	 */
	public void modifyDistribution(int index) {
		boolean modified = false;
		assert (index >= 0) && (index < exactThresholdStore.size()) : "Index " + index + " >= exact threshold store size " + exactThresholdStore.size();
		long previousValue = 0;
		for (int i = index; i != exactThresholdStore.size(); i++) {
			long currentValue = exactThresholdStore.get(i);
			if ((currentValue > 0) && (currentValue >= previousValue)) {
				exactThresholdStore.set(i, currentValue - 1);
				modified = true;
			}
			previousValue = currentValue;
		}
		if ((maxExactThreshold > 0) && modified)
			maxExactThreshold--;
	}
	/**
	 * Random selection ("sampling") of a record out of the sample
	 * @return index of the record selected
	 * @throws ArrayIndexOutOfBoundsException internal error - returned index larger than number of records in sample
	 */
	public int randomExactSample() throws ArrayIndexOutOfBoundsException
	{
		assert maxExactThreshold > 0 : "maxExactThreshold = " + maxExactThreshold;
		// generate random number for sampling
		long rand = 0;
		if (maxExactThreshold > 0)
			rand = r.nextLong(maxExactThreshold);
		// lookup index of element where random number falls within the boundaries
		int index = Collections.binarySearch(exactThresholdStore, rand);
		if (index < 0)
			index = (index + 1) * -1;
		// get to first element of the same threshold (that is avoid all elements that have already been fully created)
		if ((index > 0) && (index < exactThresholdStore.size() - 1)) {
			index--;
			while (exactThresholdStore.get(index) == exactThresholdStore.get(index + 1)) {
				if (index <= 0)
					break;
				index--;
			}
			if (exactThresholdStore.get(index) != exactThresholdStore.get(index + 1))
				index++;
		}
		assert (index >= 0) && (index < exactThresholdStore.size()) : "Array index too large; rand = " + rand + ", max exact threshold = " + maxExactThreshold + ", index = " + index + ", max index = " + exactThresholdStore.size();
		assert index < objectStore.size() : "Array index too large; rand = " + rand + ", max object store index = " + objectStore.size(); 
		return index;
	}
	/* (non-Javadoc)
	 * @see at.sume.sampling.Distribution#randomSample()
	 */
	@Override
	public int randomSample() throws ArrayIndexOutOfBoundsException {
		return randomExactSample();
	}
	/**
	 * Return the object store
	 * @return
	 */
	public List<E> getObjectStore() {
		return objectStore;
	}
}
