/**
 * 
 */
package at.sume.dm.model.output;

/**
 * @author ar
 *
 */
public interface Fileable {
	public String toCsvHeadline(String delimiter);
	public String toString(String delimiter);
}
