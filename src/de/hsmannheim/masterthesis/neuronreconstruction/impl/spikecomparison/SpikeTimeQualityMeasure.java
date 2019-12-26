/** SpikeTimeQualityMeasure.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison;

/**
 * Implements an error function which compares the spiking times.
 * 
 * @author Leah Lackner
 */
public class SpikeTimeQualityMeasure implements SpikeTrainCmpInterface {

	@Override
	public double compare(SpikeTrainComparator comparator) {
		int sampling = 1000;
		double deltaWindow = 25e-03;

		return calcSpikeTimeError(comparator.targetSpikeTrainIndices, comparator.modelSpikeTrainIndices, sampling,
				deltaWindow, 0);
	}

	/**
	 * Calculation of the gamma error adapted from
	 * http://lcn.epfl.ch/~gerstner/QuantNeuronMod2007/GamCoincFac.m
	 * 
	 * (See Kistler et al, Neural Comp 9:1015-1045 (1997) Jolivet et al, J
	 * Neurophysiol 92:959-976 (2004) for further details)
	 */
	private double calcSpikeTimeError(int[] data, int[] model, int SamplingFreq, double DeltaWindow, int startIdx) {

		double g = Double.MAX_VALUE;

		if (model.length != 0 && data.length != 0) {

			double DeltaBins = DeltaWindow * SamplingFreq;

			int NSpikesPred = model.length;
			int NSpikesTarget = data.length;

			double FreqPred = SamplingFreq * ((double) NSpikesPred - 1)
					/ (double) Math.max((model[NSpikesPred - 1] - model[0]), 1);
			double NCoincAvg = 2 * DeltaWindow * (double) NSpikesTarget * (double) FreqPred;
			double NNorm = 1 - 2 * FreqPred * DeltaWindow;

			double NCoinc = 0;

			int i = 0;
			while (i < NSpikesTarget) {

				int j = 0;
				while (j < NSpikesPred) {
					if (Math.abs(model[j] - data[i]) <= DeltaBins) {
						NCoinc += 1;
						i += 1;
						if (i >= NSpikesTarget) {
							break;
						}
					}
					j += 1;
				}
				i += 1;
			}
			double t1 = (NCoinc - NCoincAvg);
			double t2 = (double) (.5 * (double) (NSpikesPred + NSpikesTarget));
			double t3 = 1 / (double) NNorm;
			g = t1 / t2 * t3;
			System.out.println("g=" + g);

			if (g <= 0)
				g = 0.0000000000001;
			else if (g >= 1)
				g = 0.9999999999999;

			g = 1 - g;
		} else if (data.length == 0 && model.length == 0) {
			return 0;
		} else {
			return .8;
		}
		return g;
	}

	@Override
	public double normalize(double value) {
		return SpikeTrainComparator.doNormalize(value, 0, 1);
	}

}
