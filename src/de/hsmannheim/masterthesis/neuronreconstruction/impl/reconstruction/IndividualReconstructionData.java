/** IndividualReconstructionData.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction;

import java.util.Random;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.model.Model;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.model.ValueBorders;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.math.GaussianElimination;

/**
 * Contains the model data during a reconstruction attempt.
 * 
 * @author Leah Lackner
 */
public class IndividualReconstructionData {

	private Model model;
	@SuppressWarnings("unused")
	private Random r;
	private double[][] inputs;
	private double[][] vs;
	private double dt;
	private int inputIdxAfterTuning;

	private double[][] isSpike;
	private double[] uGuess;
	private double[] vFactorGuess;

	private double spikeval = ValueBorders.SPIKE_RESET;

	public static Model reconstructValuesOfModel(Individual individual, Random r, double[][] inputs, double[][] vs,
			double dt, int inputIdxAfterTuning) {
		IndividualReconstructionData reconstr = new IndividualReconstructionData(new Individual(individual), r, inputs,
				vs, dt, inputIdxAfterTuning);
		return reconstr.model;
	}

	/**
	 * Initialises the reconstruction data for the given individual and the
	 * given data sets.
	 * 
	 * @param individual
	 *            The individual containing the model
	 * @param r
	 *            The random instance
	 * @param inputs
	 *            the input values (currents)
	 * @param vs
	 *            the voltage values
	 * @param dt
	 *            the dt value for the solving of the differential equations
	 * @param inputIdxAfterTuning
	 *            the index at which the input data is split apart.
	 */
	private IndividualReconstructionData(Individual individual, Random r, double[][] inputs, double[][] vs, double dt,
			int inputIdxAfterTuning) {
		this.model = individual.getModel();

		this.r = r;
		this.inputs = inputs;
		this.vs = vs;
		this.dt = dt;
		this.inputIdxAfterTuning = inputIdxAfterTuning;

		// modifyParameters();

		doReconstruction();
	}

	/**
	 * Helper method to set a predefined reconstruction instance with ten
	 * interconnected neurons.
	 */
	@SuppressWarnings("unused")
	private void modifyParameters() {
		switch (model.neuronIndex) {
		case 0:
			model.a = 0.0563;
			model.b = 0.0917;
			model.c = -41.863;
			model.d = 3.690;
			model.u = 8.799;
			model.startU = -29.910;
			break;
		case 1:
			model.a = 0.0112;
			model.b = 0.157;
			model.c = -55.87;
			model.d = 3.23;
			model.u = 11.44;
			model.startU = -12.65;
			break;
		case 2:
			model.a = 0.0799;
			model.b = 0.2477;
			model.c = -43.14;
			model.d = 2.75;
			model.u = -6.95;
			model.startU = 3.92;
			break;
		case 3:
			model.a = 0.0324;
			model.b = 0.214;
			model.c = -48.89;
			model.d = 0.83;
			model.u = -6.42;
			model.startU = -11.205;
			break;
		case 4:
			model.a = 0.046;
			model.b = 0.106;
			model.c = -47.94;
			model.d = 3.11;
			model.u = 6.743;
			model.startU = -5.45;
			break;
		case 5:
			model.a = 0.063;
			model.b = 0.056;
			model.c = -40.76;
			model.d = 6.02;
			model.u = 14.03;
			model.startU = 9.915;
			break;
		case 6:
			model.a = 0.0477;
			model.b = 0.253;
			model.c = -49.207;
			model.d = 7.616;
			model.u = 1.297;
			model.startU = -23.04;
			break;
		case 7:
			model.a = 0.075;
			model.b = 0.25;
			model.c = -64.75;
			model.d = 7.176;
			model.u = -4.174;
			model.startU = 3.29;
			break;
		case 8:
			model.a = 0.076;
			model.b = 0.156;
			model.c = -46.33;
			model.d = 6.422;
			model.u = 1.85;
			model.startU = 20.269;
			break;
		case 9:
			model.a = 0.048;
			model.b = 0.273;
			model.c = -63.754;
			model.d = 1.682;
			model.u = -9.06;
			model.startU = 18.018;
			break;
		}
	}

