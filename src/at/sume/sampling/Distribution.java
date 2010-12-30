package at.sume.sampling;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

/**
 * General handling of distributions for random ("Monte Carlo") sampling.
 * 
 * This is a simpler form of class SamplingDistribution.
 * 
 * @author Alexander Remesch
 *
 * @param <E> Data that may be stored with each sample record 
 */
public class Distribution<E> implements Collection<E>, Iterable<E> {
//	private ArrayList<Long> idStore; // TODO: do we really need the idstore???
	protected ArrayList<Long> thresholdStore;
	protected ArrayList<E> objectStore;
	protected long maxThreshold;

	/**
	 * Construct an empty class
	 */
	public Distribution() {
//		idStore = new ArrayList<Long>(0);
		thresholdStore = new ArrayList<Long>();
		objectStore = new ArrayList<E>();
	}
	/**
	 * Constructor that reserves memory for a given number of records
	 * @param recordCount number of records memory will be reserved for
	 */
	public Distribution(int recordCount) {
//		idStore = new ArrayList<Long>(recordCount);
		thresholdStore = new ArrayList<Long>(recordCount);
		objectStore = new ArrayList<E>(recordCount);
	}
	public Distribution(ArrayList<E> objectStore, String sourceFieldName) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
//		idStore = new ArrayList<Long>();
		thresholdStore = new ArrayList<Long>();
		this.objectStore = objectStore;
		buildThresholds(sourceFieldName);
	}
	/**
	 * Get maximum boundary of the sampling range
	 * @return maximum boundary of the sampling range
	 */
	public long getMaxThreshold() {
		return maxThreshold;
	}
	public void buildThresholds(String sourceFieldName) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		maxThreshold = 0;
		Class<?> c = objectStore.get(0).getClass();
		// TODO: implement ability to access private fields
		Field field = c.getField(sourceFieldName);
		String type = field.getType().getName();
		for (int i = 0; i != objectStore.size(); i++) {
			if (type.equals("long") || type.equals("java.lang.Long")) {
				maxThreshold += field.getLong(objectStore.get(i));
			} else if (type.equals("double") || type.equals("java.lang.Double")) {
				maxThreshold += Math.round(field.getDouble(objectStore.get(i)) * 1000);
			} else if (type.equals("int") || type.equals("java.lang.Integer")) {
				maxThreshold += field.getInt(objectStore.get(i));
			} else if (type.equals("short") || type.equals("java.lang.Short")) {
				maxThreshold += field.getShort(objectStore.get(i));
			} else if (type.equals("byte") || type.equals("java.lang.Byte")) {
				maxThreshold += field.getByte(objectStore.get(i));
			} else if (type.equals("float") || type.equals("java.lang.Float")) {
				maxThreshold += field.getFloat(objectStore.get(i));
			} else {
				throw new AssertionError("fieldName = " + c.getName() + "." + sourceFieldName + ", type = " + type);
			}
			thresholdStore.add(maxThreshold);
		}
		// TODO: is this a good workaround?
//		if (maxThreshold == 0) {
//			System.out.println(Common.printInfo() + ": maxThreshold = 0");
//			maxThreshold++;
//		}
	}
	/**
	 * Add a record to the sample
	 * @param delta Range that gives the probability with which this particular record may be chosen during random sampling
	 * @param object Data that may be stored with each sample record
	 */
	public void add(long delta, E object) {
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
	public void add(long id, long delta, E object) {
//		idStore.add(id);
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
		int sampleThreshold = 1;
		if (maxThreshold == 0) {
			// TODO: need a good workaround for this or an assert
			// This occurs when all thresholds = 0 because of rounding. Usually this disappears
			// when the distribution (thresholds) are multiplied by 100 or 1000.
			System.out.println("Problem: maxThreshold = 0");
//			return 0;
		} else {
			sampleThreshold = (int) maxThreshold;
		}
		long rand = (long) r.nextInt(sampleThreshold);
		// lookup index of element where random number falls within the boundaries
		int index = Collections.binarySearch(thresholdStore, rand);
		if (index < 0)
			index = (index + 1) * -1;
		assert index < thresholdStore.size() : "Array index too large; rand = " + rand + ", max threshold = " + maxThreshold + ", index = " + index + ", max index = " + thresholdStore.size();
		return index;
	}
	
	/**
	 * Get data stored with each sample record
	 * @param index index of the sample record
	 * @return
	 */
	public E get(int index) {
		return objectStore.get(index);
	}

	@Override
	public Iterator<E> iterator() {
		return objectStore.iterator();
	}

	@Override
	public boolean add(E arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
//		idStore.clear();
		thresholdStore.clear();
		objectStore.clear();
		maxThreshold = 0;
	}

	@Override
	public boolean contains(Object arg0) {
		return objectStore.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return objectStore.containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return objectStore.isEmpty();
	}

	@Override
	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		return objectStore.size();
	}

	@Override
	public Object[] toArray() {
		return objectStore.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return objectStore.toArray(arg0);
	}
}
