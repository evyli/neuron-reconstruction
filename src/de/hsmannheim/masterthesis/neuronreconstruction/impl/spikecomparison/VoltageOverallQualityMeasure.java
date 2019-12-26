/** VoltageOverallQualityMeasure.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison;

/**
 * Implements an error function which compares the voltage values of the
 * trajectory.
 * 
 * 
 * @author Leah Lackner
 */
public class VoltageOverallQualityMeasure implements SpikeTrainCmpInterface {

	@Override
	public double compare(SpikeTrainComparator comparator) {

		double sum = 0;
		for (int k = 1; k <= 1; k++) {
			double integralVal = 0;
			for (int i = 0; i < comparator.length; i++) {
				integralVal += g(xk(i, comparator.targetSpikeTrain, comparator.modelSpikeTrain));
			}
			sum += (1 / (double) comparator.length) * integralVal;
		}
		return sum;
	}

	private double g(double xk) {
		return (double) (xk * xk);
	}

	private double xk(int t, double[] s1, double[] s2) {
		return (s1[t] - s2[t]);
	}

	@Override
	public double normalize(double value) {
		return SpikeTrainComparator.doNormalize(value, 0, 1000);
	}

}
