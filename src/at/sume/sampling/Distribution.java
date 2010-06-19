package at.sume.sampling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * General handling of distributions for random ("Monte Carlo") sampling
 * @author Alexander Remesch
 *
 * @param <T> Data that may be stored with each sample record 
 */
public class Distribution<T> {
	private ArrayList<Long> idStore;
	private ArrayList<Long> thresholdStore;
	private ArrayList<T> objectStore;
	private long maxThreshold;

	/**
	 * Get maximum boundary of the sampling range
	 * @return maximum boundary of the sampling range
	 */
	public long getMaxThreshold() {
		return maxThreshold;
	}

	/**
	 * Construct an empty class
	 */
	public Distribution() {
		idStore = new ArrayList<Long>(0);
		thresholdStore = new ArrayList<Long>(0);
		objectStore = new ArrayList<T>(0);
	}
	
	/**
	 * Constructor that reserves memory for a given number of records
	 * @param recordCount number of records memory will be reserved for
	 */
	public Distribution(int recordCount) {
		idStore = new ArrayList<Long>(recordCount);
		thresholdStore = new ArrayList<Long>(recordCount);
		objectStore = new ArrayList<T>(recordCount);
	}

	/**
	 * Add a record to the sample
	 * @param delta Range that gives the probability with which this particular record may be chosen during random sampling
	 * @param object Data that may be stored with each sample record
	 */
	public void add(long delta, T object) {
		maxThreshold += delta;
		thresholdStore.add(maxThreshold);
		objectStore.add(object);
	}

	/**
	 * Add a record to the sample
	 * @param id Identification number
	 * @param delta Range that gives the probability with which this particular record may be chosen during random sampling
	 * @param object Data that may be stored with each sample record
	 */
	public void add(long id, long delta, T object) {
		idStore.add(id);
		add(delta, object);
	}
	
	/**
	 * Random selection ("sampling") of a record out of the sample
	 * @return index of the record selected
	 * @throws ArrayIndexOutOfBoundsException internal error - returned index larger than number of records in sample
	 */
	public int randomSample() throws ArrayIndexOutOfBoundsException
	{
		Random r = new Random();
		// generate random number for sampling
		long rand = (long) (r.nextDouble() * maxThreshold);
		// lookup index of element where random number falls within the boundaries
		int index = Collections.binarySearch(thresholdStore, rand);
		if (index < 0)
			index = (index + 1) * -1;
		if (index > thresholdStore.size())
			throw(new ArrayIndexOutOfBoundsException("rand = " + rand + ", max threshold = " + maxThreshold + ", index = " + index + ", max index = " + thresholdStore.size()));
		return index;
	}
	
	/**
	 * Get data stored with each sample record
	 * @param index index of the sample record
	 * @return
	 */
	public T get(int index) {
		return objectStore.get(index);
	}
}
