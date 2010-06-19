package net.remesch.util;

public class LeastSquareAdjustment {
	/**
	 * Calculate the marginal totals for a two-dimensional array and put them into the last (i.e. rightmost/bottom) column/row of the matrix
	 * @param z
	 */
	public static void calc_marginal_totals(double z[][]) {
		int x, y;
		int ymt = z.length, xmt = z[0].length;
		int yl = ymt - 1, xl = xmt - 1;
		
		for (y = 0; y != yl; y++)
			z[xmt][y] = 0;
		for (x = 0; x != xl; x++)
			z[x][ymt] = 0;
		for (y = 0; y != yl; y++) {
			for (x = 0; x != xl; x++) {
				z[xmt][y] += z[x][y]; // row-totals
				z[x][ymt] += z[x][y]; // column-totals
			}
		}
	}
	
	/**
	 * Perform least square adjustment of array m to resemble the marginal totals given in array n. Both arrays are left unmodified
	 * by this function 
	 * @param n target marginal totals 
	 * @param m cell frequencies to be adjusted to the marginal totals
	 * @param max_cycles
	 * @return
	 */
	public static double [][] adjust_cells(double n[][], double m[][], int max_cycles) {
		int i, x, y;
		int ymt = n.length;
		int xmt = n[0].length;
		int yl = ymt - 1;
		int xl = xmt - 1;
		double z[][] = m.clone();
		
		for (i = 0; i != max_cycles; i++) {
			// 1) re-calculate matrix according to marginal column totals
			for (y = 0; y != yl; y++) {
				z[xmt][y] = 0;
				for (x = 0; x != xl; x++) {
					z[x][y] = z[x][y]*(n[x][ymt]/z[x][ymt]);
					z[xmt][y] += z[x][y]; // row-totals
				}
			}
			// 2) re-calculate matrix according to marginal row totals
			for (x = 0; x != xl; x++) {
				z[x][ymt] = 0;
				for (y = 0; y != yl; y++) {
					z[x][y] = z[x][y]*(n[xmt][y]/z[xmt][y]);
					z[x][ymt] += z[x][y]; // column-totals
				}
			}
			// return condition met? (all marginal totals equal)
			for (y = 0; y != yl; y++) {
				if (m[xmt][y] != n[xmt][y])
					break;
			}
			for (x = 0; x != xl; x++) {
				if (m[x][ymt] != n[x][ymt])
					break;
			}
			if ((x == xl) && (y == yl))
				return z;
		}
		return z;
	}
	
}
