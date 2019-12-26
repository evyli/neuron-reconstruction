/** Reconstruction.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.model.Model;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.gui.GUI;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes.ReconstructionMode;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes.ReconstructionModeAbstract;

/**
 * Main implementation of the reconstruction. Contains also the implementation
 * of the Genetic Algorithm.
 * 
 * @author Leah Lackner
 */
public class Reconstruction {

	private GUI gui;

	/**
	 * Reconstructs a model for one neuron of input.
	 * 
	 * @param datasetName
	 *            The shown name of the data set which is processed (Does not
	 *            affect the reconstruction)
	 * @param r
	 *            The random instance
	 * @param inputs
	 *            The input data (current)
	 * @param vs
	 *            The voltage data
	 * @param dt
	 *            The dt value for the solving of the differential equations
	 * @param populationSize
	 *            the size of the population of the Genetic Algorithm
	 * @param fitnessThreshold
	 *            The reconstruction is aborted when this fitness value is
	 *            reached (less is better)
	 * @param generationThreshold
	 *            The maximum generation which is computed
	 * @param abortWhenNoChangeAfterGenerationThreshold
	 *            When the best individual has not changed for this amount of
	 *            generations the reconstruction is aborted.
	 * @param mutationRate
	 *            The mutation rate used in the Genetic Algorithm
	 * @param inputIdxAfterTuning
	 *            The time step until the u value is tuned (before the
	 *            reconstruction is started)
	 * @param guiEnabled
	 *            Runs the reconstruction with enabled graphical user interface
	 * @param modeWrapper
	 *            The reconstruction mode (parameter sets as described in the
	 *            thesis)
	 * @param outputPath
	 *            The output path (directory) of the generated files
	 * @param measures
	 *            Map containing the measures (as Class<?> objects) to be used
	 *            in the fitness evaluation along with their assigned weight
	 *            values. A weight of 1 for all measures results in the use of
	 *            the balanced weight measure, 2 for all measures results in the
	 *            use of the all-same weight measure, otherwise the weights are
	 *            manually set.
	 * 
	 * @return The reconstructed model
	 * 
	 * @throws IOException
	 */
	public Model reconstruct(String datasetName, Random r, double[] inputs, double[] vs, double dt, int populationSize,
			double fitnessThreshold, int generationThreshold, int abortWhenNoChangeAfterGenerationThreshold,
			double mutationRate, int inputIdxAfterTuning, boolean guiEnabled, ReconstructionMode modeWrapper,
			String outputPath, Map<Class<?>, Double> measures) throws IOException {

		double[][] newVs = new double[vs.length][1];
		double[][] newInputs = new double[inputs.length][1];

		for (int i = 0; i < vs.length; i++) {
			newVs[i][0] = vs[i];
		}
		for (int i = 0; i < inputs.length; i++) {
			newInputs[i][0] = inputs[i];
		}
		return reconstruct(datasetName, r, newInputs, newVs, dt, populationSize, fitnessThreshold, generationThreshold,
				abortWhenNoChangeAfterGenerationThreshold, mutationRate, inputIdxAfterTuning, guiEnabled, modeWrapper,
				outputPath, measures).get(0);
	}

