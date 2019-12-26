/** Model.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.model;

import static de.hsmannheim.masterthesis.neuronreconstruction.impl.model.ValueBorders.*;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes.ReconstructionModeAbstract;

/**
 * The class for the model computation of the used Izhikevich model.
 * 
 * <p />
 * Instead of the Izhikevich reset condition, the following equations are used:
 * 
 * <p />
 * h = 1.0 / (1.0 - Math.exp(10000 * (SPIKE_RESET - v)))<br />
 * v <- v * (1.0 - h) + c * h<br />
 * u <- u + d * h<br />
 * 
 * <p />
 * The other equations are the same as used in the Izhikevich model. To
 * generalise the model the remaining constants are replaced by the variables
 * p1, p2, p3, and p4.
 * 
 * <p />
 * v <- v + dt * (p1 * v * v + p2 * v + p3 - u + p4 * input)<br />
 * u <- u + dt * (a * (b * v - u))
 * 
 * <p />
 * See also the Master's Thesis for more details about the chosen model
 * equations.
 * 
 * @author Leah Lackner
 */
public class Model {

	/**
	 * The unique index of the neuron in the network.
	 */
	public int neuronIndex;

	/**
	 * The number of all neurons in the network.
	 */
	public int numNeurons;

	/**
	 * The parameter a of the original Izhikevich formula.
	 */
	public double a;

	/**
	 * The parameter b of the original Izhikevich formula.
	 */
	public double b;

	/**
	 * The parameter c of the original Izhikevich formula.
	 */
	public double c;

	/**
	 * The parameter d of the original Izhikevich formula.
	 */
	public double d;

	/**
	 * The custom model parameter p1. See the class javadoc for more details.
	 */
	public double p1;

	/**
	 * The custom model parameter p2. See the class javadoc for more details.
	 */
	public double p2;

	/**
	 * The custom model parameter p3. See the class javadoc for more details.
	 */
	public double p3;

	/**
	 * The custom model parameter p4. See the class javadoc for more details.
	 * 
	 * This model parameter is currently unused and therefore defaults to 1.
	 */
	public double p4;

	/**
	 * The voltage state of the neuron.
	 */
	public double v;

	/**
	 * The recovery variable state of the neuron.
	 */
	public double u;

	/**
	 * The value of the u value which was reconstructed for the time step zero.
	 * After the reconstruction the value of u will be set to the correct u
	 * value for the time step 'inputIdxAfterTuning' in the reconstruction.
	 * 
	 * The parameter startU on the other hand will remain and keep the initial
	 * guessed value.
	 */
	public double startU;

	/**
	 * The weights to the other neurons outgoing from the current neuron object.
	 */
	public double[] weights;

	/**
	 * Creates an exact clone of another Model object.
	 * 
	 * @param m
	 *            The other Model object
	 */
	public Model(Model m) {
		neuronIndex = m.neuronIndex;
		numNeurons = m.numNeurons;

		a = m.a;
		b = m.b;
		c = m.c;
		d = m.d;
		p1 = m.p1;
		p2 = m.p2;
		p3 = m.p3;
		p4 = m.p4;
		v = m.v;
		u = m.u;
		startU = m.startU;

		weights = new double[m.weights.length];
		for (int n = 0; n < m.weights.length; n++) {
			weights[n] = m.weights[n];
		}
	}

	/**
	 * Creates a model with random values which is required during the
	 * reconstruction.
	 * 
	 * @param mode
	 *            The parameter range for the model generation
	 * @param r
	 *            The random object
	 * @param numNeurons
	 *            The number of all neurons in the network
	 * @param neuronIndex
	 *            The index of the current neuron
	 */
	public Model(ReconstructionModeAbstract mode, Random r, int numNeurons, int neuronIndex) {
		this.numNeurons = numNeurons;
		this.neuronIndex = neuronIndex;

		a = generate(r, mode.getMinA(), mode.getMaxA());
		b = generate(r, mode.getMinB(), mode.getMaxB());
		c = generate(r, mode.getMinC(), mode.getMaxC());
		d = generate(r, mode.getMinD(), mode.getMaxD());
		p1 = generate(r, mode.getMinP1(), mode.getMaxP1());
		p2 = generate(r, mode.getMinP2(), mode.getMaxP2());
		p3 = generate(r, mode.getMinP3(), mode.getMaxP3());
		p4 = generate(r, mode.getMinP4(), mode.getMaxP4());
		u = startU = generate(r, mode.getMinU(), mode.getMaxU());

		weights = new double[numNeurons];
	}

