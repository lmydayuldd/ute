/**
 * 
 */
package at.sume.dm.indicators.rows;

/**
 * @author Alexander Remesch
 *
 */
public class IndicatorRow {
	protected static String delimiter = ";";
	
	public static void setDelimiter(String delimiter) {
		IndicatorRow.delimiter = delimiter;
	}
	
	public String display() {
		return this.toString();
	}
}
