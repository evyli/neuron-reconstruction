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
public class OneNeuronMainVoltageSingle {

	public static final String DATASET_NAME = OneNeuronMainVoltageSingle.class.getSimpleName();
	public static final String OUTPUT_PATH = "generatedData/" + OneNeuronMainVoltageSingle.class.getSimpleName();

	public static void main(String[] args) throws IOException {

		Map<Class<?>, Double> spikeMeasures = new HashMap<>();
		spikeMeasures.put(SpikeTimeQualityMeasure.class, 1.0);
		spikeMeasures.put(VoltageSingleQualityMeasure.class, 5.0);
		spikeMeasures.put(VoltageOverallQualityMeasure.class, 1.0);
		spikeMeasures.put(SpikeFrequencyQualityMeasure.class, 1.0);

		GeneratedDataTestRunner.run(DATASET_NAME, OneNeuronBalanced.SEED, OneNeuronBalanced.TIMESTEPS,
				OneNeuronBalanced.INPUT_IDX_AFTER_TUNING, OneNeuronBalanced.NUM_NEURONS, OneNeuronBalanced.DT,
				OneNeuronBalanced.POPULATION_SIZE, OneNeuronBalanced.FITNESS_THRESHOLD,
				OneNeuronBalanced.GENERATION_THRESHOLD, OneNeuronBalanced.ABORT_AFTER_GENERATIONS_WITHOUT_CHANGE,
				OneNeuronBalanced.MUTATION_RATE, OneNeuronBalanced.GUI, ReconstructionMode.IZHIKEVICH, OUTPUT_PATH,
				spikeMeasures);
	}
}
