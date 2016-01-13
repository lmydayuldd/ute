/**
 * 
 */
package net.remesch.probability;

import net.remesch.util.Random;

/**
 * @author Alexander Remesch
 */
public class SingleProbability {
	int threshold;
	int minimum;
	int maximum;
	
	public SingleProbability(int threshold, int maximum) {
		this(threshold, 0, maximum);
	}
	
	public SingleProbability(int threshold, int minimum, int maximum) {
		assert (maximum > minimum) && (minimum >= 0) && (maximum > 0) : "maximum <= minimum or minimum < 0 or maximum <= 0";
		assert (threshold >= minimum) && (threshold <= maximum) : "threshold <= minimum or threshold >= maximum";
		this.threshold = threshold;
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	public boolean occurs() {
		Random r = new Random();
		int p = r.nextInt(maximum - minimum) + minimum;
		return p <= threshold;
	}
}
