/**
 * 
 */
package net.remesch.util;

/**
 * @author Alexander Remesch
 *
 */
public class Random extends java.util.Random {
	private static final long serialVersionUID = 1300771848132560485L;

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
}
