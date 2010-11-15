/**
 * 
 */
package at.sume.db;

/**
 * @author Alexander Remesch
 *
 */
public abstract class RecordSetRowClonable<T extends RecordSet<?>> extends RecordSetRow<T> implements Cloneable {
	public abstract Object clone();
}
