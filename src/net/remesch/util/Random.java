/**
 * 
 */
package net.remesch.util;

import ec.util.MersenneTwisterFast;

/**
 * @author Alexander Remesch
 *
 */
public class Random extends MersenneTwisterFast {
	private static final long serialVersionUID = 1300771848132560485L;

	public Random() {
		super();
	}
	public Random(long seed) {
		super(seed);
	}
	/**
	 * Use nextGaussian() to return the next pseudorandom, Gaussian ("normally") distributed double value within the given range.
	 * See: http://stackoverflow.com/questions/629798/problem-with-random-nextgaussian
	 *  
	 * @param range The range the returned value should be in from -range to +range.
	 * @return
	 */
	public double nextGaussianRange(int range) {
		double result = 0;
//		int negRange = range * -1;
		do {
			result = nextGaussian() * 0.3;
		} while ((result > range) || (result < -range));
		return result;
	}
	/**
	 * Returns the next pseudorandom, triangular distributed double value, ranging from min to max with mode as the most frequent value. 
	 * @param min Minimum return value
	 * @param max Maximum return value
	 * @param mode Most frequent return value
	 * @return
	 */
	public double triangular(double min, double max, double mode) {
		if (mode < min || mode > max || min > max)
			throw new IllegalArgumentException("Invalid arguments to triangular(min, max, mode): " + min + ", " + max + ", " + mode);
		double bound = (mode - min) / (max - min);
		double r = nextDouble();
		double result = 0;
		if (r < bound) {
			result = min + Math.sqrt(r * (max - min) * (mode - min));
		} else {
			result = max - Math.sqrt((1 - r) * (max - min) * (max - mode));
		}
		assert min <= result : "Result " + result + " < min " + min;
		assert result <= max : "Result " + result + " > max " + max;
		return result;
	}
}
