/**
 * 
 */
package at.sume.dm.model.output;

/**
 * @author Alexander Remesch
 *
 */
public interface OutputRow {
	/**
	 * Print out a row in CSV format
	 * @return
	 */
	public String toCsv();
}
