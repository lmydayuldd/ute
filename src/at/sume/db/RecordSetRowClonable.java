/**
 * 
 */
package at.sume.db;

/**
 * @author Alexander Remesch
 *
 */
public abstract class RecordSetRowClonable<T extends RecordSet<?>> extends RecordSetRow<T> implements Cloneable {
	/**
	 * @param rowList
	 */
	public RecordSetRowClonable(T rowList) {
		super(rowList);
	}

	public abstract Object clone();
}
