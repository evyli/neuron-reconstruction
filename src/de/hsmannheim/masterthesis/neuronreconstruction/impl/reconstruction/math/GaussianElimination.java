/** GaussianElimination.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 * Adapted from http://introcs.cs.princeton.edu/java/95linear/GaussianElimination.java.html
 * Copyright (C) 2000–2011, Robert Sedgewick and Kevin Wayne
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.math;

/**
 * Implementation of the Gaussian Elimination algorithm.
 * 
 * The special condition is implemented to prevent a divisor of exactly zero
 * which cannot be handled by the weight estimation.
 * 
 * @author Leah Lackner
 */
public class GaussianElimination {

	private GaussianElimination() {
	}

	/**
	 * Solve the equations
	 * 
	 * @param A
	 *            The matrix containing the equations
	 * @param b
	 *            The solution vector
	 * 
	 * @return The resulting values
	 */
	public static double[] lsolve(double[][] A, double[] b) {
		int N = b.length;
		double[] x = new double[N];

		if (N != A.length || N != A[0].length) {
			throw new IllegalArgumentException("Matrix length mismatch");
		}

		for (int p = 0; p < N; p++) {
			int max = p;
			for (int i = p + 1; i < N; i++) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}
			double[] temp = A[p];
			A[p] = A[max];
			A[max] = temp;
			double t = b[p];
			b[p] = b[max];
			b[max] = t;

			for (int i = p + 1; i < N; i++) {
				double divisor = A[p][p];
				if (divisor == 0) {
					divisor = 0.00000000000000000001;
				}
				double alpha = A[i][p] / divisor;
				b[i] -= alpha * b[p];
				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}
		for (int i = N - 1; i >= 0; i--) {
			double sum = 0.0;
			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}
			double divisor = A[i][i];
			if (divisor == 0) {
				divisor = 0.00000000000000000001;
			}
			x[i] = (b[i] - sum) / divisor;
		}
		return x;
	}

}
