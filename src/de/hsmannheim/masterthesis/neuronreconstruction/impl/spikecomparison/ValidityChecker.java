/** ValidityChecker.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.model.ValueBorders;

/**
 * Checks the validity of a reconstructed spike train. The models of invalid
 * spike trains will be discarded.
 * 
 * @author Leah Lackner
 */
public class ValidityChecker implements SpikeTrainCmpInterface {

	@Override
	public double compare(SpikeTrainComparator comparator) {
		for (int i = 0; i < comparator.length; i++) {
			if (!Double.isFinite(comparator.modelSpikeTrain[i])) {
				return Double.POSITIVE_INFINITY;
			}
		}
		if (comparator.modelSpikeTrainIndices.length <= comparator.targetSpikeTrainIndices.length / 3
				|| comparator.modelSpikeTrainIndices.length > comparator.targetSpikeTrainIndices.length * 3) {
			if (comparator.modelSpikeTrainIndices.length != comparator.targetSpikeTrainIndices.length)
				return Double.POSITIVE_INFINITY;
		}
		int modelContinuous = countContinousSpikes(comparator.modelSpikeTrain);
		int targetContinuous = countContinousSpikes(comparator.targetSpikeTrain);

		if (targetContinuous > 0) {
			if (modelContinuous > targetContinuous * 5) {
				return Double.POSITIVE_INFINITY;
			}
		} else if (modelContinuous > comparator.targetSpikeTrainIndices.length / 5) {
			return Double.POSITIVE_INFINITY;
		}
		return 0;
	}

	/**
	 * Count continuous spikes in a spike train.
	 * 
	 * For more details see the individual validation in the thesis in which
	 * they are described in more detail.
	 * 
	 * @param spikeTrain
	 *            The spike train
	 * 
	 * @return The total number of continuous spikes.
	 */
	private int countContinousSpikes(double[] spikeTrain) {
		int continuous = 0;
		for (int i = 0; i < spikeTrain.length; i++) {
			if (spikeTrain[i] >= ValueBorders.SPIKE_RECOGNITION) {
				for (int j = i + 1; j < spikeTrain.length; j++) {
					if (spikeTrain[j] < ValueBorders.SPIKE_RECOGNITION) {
						i = j + 1;
						break;
					}
				}
				for (int j = i + 1; j < spikeTrain.length && j < i + 5; j++) {
					if (spikeTrain[j] > ValueBorders.SPIKE_RECOGNITION) {
						continuous++;
					}
				}
			}
		}
		return continuous;
	}

	@Override
	public double normalize(double value) {
		return value;
	}

}
