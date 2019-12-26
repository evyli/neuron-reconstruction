/** OneNeuronDetailed.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes.ReconstructionMode;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.SpikeFrequencyQualityMeasure;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.SpikeTimeQualityMeasure;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.VoltageOverallQualityMeasure;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.VoltageSingleQualityMeasure;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.helper.GeneratedDataTestRunner;

/**
 * This is a test for a reconstruction with one generated neuron.
 * 
 * @author Leah Lackner
 */
public class OneNeuronLongRunning {

	public static final String DATASET_NAME = OneNeuronLongRunning.class.getSimpleName();
	public static final String OUTPUT_PATH = "generatedData/" + OneNeuronLongRunning.class.getSimpleName();

	public static final int SEED = 11;
	public static final int TIMESTEPS = 3000;
	public static final int POPULATION_SIZE = 50;
	public static final double FITNESS_THRESHOLD = 0.0000000000000000001;
	public static final int GENERATION_THRESHOLD = 50;
	public static final double MUTATION_RATE = 0.3;
	public static final int ABORT_AFTER_GENERATIONS_WITHOUT_CHANGE = 150;
	public static final int INPUT_IDX_AFTER_TUNING = TIMESTEPS / 2;

	public static void main(String[] args) throws IOException {

		Map<Class<?>, Double> spikeMeasures = new HashMap<>();
		spikeMeasures.put(SpikeTimeQualityMeasure.class, 2.0);
		spikeMeasures.put(VoltageSingleQualityMeasure.class, 2.0);
		spikeMeasures.put(VoltageOverallQualityMeasure.class, 2.0);
		spikeMeasures.put(SpikeFrequencyQualityMeasure.class, 2.0);

		GeneratedDataTestRunner.run(DATASET_NAME, SEED, TIMESTEPS, INPUT_IDX_AFTER_TUNING,
				OneNeuronBalanced.NUM_NEURONS, OneNeuronBalanced.DT, POPULATION_SIZE, FITNESS_THRESHOLD,
				GENERATION_THRESHOLD, ABORT_AFTER_GENERATIONS_WITHOUT_CHANGE, MUTATION_RATE, OneNeuronBalanced.GUI,
				ReconstructionMode.IZHIKEVICH, OUTPUT_PATH, spikeMeasures);
	}
}