	/**
	 * Reconstructs a model for multiple neurons of input.
	 * 
	 * @param datasetName
	 *            The shown name of the data set which is processed (Does not
	 *            affect the reconstruction)
	 * @param r
	 *            The random instance
	 * @param inputs
	 *            The input data (current)
	 * @param vs
	 *            The voltage data
	 * @param dt
	 *            The dt value for the solving of the differential equations
	 * @param populationSize
	 *            the size of the population of the Genetic Algorithm
	 * @param fitnessThreshold
	 *            The reconstruction is aborted when this fitness value is
	 *            reached (less is better)
	 * @param generationThreshold
	 *            The maximum generation which is computed
	 * @param abortWhenNoChangeAfterGenerationThreshold
	 *            When the best individual has not changed for this amount of
	 *            generations the reconstruction is aborted.
	 * @param mutationRate
	 *            The mutation rate used in the Genetic Algorithm
	 * @param inputIdxAfterTuning
	 *            The time step until the u value is tuned (before the
	 *            reconstruction is started)
	 * @param guiEnabled
	 *            Runs the reconstruction with enabled graphical user interface
	 * @param modeWrapper
	 *            The reconstruction mode (parameter sets as described in the
	 *            thesis)
	 * @param outputPath
	 *            The output path (directory) of the generated files
	 * @param measures
	 *            Map containing the measures (as Class<?> objects) to be used
	 *            in the fitness evaluation along with their assigned weight
	 *            values. A weight of 1 for all measures results in the use of
	 *            the balanced weight measure, 2 for all measures results in the
	 *            use of the all-same weight measure, otherwise the weights are
	 *            manually set.
	 * 
	 * @return The reconstructed model
	 * 
	 * @throws IOException
	 */
	public List<Model> reconstruct(String datasetName, Random r, double[][] inputs, double[][] vs, double dt,
			int populationSize, double fitnessThreshold, int generationThreshold,
			int abortWhenNoChangeAfterGenerationThreshold, double mutationRate, int inputIdxAfterTuning,
			boolean guiEnabled, ReconstructionMode modeWrapper, String outputPath, Map<Class<?>, Double> measures)
			throws IOException {
		cleanOldLogFiles(outputPath);
		new File(outputPath).mkdirs();

		ReconstructionModeAbstract mode = modeWrapper.getMode();
		int numNeurons = inputs[0].length;

		log(outputPath, "description,neuron,fitness,diversity,time,time_generation");

		WorkQueue parallelQueue = new WorkQueue();
		long startTime = System.currentTimeMillis();

		// Initialise the GUI only if it is enabled
		if (guiEnabled) {
			gui = new GUI(datasetName, inputs, vs, dt, inputIdxAfterTuning, generationThreshold, fitnessThreshold,
					populationSize, abortWhenNoChangeAfterGenerationThreshold, mutationRate);
		}

		List<Model> bestInds = new LinkedList<>();

		// For each neuron of the network
		for (int n = 0; n < numNeurons; n++) {
			initLogFiles(outputPath, n, numNeurons);

			// 1: Generate an initial population with N individuals
			log(outputPath, "Generating initial population,,,");
			List<Individual> population = new ArrayList<>(populationSize);
			for (int i = 0; i < populationSize; i++) {
				population.add(new Individual(new Model(mode, r, numNeurons, n)));
			}

			// 2: Calculate the fitness values for the initial generation.
			log(outputPath, "Starting reconstruction,,,");
			recalculateFitness(mode, n, r, parallelQueue, population, inputs, vs, dt, inputIdxAfterTuning, 0, measures);
			String timeStrGeneration1 = toTime(startTime, System.currentTimeMillis());
			log(outputPath, "Generation 0," + n + "," + population.get(0).getFitness() + "," + getDiversity(population)
					+ "," + timeStrGeneration1 + "," + timeStrGeneration1);
			logModel(outputPath, population.get(0), n);

			double lastFitness = population.get(0).getFitness();
			int lastFitnessSameCount = 0;

			// 3: Run the Genetic Algorithm
			int generationCount = 0;
			while (population.get(0).getFitness() > fitnessThreshold && generationCount < generationThreshold) {
				generationCount++;
				long startTimeGeneration = System.currentTimeMillis();

				List<Individual> nextGeneration = new ArrayList<>(populationSize);
				// add 1% of the best individuals of the last generation to the
				// new generation
				double percentForSurvive = nextGeneration.size() / (double) 100;
				percentForSurvive = Math.max(1, percentForSurvive);
				for (int i = 0; i < percentForSurvive; i++) {
					// add the model as it is
					nextGeneration.add(new Individual(new Model(population.get(i).getModel())));

					// add a mutated version of the best 1% of the individuals,
					// so that 2% of the population of the next generation are
					// already generated
					Model bestIndMutated = new Model(population.get(i).getModel());
					mutate(mode, r, bestIndMutated);
					nextGeneration.add(new Individual(bestIndMutated));
				}

				// Generate new generation by crossover and mutation
				while (nextGeneration.size() < populationSize) {
					Individual ind1 = select(r, population);
					Individual ind2 = select(r, population);

					Model newModel = crossover(r, ind1.getModel(), ind2.getModel());
					if (r.nextDouble() <= mutationRate) {
						mutate(mode, r, newModel);
					}
					nextGeneration.add(new Individual(newModel));
				}

				// Determine the fitness values of the whole population
				recalculateFitness(mode, n, r, parallelQueue, nextGeneration, inputs, vs, dt, inputIdxAfterTuning,
						generationCount, measures);
				population = nextGeneration;
				log(outputPath,
						"Generation " + generationCount + "," + n + "," + population.get(0).getFitness() + ","
								+ getDiversity(population) + "," + toTime(startTime, System.currentTimeMillis()) + ","
								+ toTime(startTimeGeneration, System.currentTimeMillis()));
				logModel(outputPath, population.get(0), n);

				// Abort if the fitness value has not changed for a specified
				// number of generations
				if (population.get(0).getFitness() == lastFitness) {
					lastFitnessSameCount++;
					if (lastFitnessSameCount > abortWhenNoChangeAfterGenerationThreshold) {
						break;
					}
				} else {
					lastFitness = population.get(0).getFitness();
					lastFitnessSameCount = 0;
				}
			}
			// Extract the best individual
			Individual bestInd = population.get(0);
			log(outputPath, "Finnished after " + generationCount + " generation(s),," + bestInd.getFitness() + ","
					+ getDiversity(population) + "," + toTime(startTime, System.currentTimeMillis()) + ",");

			bestInds.add(bestInd.getModel());

			if (guiEnabled)
				gui.actionUpdateOverall(n);
			System.err.flush();
		}
		parallelQueue.shutdown();

		// Simulate the whole network to attain the data for the written results
		double[][] vsReconstructed = calculateAFullNetworkSimulation(inputs, vs, bestInds, inputIdxAfterTuning, dt);

		// Produce the spike timing graphic if the GUI is enabled
		if (guiEnabled) {
			gui.actionReconstructionIsDone(vs, vsReconstructed);
		}
		writeResults(inputs, vs, vsReconstructed, bestInds, outputPath);

		return bestInds;
	}

