/**
 * 
 */
package at.sume.db;

import java.util.ArrayList;

/***
 * 
 * General handling of database recordsets of a certain type
 * 
 * @author Alexander Remesch
 *
 * @param <T> Data containing one database record 
*/
public abstract class RecordSet<T> {
	private ArrayList<Long> idStore;
	private ArrayList<T> objectStore;

	/**
	 * Construct an empty class
	 */
	public RecordSet() {
		idStore = new ArrayList<Long>(0);
		objectStore = new ArrayList<T>(0);
	}
	
	/**
	 * Constructor that reserves memory for a given number of records
	 * @param recordCount number of records memory will be reserved for
	 */
	public RecordSet(int recordCount) {
		idStore = new ArrayList<Long>(recordCount);
		objectStore = new ArrayList<T>(recordCount);
	}

	/**
	 * Add a record to the record store
	 * @param delta Range that gives the probability with which this particular record may be chosen during random sampling
	 * @param object Data that may be stored with each sample record
	 */
	public void add(long delta, T object) {
		objectStore.add(object);
	}

	/**
	 * Add a record to the record store
	 * @param id Identification number
	 * @param delta Range that gives the probability with which this particular record may be chosen during random sampling
	 * @param object Data that may be stored with each sample record
	 */
	public void add(long id, long delta, T object) {
		idStore.add(id);
		add(delta, object);
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
