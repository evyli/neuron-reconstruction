/** ContestDataTestRunner.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.tests.helper;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.datareader.ContestDataReader;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.model.Model;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.Reconstruction;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes.ReconstructionMode;

/**
 * A test runner for the reconstruction from the contest data.
 * 
 * @author Leah Lackner
 */
public class ContestDataTestRunner {

	private ContestDataTestRunner() {
	}

	/**
	 * Runs the given test scenario.
	 * 
	 * @param datasetName
	 *            The name of the test
	 * @param seed
	 *            The seed for the random generator
	 * @param dt
	 *            The delta t to solve the differential equations
	 * @param populationSize
	 *            The size of the genetic generation
	 * @param fitnessThreshold
	 *            The threshold for the fitness at which the reconstruction will
	 *            be aborted
	 * @param generationThreshold
	 *            The threshold for the generation at which the reconstruction
	 *            will be aborted
	 * @param abortAfterGenerationsWithoutChange
	 *            The maximum number of generations after which the
	 *            reconstruction will be aborted by unchanged fitness values.
	 * @param mutationRate
	 *            The mutation rate for the genetic algorithm [0, 1]
	 * @param enableGui
	 *            Enable the graphical user interface.
	 * @param mode
	 *            The reconstruction mode (determining the parameter ranges)
	 * @param inputCurrentFile
	 *            Path to the current file of the contest data
	 * @param inputVoltageFile
	 *            Path to the voltage file of the contest data
	 * @param outputPath
	 *            The output directory for all written files
	 * 
	 * @throws IOException
	 */
	public static void run(String datasetName, int seed, double dt, int populationSize, double fitnessThreshold,
			int generationThreshold, int abortAfterGenerationsWithoutChange, double mutationRate, boolean enableGui,
			ReconstructionMode mode, String inputCurrentFile, String inputVoltageFile, String outputPath,
			Map<Class<?>, Double> measures) throws IOException {
		outputPath = "NeuronReconstruction/" + outputPath;

		ContestDataReader reader = new ContestDataReader(inputCurrentFile, inputVoltageFile);
		double[] inputs = reader.data[0];
		double[] Vs = reader.data[1];
		int lines = reader.lines;

		int inputIdxAfterTuning = lines / 2;

		Model reconstructed = new Reconstruction().reconstruct(datasetName, new Random(seed), inputs, Vs, dt,
				populationSize, fitnessThreshold, generationThreshold, abortAfterGenerationsWithoutChange, mutationRate,
				inputIdxAfterTuning, enableGui, mode, outputPath, measures);

		Model reconstructedForSimulation = new Model(reconstructed);

		double[] Vs2 = new double[Vs.length];
		for (int i = inputIdxAfterTuning; i < inputs.length; i++) {
			double input = inputs[i];
			Vs2[i] = reconstructedForSimulation.v;
			reconstructedForSimulation.calculateModel(dt, input);
		}
		System.out.println("Reconstructed model:     " + reconstructed);

	}
}
