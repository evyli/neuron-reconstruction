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
import de.hsmannheim.masterthesis.neuronreconstruction.tests.helper.ContestDataTestRunner;

/**
 * This is a test for a reconstruction using the neuron data from the
 * contest using the single neuron parameter set. (the details can be found in the README.md file)
 * 
 * @author Leah Lackner
 */
public class RealNeuronSingleNeuronSetAllSame {

	public static final String DATASET_NAME = RealNeuronSingleNeuronSetAllSame.class.getSimpleName();
	public static final String OUTPUT_PATH = "contestdata-output/"
			+ RealNeuronSingleNeuronSetAllSame.class.getSimpleName();
	public static final ReconstructionMode RECONSTRUCTION_MODE = ReconstructionMode.IZHIKEVICH_SINGLE_NEURON;

	public static void main(String[] args) throws IOException {

		Map<Class<?>, Double> spikeMeasures = new HashMap<>();
		spikeMeasures.put(SpikeTimeQualityMeasure.class, 2.0);
		spikeMeasures.put(VoltageSingleQualityMeasure.class, 2.0);
		spikeMeasures.put(VoltageOverallQualityMeasure.class, 2.0);
		spikeMeasures.put(SpikeFrequencyQualityMeasure.class, 2.0);

		ContestDataTestRunner.run(DATASET_NAME, RealNeuronSingleNeuronSetBalanced.SEED,
				RealNeuronSingleNeuronSetBalanced.dt, RealNeuronSingleNeuronSetBalanced.POPULATION_SIZE,
				RealNeuronSingleNeuronSetBalanced.FITNESS_THRESHOLD,
				RealNeuronSingleNeuronSetBalanced.GENERATION_THRESHOLD,
				RealNeuronSingleNeuronSetBalanced.ABORT_AFTER_GENERATIONS_WITHOUT_CHANGE,
				RealNeuronSingleNeuronSetBalanced.MUTATION_RATE, RealNeuronSingleNeuronSetBalanced.GUI,
				RECONSTRUCTION_MODE, RealNeuronSingleNeuronSetBalanced.INPUTFILE_CURRENT,
				RealNeuronSingleNeuronSetBalanced.INPUTFILE_VOLTAGE, OUTPUT_PATH, spikeMeasures);
	}

}
