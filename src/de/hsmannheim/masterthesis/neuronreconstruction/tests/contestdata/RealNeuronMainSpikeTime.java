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
 * contest. (the details can be found in the README.md file)
 * 
 * @author Leah Lackner
 */
public class RealNeuronMainSpikeTime {

	public static final String DATASET_NAME = RealNeuronMainSpikeTime.class.getSimpleName();
	public static final String OUTPUT_PATH = "contestdata-output/" + RealNeuronMainSpikeTime.class.getSimpleName();
	public static final ReconstructionMode RECONSTRUCTION_MODE = ReconstructionMode.IZHIKEVICH;

	public static void main(String[] args) throws IOException {

		Map<Class<?>, Double> spikeMeasures = new HashMap<>();
		spikeMeasures.put(SpikeTimeQualityMeasure.class, 5.0);
		spikeMeasures.put(VoltageSingleQualityMeasure.class, 1.0);
		spikeMeasures.put(VoltageOverallQualityMeasure.class, 1.0);
		spikeMeasures.put(SpikeFrequencyQualityMeasure.class, 1.0);

		ContestDataTestRunner.run(DATASET_NAME, RealNeuronBalanced.SEED, RealNeuronBalanced.dt,
				RealNeuronBalanced.POPULATION_SIZE, RealNeuronBalanced.FITNESS_THRESHOLD,
				RealNeuronBalanced.GENERATION_THRESHOLD, RealNeuronBalanced.ABORT_AFTER_GENERATIONS_WITHOUT_CHANGE,
				RealNeuronBalanced.MUTATION_RATE, RealNeuronBalanced.GUI, RECONSTRUCTION_MODE,
				RealNeuronBalanced.INPUTFILE_CURRENT, RealNeuronBalanced.INPUTFILE_VOLTAGE, OUTPUT_PATH, spikeMeasures);
	}

}