	/**
	 * Creates a model with random values which is required during the
	 * generation of a model which is meant to be reconstructed afterwards.
	 * 
	 * @param r
	 *            The random object
	 * @param numNeurons
	 *            The number of all neurons in the network
	 * @param neuronIndex
	 *            The index of the current neuron
	 */
	public Model(Random r, int numNeurons, int neuronIndex) {
		a = generate(r, MIN_A_GENERATION, MAX_A_GENERATION);
		b = generate(r, MIN_B_GENERATION, MAX_B_GENERATION);
		c = generate(r, MIN_C_GENERATION, MAX_C_GENERATION);
		d = generate(r, MIN_D_GENERATION, MAX_D_GENERATION);
		p1 = generate(r, MIN_P1_GENERATION, MAX_P1_GENERATION);
		p2 = generate(r, MIN_P2_GENERATION, MAX_P2_GENERATION);
		p3 = generate(r, MIN_P3_GENERATION, MAX_P3_GENERATION);
		p4 = generate(r, MIN_P4_GENERATION, MAX_P4_GENERATION);
		u = startU = generate(r, MIN_U_GENERATION, MAX_U_GENERATION);
		v = generate(r, MIN_V_START_GENERATION, MAX_V_START_GENERATION);

		weights = new double[numNeurons];
		for (int n = 0; n < weights.length; n++) {
			// no self connection!
			if (n == neuronIndex)
				weights[n] = 0;
			else {
				weights[n] = generate(r, MIN_WEIGHT_GENERATION, MAX_WEIGHT_GENERATION);

				// inhibitory
				if (n >= weights.length * (1.0 - PERCENTAGE_INHIBITORY_WEIGHTS))
					weights[n] *= -1;
			}
		}
	}

	/**
	 * Calculate the instance of the Izhikevich model for the given delta t and
	 * the given input.
	 * 
	 * @param dt
	 *            The delta t value for the solving of the differential
	 *            equations.
	 * @param input
	 *            The input which is injected to the current neuron.
	 */
	public void calculateModel(double dt, double input) {
		double[] results = new double[2];
		calculateModelIntern(dt, input, results);
		v = results[0];
		u = results[1];
	}

	/**
	 * Internal method which solves the model equations for one time step.
	 * 
	 * @param dt
	 *            The delta t value for the solving of the differential
	 *            equations.
	 * @param input
	 *            The input which is injected to the current neuron.
	 * 
	 * @param results
	 *            The result array because java does not support multiple return
	 *            values. result[0] contains the new voltage value and result[1]
	 *            contains the new u value.
	 */
	private void calculateModelIntern(double dt, double input, double[] results) {

		calculateModel(a, b, c, d, p1, p2, p3, p4, dt, v, u, input, results);

	}

	/**
	 * Calculates the part of the Izhikevich model which is normally solved with
	 * an if-condition. In our implementation it is solved with an exponential
	 * function depending on the spike reset value to simulate the hard reset of
	 * the voltage value.
	 */
	private static void calculateModel1(double a, double b, double c, double d, double p1, double p2, double p3,
			double p4, double dt, double v, double u, double[] results) {

		double h = 1.0 / (1.0 - Math.exp(10000 * (SPIKE_RESET - v)));

		double newV1 = v * (1.0 - h) + c * h;
		double newU1 = u + d * h;

		results[0] = newV1;
		results[1] = newU1;
	}

	/**
	 * Calculates the normal part of the Izhikevich model after the neuron was
	 * reset if necessary.
	 */
	private static void calculateModel2(double a, double b, double c, double d, double p1, double p2, double p3,
			double p4, double dt, double v, double u, double input, double[] results) {

		double newV2 = v + dt * (p1 * v * v + p2 * v + p3 - u + p4 * input);
		double newU2 = u + dt * (a * (b * v - u));

		results[0] = newV2;
		results[1] = newU2;
	}

