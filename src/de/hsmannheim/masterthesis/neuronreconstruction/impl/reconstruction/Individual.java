/** Individual.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction;

import java.util.Map;
import java.util.Random;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.model.Model;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes.ReconstructionModeAbstract;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.SpikeTrainComparator;

/**
 * An individual during the Genetic Algorithm which holds the associated model
 * instance and its fitness value.
 * 
 * @author Leah Lackner
 */
public class Individual implements Comparable<Individual> {

	private Model model;

	private double fitness;

	/**
	 * Create an individual for the given model
	 * 
	 * @param model
	 *            the neuron model
	 */
	public Individual(Model model) {
		this.model = model;
	}

	/**
	 * Clone an individual
	 * 
	 * @param ind
	 *            the individual to be cloned
	 */
	public Individual(Individual ind) {
		this.model = new Model(ind.model);
		this.fitness = ind.fitness;
	}

	/**
	 * Recalculate the individual in order to determine the fitness of the
	 * neuron model.
	 * 
	 * The internal model is replaced by a new randomly generated model if the
	 * old model yielded infinite results.
	 * 
	 * @param mode
	 *            the parameter ranges
	 * @param r
	 *            the random instance
	 * @param inputs
	 *            the input values (currents)
	 * @param vs
	 *            the voltage values
	 * @param dt
	 *            the dt value for the solving of the differential equations
	 * @param inputIdxAfterTuning
	 *            the index of the time step splitting the two parts of the
	 *            reconstruction
	 */
	public void recalculate(ReconstructionModeAbstract mode, Random r, double[][] inputs, double[][] vs, double dt,
			int inputIdxAfterTuning, Map<Class<?>, Double> measures) {
		while (true) {
			model = IndividualReconstructionData.reconstructValuesOfModel(this, r, inputs, vs, dt, inputIdxAfterTuning);

			// Various intermediate results are computed.
			// This is done because otherwise each error function would have to
			// compute these results by itself, which would increase the overall
			// reconstruction time.

			// Store the input and voltage values without the values of the
			// u-tuning
			double[] realPotentials = new double[vs.length - inputIdxAfterTuning];
			double[] realInputs = new double[inputs.length - inputIdxAfterTuning];
			for (int i = inputIdxAfterTuning, j = 0; i < inputs.length; i++, j++) {
				realPotentials[j] = vs[i][model.neuronIndex];
				realInputs[j] = inputs[i][model.neuronIndex];
			}

			// calculate the single step changes
			double[] modelPotentialsSingleStep = new double[realPotentials.length];
			modelPotentialsSingleStep[0] = realPotentials[0];

			Model model = new Model(this.model);
			for (int i = inputIdxAfterTuning, j = 0; i < inputs.length; i++, j++) {
				model.v = vs[i][model.neuronIndex];
				model.calculateNetwork(dt, vs[i - 1], inputs[i][model.neuronIndex]);

				modelPotentialsSingleStep[j] = model.v;
			}

			// compute the whole simulation
			double[] modelPotentials = new double[realPotentials.length];
			modelPotentials[0] = realPotentials[0];

			model = new Model(this.model);
			model.v = realPotentials[0];
			for (int i = inputIdxAfterTuning, j = 0; i < inputs.length; i++, j++) {
				model.calculateNetwork(dt, vs[i - 1], inputs[i][model.neuronIndex]);

				modelPotentials[j] = model.v;
			}

			// Calculate the fitness value
			// In order to do this all the intermediate results of the spike
			// trains are handed over to the spike comparison function.
			fitness = SpikeTrainComparator.compareSpikeTrains(realPotentials, modelPotentials,
					modelPotentialsSingleStep, dt, measures);
			// Replace the individual if it is invalid in regard to the
			// individual validation
			if (Double.isFinite(fitness)) {
				break;
			} else {
				this.model = new Model(mode, r, model.numNeurons, model.neuronIndex);
			}
		}
	}

	/**
	 * @return return the internal neuron model of the individual
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @return return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

	@Override
	public int compareTo(Individual ind2) {
		return Double.compare(fitness, ind2.fitness);
	}

	@Override
	public String toString() {
		return model + "; fitness=" + fitness + "  ";
	}

}