	/**
	 * Do the reconstruction of the other model parameters (u and weights).
	 */
	private void doReconstruction() {
		reconstructOtherValues();

		if (vs[0].length > 1) {
			isSpike = new double[vs.length][vs[0].length];
			uGuess = new double[vs.length];
			vFactorGuess = new double[vs.length];

			reconstructSpikes();
			reconstructUAndVFactor();
			reconstructWeights();

			isSpike = null;
			uGuess = null;
			vFactorGuess = null;
		}
	}

	/**
	 * Tuning of the u variable
	 */
	private void reconstructOtherValues() {
		model = new Model(model);
		model.u = model.startU;
		for (int i = 1; i < inputIdxAfterTuning; i++) {
			model.v = vs[i][model.neuronIndex];
			model.calculateNetwork(dt, vs[i - 1], inputs[i][model.neuronIndex]);
		}
		this.model.v = vs[inputIdxAfterTuning][model.neuronIndex];
	}

	/**
	 * Determine spike timings for the weight estimation
	 * 
	 * Adopted from the implementations of the preceding theses.
	 */
	private void reconstructSpikes() {
		for (int t = 1; t < vs.length; t++) {
			for (int n = 0; n < model.numNeurons; n++) {
				isSpike[t][n] = (vs[t - 1][n] >= spikeval) ? 1 : 0;
			}
		}
	}

	/**
	 * Determine intermediate u and v values for the weight estimation
	 * 
	 * Adopted from the implementations of the preceding theses.
	 */
	private void reconstructUAndVFactor() {
		uGuess[inputIdxAfterTuning] = model.u;

		for (int t = inputIdxAfterTuning + 1; t < vs.length; t++) {
			double a = model.a;
			double b = model.b;
			@SuppressWarnings("unused")
			double c = model.c;
			double d = model.d;
			double p1 = model.p1;
			double p2 = model.p2;
			double p3 = model.p3;
			@SuppressWarnings("unused")
			double p4 = model.p4;

			// calculate Uguess
			uGuess[t] = uGuess[t - 1] + dt * a * (b * vs[t - 1][model.neuronIndex] - uGuess[t - 1]);

			// add d to uguess if membrane potential threshold is reached
			// (neuron has spiked)
			if (isSpike[t - 1][model.neuronIndex] != 0)
				uGuess[t] = uGuess[t - 1] + d;

			// calculate Vfactorguess, because we don't know the weight
			double vOneStepBefore = vs[t - 1][model.neuronIndex];
			vFactorGuess[t] = vOneStepBefore
					+ dt * (p1 * vOneStepBefore * vOneStepBefore + p2 * vOneStepBefore + p3 - uGuess[t - 1]);
		}
	}

	/**
	 * Calculate the weights
	 * 
	 * Adopted from the implementations of the preceding theses.
	 */
	private void reconstructWeights() {
		double[][] matrix = new double[model.numNeurons][model.numNeurons];
		double[] b = new double[model.numNeurons];

		// for each time step where the neuron has not spiked at time step t and
		// t-1
		for (int t = inputIdxAfterTuning + 1; t < vs.length; t++) {

			// calculate only if neuron not spiked at t-1 and t
			if (vs[t - 1][model.neuronIndex] >= spikeval)
				continue;

			// equation is multiplied by 1/dt to get rid of factor dt
			double wSolution = 1 / dt * (vs[t][model.neuronIndex] - vFactorGuess[t]) - inputs[t - 1][model.neuronIndex];

			// fill the equation matrix
			for (int n00 = 0; n00 < model.numNeurons; n00++) {
				b[n00] += 1 / dt * isSpike[t][n00] * wSolution;
			}
			for (int n00 = 0; n00 < model.numNeurons; n00++) {
				for (int n11 = 0; n11 < model.numNeurons; n11++) {
					matrix[n00][n11] += 1 / dt * isSpike[t][n00] * isSpike[t][n11];
				}
			}
		}
		// --------------- Solve ----------------
		double[] calculatedWeightSolution = GaussianElimination.lsolve(matrix, b);

		for (int n1 = 0; n1 < model.numNeurons; n1++) {
			model.weights[n1] = calculatedWeightSolution[n1];
		}
	}
}