	/**
	 * General function to calculate the model equatiosn for the given
	 * parameters and writes the result in the given results array.
	 */
	public static void calculateModel(double a, double b, double c, double d, double p1, double p2, double p3,
			double p4, double dt, double v, double u, double input, double[] results) {
		results[0] = v;
		results[1] = u;

		calculateModel1(a, b, c, d, p1, p2, p3, p4, dt, results[0], results[1], results);
		calculateModel2(a, b, c, d, p1, p2, p3, p4, dt, results[0], results[1], input, results);

	}

	/**
	 * Calculates a neuron for one time step. The interconnected neurons and
	 * their v state value will be used to simulate also the interconnections
	 * and the exchanged data between the neurons to the current neuron. The
	 * resulting state will only be calculated and stored for the current
	 * neuron.
	 * 
	 * @param dt
	 *            The delta t value for the solving of the differential
	 *            equations.
	 * @param vs
	 *            The voltage value of all neurons in the network of the last
	 *            time step
	 * @param input
	 *            The input value which is injected in the current neuron
	 */
	public void calculateNetwork(double dt, double[] vs, double input) {
		int maxNeurons = vs.length;

		double inputValue = input;
		for (int n1 = 0; n1 < maxNeurons; n1++) {
			double isSpike = 0;
			if (vs[n1] >= SPIKE_RECOGNITION) {
				isSpike = 1.0;
			}
			inputValue += isSpike * weights[n1];
		}

		double[] results = new double[2];
		calculateModelIntern(dt, inputValue, results);

		v = results[0];
		u = results[1];
	}

	/**
	 * Calculates a full network of interconnected neurons for one single time
	 * step. The resulting state will be stored in the models themselves.
	 *
	 * @param dt
	 *            The delta t value for the solving of the differential
	 *            equations.
	 * @param models
	 *            The models of the network
	 * @param input
	 *            An input array with an input value for each neuron of the
	 *            network
	 */
	public static void calculateNetwork(double dt, List<Model> models, double[] input) {
		int maxNeurons = models.size();

		double[] newV = new double[maxNeurons];
		double[] newU = new double[maxNeurons];
		for (int n0 = 0; n0 < maxNeurons; n0++) {
			double inputValue = input[n0];
			for (int n1 = 0; n1 < maxNeurons; n1++) {
				double isSpike = 0;
				if (models.get(n1).v >= SPIKE_RECOGNITION) {
					isSpike = 1.0;
				}
				inputValue += isSpike * models.get(n0).weights[n1];
			}

			Model model = models.get(n0);

			double[] results = new double[2];
			model.calculateModelIntern(dt, inputValue, results);

			newV[n0] = results[0];
			newU[n0] = results[1];
		}

		for (int n0 = 0; n0 < maxNeurons; n0++) {
			Model instance = models.get(n0);
			instance.v = newV[n0];
			instance.u = newU[n0];
		}
	}

	/**
	 * Generates a random input value which can be injected into a neuron.
	 * 
	 * @param r
	 *            The random object
	 * 
	 * @return The generated random input value
	 */
	public static double generateInput(Random r) {
		return generate(r, ValueBorders.MIN_INPUT_GENERATION, ValueBorders.MAX_INPUT_GENERATION);
	}

	/**
	 * Helper function to generate a random double value in the given range.
	 * 
	 * @param r
	 *            The random object
	 * @param min
	 *            The minimum value (inclusive)
	 * @param max
	 *            The maximum value (exclusive)
	 * 
	 * @return The generated random value
	 */
	public static double generate(Random r, double min, double max) {
		return min + (max - min) * r.nextDouble();
	}

	@Override
	public String toString() {
		StringBuffer weightsString = new StringBuffer();
		for (int i = 0; i < weights.length; i++) {
			double weight = weights[i];
			weightsString.append(weight);
			if (i + 1 < weights.length)
				weightsString.append(", ");
		}
		return String.format(Locale.UK,
				"a=%.5f, b=%.5f, c=%.5f, d=%.5f, p1=%.5f, p2=%.5f, p3=%.5f, p4=%.5f, u=%.5f, startU=%.5f, weights=[%s]",
				a, b, c, d, p1, p2, p3, p4, u, startU, weightsString);
	}

}