	/**
	 * Recalculate the fitness values of a population of the Genetic Algorithm.
	 * 
	 * @param mode
	 *            The used parameter set
	 * @param neuronIdx
	 *            The index of the neuron
	 * @param r
	 *            The random instance
	 * @param parallelQueue
	 *            Internal object used for the parallelisation of the algorithm
	 * @param population
	 *            The population of the Genetic Algorithm
	 * @param inputs
	 *            The input data (currents)
	 * @param vs
	 *            The voltage data
	 * @param dt
	 *            The dt value for the solving of the differential equations
	 * @param inputIdxAfterTuning
	 *            The time step until the u value is tuned (before the
	 *            reconstruction is started)
	 * @param generation
	 *            The current generation
	 * @param measures
	 *            Map containing the measures (as Class<?> objects) to be used
	 *            in the fitness evaluation along with their assigned weight
	 *            values. A weight of 1 for all measures results in the use of
	 *            the balanced weight measure, 2 for all measures results in the
	 *            use of the all-same weight measure, otherwise the weights are
	 *            manually set.
	 */
	private void recalculateFitness(ReconstructionModeAbstract mode, int neuronIdx, Random r, WorkQueue parallelQueue,
			List<Individual> population, double[][] inputs, double[][] vs, double dt, int inputIdxAfterTuning,
			int generation, Map<Class<?>, Double> measures) {
		long beginFunction = System.currentTimeMillis();
		if (gui != null) {
			gui.actionUpdatePerGenerationCount(0);
			gui.actionUpdatePerNeuron(neuronIdx, new Individual(population.get(0)), generation);
		}
		// Reconstruct the individuals in parallel.
		// A seed is deterministically generated by using the random instance to
		// get reproducible results.
		// The atomic integer is used to get the number of already computed
		// individuals without race conditions.
		long seed = r.nextLong();
		AtomicLong atLong = new AtomicLong(0);
		for (int i = 0; i < population.size(); i++) {
			Individual ind = population.get(i);

			final long rseed = seed + i;

			parallelQueue.execute(() -> {
				ind.recalculate(mode, new Random(rseed), inputs, vs, dt, inputIdxAfterTuning, measures);
				atLong.incrementAndGet();
			});
		}
		// Wait until all individuals are reconstructed.
		long lastVal = 0;
		while (true) {
			long atLongVal = atLong.get();
			if (gui != null) {
				gui.actionUpdatePerGenerationCount((int) atLongVal);
			}
			if (atLongVal == population.size())
				break;
			if (population.size() >= 10000 && atLongVal % (population.size() / 10) == 0 && atLongVal != 0
					&& atLongVal != lastVal) {
				System.err.println("Individual " + atLongVal + "/" + population.size());
				System.err.flush();
			}
			if (atLongVal != lastVal) {
				lastVal = atLongVal;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		parallelQueue.waitForEndOfWork();
		// Sort the population so that they are sorted suitable for the
		// selection method described in the thesis.
		Collections.sort(population);
		if (gui != null) {
			long sleeptime = 600 - (System.currentTimeMillis() - beginFunction);
			if (sleeptime > 0)
				try {
					Thread.sleep(sleeptime);
				} catch (InterruptedException e) {
				}
			gui.actionUpdatePerGenerationCount(population.size());
			gui.actionUpdatePerNeuronAfter(neuronIdx, new Individual(population.get(0)), generation);
		}
	}

	/**
	 * Remove old log files.
	 */
	private void cleanOldLogFiles(String outputPath) {
		if (outputPath == null)
			return;
		File outputPathFile = new File(outputPath);
		String[] files = outputPathFile.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("fitness_") && name.endsWith(".csv") || name.equals("reconstruction.log")
						|| name.startsWith("model_") && name.endsWith(".csv")
						|| name.startsWith("weights_") && name.endsWith(".csv")
						|| name.startsWith("targetmodel_") && name.endsWith(".csv")
						|| name.startsWith("targetweights_") && name.endsWith(".csv");
			}
		});

