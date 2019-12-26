/** SpikeTimeQualityMeasure.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison;

/**
 * Implements an error function which compares the spiking frequencies.
 * 
 * @author Leah Lackner
 */
public class SpikeFrequencyQualityMeasure implements SpikeTrainCmpInterface {

	@Override
	public double compare(SpikeTrainComparator comparator) {
		int sampling = 2000;
		double deltaWindow = 25e-03;

		return calcSpikeTimeError(comparator.targetSpikeTrainIndices, comparator.modelSpikeTrainIndices, sampling,
				deltaWindow, 0);
	}

	/**
	 * Calculation of the gamma error modified for rough frequency determination
	 * adapted from
	 * http://lcn.epfl.ch/~gerstner/QuantNeuronMod2007/GamCoincFac.m
	 * 
	 * (See Kistler et al, Neural Comp 9:1015-1045 (1997) Jolivet et al, J
	 * Neurophysiol 92:959-976 (2004) for further details)
	 */
	private double calcSpikeTimeError(int[] data, int[] model, int SamplingFreq, double DeltaWindow, int startIdx) {

		double g = Double.MAX_VALUE;

		if (model.length != 0 && data.length != 0) {

			int NSpikesPred = model.length;
			int NSpikesTarget = data.length;

			double FreqPred = SamplingFreq * ((double) NSpikesPred - 1)
					/ (double) Math.max((model[NSpikesPred - 1] - model[0]), 1);
			double FreqTarget = SamplingFreq * ((double) NSpikesTarget - 1)
					/ (double) Math.max((data[NSpikesTarget - 1] - data[0]), 1);

			g = 0;
			if (FreqPred != FreqTarget)
				g = Math.abs(FreqPred - FreqTarget) * Math.abs(FreqPred - FreqTarget);
		} else {
			g = 1000;
		}
		return g;
	}

	@Override
	public double normalize(double value) {
		return SpikeTrainComparator.doNormalize(value, 1, 5000);
	}

}
