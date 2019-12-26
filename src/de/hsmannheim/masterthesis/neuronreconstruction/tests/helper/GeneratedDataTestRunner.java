/** GeneratedDataTestRunner.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.tests.helper;

import static de.hsmannheim.masterthesis.neuronreconstruction.impl.model.ValueBorders.NOISE_INPUT_MAX_GENERATION;
import static de.hsmannheim.masterthesis.neuronreconstruction.impl.model.ValueBorders.NOISE_INPUT_MIN_GENERATION;
import static de.hsmannheim.masterthesis.neuronreconstruction.impl.model.ValueBorders.NOISE_V_MAX_GENERATION;
import static de.hsmannheim.masterthesis.neuronreconstruction.impl.model.ValueBorders.NOISE_V_MIN_GENERATION;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.model.Model;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.Reconstruction;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes.ReconstructionMode;

/**
 * A test runner for generated data (which is generated with the Izhikevich
 * model).
 * 
 * @author Leah Lackner
 */
public class GeneratedDataTestRunner {

	private GeneratedDataTestRunner() {
	}

	/**
	 * Runs the given test scenario.
	 * 
	 * @param datasetName
	 *            The name of the test
	 * @param seed
	 *            The seed for the random generator
	 * @param timesteps
	 *            The number of time steps
	 * @param inputIdxAfterTuning
	 *            The index after which the real reconstruction will begin.
	 *            (After the u value tuning. See the thesis for more
	 *            information)
	 * @param numNeurons
	 *            The number of the neurons to be generated
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
	 * @param outputPath
	 *            The output directory for all written files
	 * @throws IOException
	 */
	public static void run(String datasetName, int seed, int timesteps, int inputIdxAfterTuning, int numNeurons,
			double dt, int populationSize, double fitnessThreshold, int generationThreshold,
			int abortAfterGenerationsWithoutChange, double mutationRate, boolean enableGui, ReconstructionMode mode,
			String outputPath, Map<Class<?>, Double> measures) throws IOException {
		outputPath = "NeuronReconstruction/" + outputPath;

		double[][] inputs = new double[timesteps][numNeurons];

		Random r = new Random(seed);

		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < inputs[i].length; j++) {
				if (i < inputs.length / 8 * 7 && i > inputs.length / 8 * 4.5
						|| i < inputs.length / 8 * 3 && i > inputs.length / 8 * 2)
					inputs[i][j] = Model.generateInput(r);
				else
					inputs[i][j] = 0;
			}
		}

		double[] uStarts = new double[numNeurons];
		double[][] Vs = new double[timesteps][numNeurons];

		List<Model> generatedModels = new LinkedList<>();
		for (int n = 0; n < numNeurons; n++) {
			generatedModels.add(new Model(r, numNeurons, n));
			Vs[0][n] = generatedModels.get(n).v;
		}

		for (int i = 1; i < inputs.length; i++) {
			for (int n = 0; n < uStarts.length; n++) {
				if (i == inputIdxAfterTuning) {
					uStarts[n] = generatedModels.get(n).u;
				}
				Vs[i][n] = generatedModels.get(n).v;
			}
			Model.calculateNetwork(dt, generatedModels, inputs[i]);
		}
		for (int i = 0; i < inputs[0].length; i++) {
			generatedModels.get(i).u = uStarts[i];
		}

		// apply noise
		double[][] inputsWithNoise = new double[inputs.length][numNeurons];
		for (int i = 0; i < inputs.length; i++) {
			for (int n = 0; n < inputs[0].length; n++) {
				inputsWithNoise[i][n] = inputs[i][n]
						+ Model.generate(r, NOISE_INPUT_MIN_GENERATION, NOISE_INPUT_MAX_GENERATION);
			}
		}
		double[][] VsWithNoise = new double[Vs.length][numNeurons];
		for (int i = 0; i < Vs.length; i++) {
			for (int n = 0; n < inputs[0].length; n++) {
				VsWithNoise[i][n] = Vs[i][n] + Model.generate(r, NOISE_V_MIN_GENERATION, NOISE_V_MAX_GENERATION);
			}
		}

		// do the reconstruction
		List<Model> reconstructed = new Reconstruction().reconstruct(datasetName, new Random(20), inputsWithNoise,
				VsWithNoise, dt, populationSize, fitnessThreshold, generationThreshold,
				abortAfterGenerationsWithoutChange, mutationRate, inputIdxAfterTuning, enableGui, mode, outputPath,
				measures);

		List<Model> reconstructedForSimulation = new LinkedList<>();
		for (int i = 0; i < reconstructed.size(); i++) {
			reconstructedForSimulation.add(new Model(reconstructed.get(i)));
		}

		double[][] Vs2 = new double[timesteps][numNeurons];
		for (int i = inputIdxAfterTuning; i < inputs.length; i++) {
			for (int n = 0; n < numNeurons; n++) {
				Vs2[i][n] = reconstructedForSimulation.get(n).v;
			}
			Model.calculateNetwork(dt, reconstructedForSimulation, inputs[i]);
		}

		for (int n = 0; n < numNeurons; n++) {
			double weightDifference = 0;
			for (int w1 = 0; w1 < numNeurons; w1++) {
				double singleWeightDifference = generatedModels.get(n).weights[w1] - reconstructed.get(n).weights[w1];
				weightDifference += (singleWeightDifference) * (singleWeightDifference);
			}

			System.out.println("Generated model " + n + ":         " + generatedModels.get(n));
			System.out.println("Reconstructed model " + n + ":     " + reconstructed.get(n));
			System.out.println("Squared weight difference: " + weightDifference);

			try (BufferedWriter bfModel = new BufferedWriter(
					new FileWriter(outputPath + "/targetmodel" + "_neuron-" + n + ".csv"));
					BufferedWriter bfWeights = new BufferedWriter(
							new FileWriter(outputPath + "/targetweights" + "_neuron-" + n + ".csv"))) {

				Model model = generatedModels.get(n);
				Model reconstrModel = reconstructed.get(n);
				bfModel.write("a;b;c;d;p1;p2;p3;p4;u;startU;v");
				bfModel.write(System.lineSeparator());
				bfModel.write("" + model.a);
				bfModel.write(";");
				bfModel.write("" + model.b);
				bfModel.write(";");
				bfModel.write("" + model.c);
				bfModel.write(";");
				bfModel.write("" + model.d);
				bfModel.write(";");
				bfModel.write("" + model.p1);
				bfModel.write(";");
				bfModel.write("" + model.p2);
				bfModel.write(";");
				bfModel.write("" + model.p3);
				bfModel.write(";");
				bfModel.write("" + model.p4);
				bfModel.write(";");
				bfModel.write("" + model.u);
				bfModel.write(";");
				bfModel.write("" + model.startU);
				bfModel.write(";");

				// Use the reconstructed v value, because it is the same as the
				// original value after the tuning.
				bfModel.write("" + reconstrModel.v);
				bfModel.write(System.lineSeparator());

				for (int i = 0; i < numNeurons; i++) {
					bfWeights.write("" + i);
					if (i + 1 < numNeurons) {
						bfWeights.write(";");
					}
				}
				double weights[] = model.weights;
				bfWeights.write(System.lineSeparator());
				for (int i = 0; i < weights.length; i++) {
					bfWeights.write("" + weights[i]);
					if (i + 1 < weights.length) {
						bfWeights.write(";");
					}
				}
				bfWeights.write(System.lineSeparator());
			}
		}

	}
}
