/** TenNeuronsDetailed.java
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
 * This is a test for a reconstruction with ten interconnected generated
 * neurons.
 * 
 * @author Leah Lackner
 */
public class TenNeuronsBalanced {

	public static final String DATASET_NAME = TenNeuronsBalanced.class.getSimpleName();

	public static final int SEED = 56;

	public static final int NUM_NEURONS = 10;

	public static final int TIMESTEPS = 1000;
	public static final double DT = .1;
	public static final int INPUT_IDX_AFTER_TUNING = TIMESTEPS / 2;

	public static final int POPULATION_SIZE = 10000;
	public static final double FITNESS_THRESHOLD = 0.0000000000000000001;
	public static final int GENERATION_THRESHOLD = 25;
	public static final double MUTATION_RATE = 0.5;
	public static final int ABORT_AFTER_GENERATIONS_WITHOUT_CHANGE = GENERATION_THRESHOLD / 3;

	public static final boolean GUI = true;

	public static final String OUTPUT_PATH = "generatedData/" + TenNeuronsBalanced.class.getSimpleName();

	public static void main(String[] args) throws IOException {

		Map<Class<?>, Double> spikeMeasures = new HashMap<>();
		spikeMeasures.put(SpikeTimeQualityMeasure.class, 1.0);
		spikeMeasures.put(VoltageSingleQualityMeasure.class, 1.0);
		spikeMeasures.put(VoltageOverallQualityMeasure.class, 1.0);
		spikeMeasures.put(SpikeFrequencyQualityMeasure.class, 1.0);

		GeneratedDataTestRunner.run(DATASET_NAME, SEED, TIMESTEPS, INPUT_IDX_AFTER_TUNING, NUM_NEURONS, DT,
				POPULATION_SIZE, FITNESS_THRESHOLD, GENERATION_THRESHOLD, ABORT_AFTER_GENERATIONS_WITHOUT_CHANGE,
				MUTATION_RATE, GUI, ReconstructionMode.IZHIKEVICH, OUTPUT_PATH, spikeMeasures);
	}
}
