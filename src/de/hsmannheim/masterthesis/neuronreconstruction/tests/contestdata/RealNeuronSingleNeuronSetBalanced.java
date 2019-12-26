/** NormalModeFast.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes.ReconstructionMode;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.SpikeFrequencyQualityMeasure;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.SpikeTimeQualityMeasure;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.VoltageOverallQualityMeasure;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.VoltageSingleQualityMeasure;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.GeneralTestConfiguration;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.helper.ContestDataTestRunner;

/**
 * This is a test for a reconstruction using the neuron data from the
 * contest using the single neuron parameter set. (the details can be found in the README.md file)
 * 
 * @author Leah Lackner
 */
public class RealNeuronSingleNeuronSetBalanced {

	public static final String DATASET_NAME = RealNeuronSingleNeuronSetBalanced.class.getSimpleName();

	public static final int SEED = 22;

	public static final double dt = GeneralTestConfiguration.DT;

	public static final int POPULATION_SIZE = 10000;
	public static final double FITNESS_THRESHOLD = 0.0000000000000000001;
	public static final int GENERATION_THRESHOLD = 15;
	public static final double MUTATION_RATE = 0.5;
	public static final int ABORT_AFTER_GENERATIONS_WITHOUT_CHANGE = 10;

	public static final boolean GUI = true;

	public static final String INPUTFILE_CURRENT = "contestData/input/input.csv";
	public static final String INPUTFILE_VOLTAGE = "contestData/input/voltage.csv";

	public static final String OUTPUT_PATH = "contestdata-output/" + RealNeuronSingleNeuronSetBalanced.class.getSimpleName();

	public static final ReconstructionMode RECONSTRUCTION_MODE = ReconstructionMode.IZHIKEVICH_SINGLE_NEURON;

	public static void main(String[] args) throws IOException {

		Map<Class<?>, Double> spikeMeasures = new HashMap<>();
		spikeMeasures.put(SpikeTimeQualityMeasure.class, 1.0);
		spikeMeasures.put(VoltageSingleQualityMeasure.class, 1.0);
		spikeMeasures.put(VoltageOverallQualityMeasure.class, 1.0);
		spikeMeasures.put(SpikeFrequencyQualityMeasure.class, 1.0);

		ContestDataTestRunner.run(DATASET_NAME, SEED, dt, POPULATION_SIZE, FITNESS_THRESHOLD, GENERATION_THRESHOLD,
				ABORT_AFTER_GENERATIONS_WITHOUT_CHANGE, MUTATION_RATE, GUI, RECONSTRUCTION_MODE, INPUTFILE_CURRENT,
				INPUTFILE_VOLTAGE, OUTPUT_PATH, spikeMeasures);
	}

}