		if (files != null)
			for (String fileName : files) {
				File file = new File(outputPathFile, fileName);
				if (!file.delete()) {
					throw new RuntimeException("Error while clearing old log files");
				}
			}
	}

	/**
	 * Initialise the log files which are written during a reconstruction
	 * attempt.
	 */
	private void initLogFiles(String outputPath, int neuronIdx, int maxNeurons) throws IOException {
		if (outputPath == null)
			return;

		try (BufferedWriter bfInput = new BufferedWriter(
				new FileWriter(outputPath + "/fitness_neuron-" + neuronIdx + ".csv", true))) {
			bfInput.write("fitness");
			bfInput.write(System.lineSeparator());
		}
		try (BufferedWriter bfInput = new BufferedWriter(
				new FileWriter(outputPath + "/model_neuron-" + neuronIdx + ".csv", true))) {
			bfInput.write("a;b;c;d;p1;p2;p3;p4;u;startU;v");
			bfInput.write(System.lineSeparator());
		}
		try (BufferedWriter bfInput = new BufferedWriter(
				new FileWriter(outputPath + "/weights_neuron-" + neuronIdx + ".csv", true))) {
			for (int i = 0; i < maxNeurons; i++) {
				bfInput.write("" + i);
				if (i + 1 < maxNeurons) {
					bfInput.write(";");
				}
			}
			bfInput.write(System.lineSeparator());
		}
	}

	/**
	 * Log the progress during a reconstruction attempt in a file.
	 */
	private void log(String outputPath, String message) throws IOException {
		System.out.println(message);
		System.out.flush();

		if (outputPath == null)
			return;

		try (BufferedWriter bfInput = new BufferedWriter(new FileWriter(outputPath + "/reconstruction.log", true))) {
			bfInput.write(message);
			bfInput.write(System.lineSeparator());
		}
	}

	/**
	 * Log relevant values during a reconstruction attempt in various files.
	 */
	private void logModel(String outputPath, Individual ind, int neuronIdx) throws IOException {
		logFitness(outputPath, ind.getFitness(), neuronIdx);
		logParameters(outputPath, ind.getModel(), neuronIdx);
		logWeights(outputPath, ind.getModel().weights, neuronIdx);
	}

	/**
	 * Log the fitness during a reconstruction attempt and log it in a file.
	 */
	private void logFitness(String outputPath, double fitness, int neuronIdx) throws IOException {
		if (outputPath == null)
			return;

		try (BufferedWriter bfInput = new BufferedWriter(
				new FileWriter(outputPath + "/fitness_neuron-" + neuronIdx + ".csv", true))) {
			bfInput.write(fitness + "");
			bfInput.write(System.lineSeparator());
		}
	}

	/**
	 * Log the model parameters during a reconstruction attempt and log it in a
	 * file.
	 */
	private void logParameters(String outputPath, Model model, int neuronIdx) throws IOException {
		if (outputPath == null)
			return;

		try (BufferedWriter bfInput = new BufferedWriter(
				new FileWriter(outputPath + "/model_neuron-" + neuronIdx + ".csv", true))) {
			bfInput.write("" + model.a);
			bfInput.write(";");
			bfInput.write("" + model.b);
			bfInput.write(";");
			bfInput.write("" + model.c);
			bfInput.write(";");
			bfInput.write("" + model.d);
			bfInput.write(";");
			bfInput.write("" + model.p1);
			bfInput.write(";");
			bfInput.write("" + model.p2);
			bfInput.write(";");
			bfInput.write("" + model.p3);
			bfInput.write(";");
			bfInput.write("" + model.p4);
			bfInput.write(";");
			bfInput.write("" + model.u);
			bfInput.write(";");
			bfInput.write("" + model.startU);
			bfInput.write(";");
			bfInput.write("" + model.v);
			bfInput.write(System.lineSeparator());
		}
	}

	/**
	 * Log the estimated weight during a reconstruction attempt and log it in a
	 * file.
	 */
	private void logWeights(String outputPath, double[] weights, int neuronIdx) throws IOException {
		if (outputPath == null)
			return;

		try (BufferedWriter bfInput = new BufferedWriter(
				new FileWriter(outputPath + "/weights_neuron-" + neuronIdx + ".csv", true))) {
			for (int i = 0; i < weights.length; i++) {
				bfInput.write("" + weights[i]);
				if (i + 1 < weights.length) {
					bfInput.write(";");
				}
			}
			bfInput.write(System.lineSeparator());
		}
	}

	/**
	 * Write the given input and voltage data to files in the given output path.
	 */
	private void writeResults(double[][] inputs, double[][] vs, double[][] vsReconstructed, List<Model> resultModels,
			String outputPath) throws IOException {
		new File(outputPath).mkdirs();
		for (int n = 0; n < vs[0].length; n++) {
			try (BufferedWriter bfInput = new BufferedWriter(
					new FileWriter(outputPath + "/input" + "_neuron-" + n + ".csv"));
					BufferedWriter bfGenerated = new BufferedWriter(
							new FileWriter(outputPath + "/voltage" + "_neuron-" + n + ".csv"))) {

				bfGenerated.write("target;model" + System.lineSeparator());
				bfInput.write("current" + System.lineSeparator());
				for (int i = 0; i < inputs.length; i++) {
					bfGenerated.write(vs[i][n] + ";" + vsReconstructed[i][n] + System.lineSeparator());
					bfInput.write(inputs[i][n] + System.lineSeparator());
				}
			}
		}
	}

	/**
	 * Calculates a full network over all time steps and outputs the resulting
	 * voltage values.
	 */
	private double[][] calculateAFullNetworkSimulation(double[][] inputs, double[][] vs, List<Model> neurons,
			int inputIdxAfterTuning, double dt) {
		// Clone the models so that the original model instances are not
		// modified
		List<Model> nNeurons = new LinkedList<>();
		for (Model m : neurons) {
			nNeurons.add(new Model(m));
		}
		// Calculate the network and put all intermediate voltage values in the
		// result array
		double[][] calculatedVValues = new double[vs.length][vs[0].length];
		for (int i = inputIdxAfterTuning; i < inputs.length; i++) {
			for (int n = 0; n < inputs[0].length; n++) {
				calculatedVValues[i][n] = nNeurons.get(n).v;
			}
			Model.calculateNetwork(dt, nNeurons, inputs[i]);
		}
		return calculatedVValues;
	}

	/**
	 * Calculate the diversity in the given population.
	 * 
	 * The formula is described in the thesis in more detail.
	 */
	private double getDiversity(List<Individual> population) {
		Map<Double, Integer> diversityCheckSet = new HashMap<>();

		for (Individual ind : population) {
			double fitness = ind.getFitness();
			Integer number = diversityCheckSet.get(fitness);
			if (number == null) {
				number = 1;
			} else {
				number++;
			}
			diversityCheckSet.put(fitness, number);
		}
		return diversityCheckSet.size() / (double) population.size();
	}

	/**
	 * Return a random individual with respect to the implemented selection
	 * method and the probability distribution.
	 */
	private Individual select(Random r, List<Individual> population) {
		return population.get(selectIndex(r, population.size()));
	}

	/**
	 * Select a random index out of a generation by relying on the implemented
	 * selection method of the Genetic Algorithm.
	 */
	private static int selectIndex(Random r, int populationSize) {
		int chosenInd = 0;

		double p = r.nextDouble();
		double cumulativeProbability = 0.0;
		int i = r.nextInt(populationSize);
		while (p >= cumulativeProbability) {
			cumulativeProbability += calculateProbability(populationSize, i);
			if (p <= cumulativeProbability) {
				chosenInd = i;
			}
			i++;
			i %= populationSize;
		}
		return chosenInd;
	}

	/**
	 * Calculate the probability for selecting with the Exponential Selection
	 * Method.
	 */
	private static double calculateProbability(int size, int rank) {
		return Math.max(0, 1 - Math.exp(-.005 * (size - (size * .05) - rank)));
	}

	/**
	 * Helper function to convert a time range to a string representation
	 */
	private String toTime(long starttime, long endtime) {
		long millis = endtime - starttime;
		return String.format("%02d:%02d:%02d:%03d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
				millis - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)));
	}

	/**
	 * Do crossover by combining two individuals and return the new individual.
	 */
	public Model crossover(Random r, Model m1, Model m2) {
		Model mr = new Model(m1);

		if (r.nextBoolean()) {
			mr.a = m2.a;
		}

		if (r.nextBoolean()) {
			mr.b = m2.b;
		}

		if (r.nextBoolean()) {
			mr.c = m2.c;
		}

		if (r.nextBoolean()) {
			mr.d = m2.d;
		}

		if (r.nextBoolean()) {
			mr.p1 = m2.p1;
		}

		if (r.nextBoolean()) {
			mr.p2 = m2.p2;
		}

		if (r.nextBoolean()) {
			mr.p3 = m2.p3;
		}

		if (r.nextBoolean()) {
			mr.startU = m2.startU;
		}

		return mr;
	}

	/**
	 * Mutate a model instance by mutating one of its parameters.
	 */
	public void mutate(ReconstructionModeAbstract mode, Random r, Model model) {
		int param = r.nextInt(8);
		switch (param) {
		case 0:
			model.a = incrementGrayCode(r, model.a, mode.getMinA(), mode.getMaxA());
			break;
		case 1:
			model.b = incrementGrayCode(r, model.b, mode.getMinB(), mode.getMaxB());
			break;
		case 2:
			model.c = incrementGrayCode(r, model.c, mode.getMinC(), mode.getMaxC());
			break;
		case 3:
			model.d = incrementGrayCode(r, model.d, mode.getMinD(), mode.getMaxD());
			break;
		case 4:
			model.p1 = incrementGrayCode(r, model.p1, mode.getMinP1(), mode.getMaxP1());
			break;
		case 5:
			model.p2 = incrementGrayCode(r, model.p2, mode.getMinP2(), mode.getMaxP2());
			break;
		case 6:
			model.p3 = incrementGrayCode(r, model.p3, mode.getMinP3(), mode.getMaxP3());
			break;
		case 7:
			model.startU = incrementGrayCode(r, model.startU, mode.getMinU(), mode.getMaxU());
			break;
		}
	}

	/**
	 * Increments a value in Gray Code representation.
	 * 
	 * In order to do this the value is converted from a floating point to a
	 * binary and Gray Code representation. Afterwards it is mutated and then
	 * converted back to its floating point representation.
	 */
	private double incrementGrayCode(Random r, double value, double minValue, double maxValue) {
		double nVal = value;
		if (maxValue != minValue) {
			int asLong = (int) ((value - minValue) / (maxValue - minValue) * Integer.MAX_VALUE);
			int asLongGray = binaryToGray(asLong);
			asLongGray ^= bitArray[r.nextInt(bitArray.length)];
			asLong = grayToBinary(asLongGray);
			nVal = ((double) asLong / (double) Integer.MAX_VALUE) * (maxValue - minValue) + minValue;
		}

		return nVal;
	}

	/**
	 * Convert an integer to Gray Code.
	 */
	private int binaryToGray(int num) {
		return num ^ (num >> 1);
	}

	/**
	 * Convert a Gray Code number to an integer.
	 */
	private int grayToBinary(int num) {
		int mask;
		for (mask = num >> 1; mask != 0; mask = mask >> 1) {
			num = num ^ mask;
		}
		return num;
	}

	/**
	 * Helper array with common values for the Gray Code mutation.
	 */
	private final int bitArray[] = { 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864,
			268435456 };

}
