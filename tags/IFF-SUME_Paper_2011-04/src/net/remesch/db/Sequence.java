/**
 * 
 */
package net.remesch.db;

/**
 * @author Alexander Remesch
 *
 */
public class Sequence {
	private int value;
	
	public Sequence() {
		value = 1;
	}
	public Sequence(int start) {
		value = start;
	}
	
	public int getNext() {
		return value++;
	}
}
