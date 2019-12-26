/** ValueBorders.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.model;

/**
 * ValueBorders contains constants for all parameters for the generation of new
 * neurons as well as the constants for the spike recognition.
 * 
 * Be aware that the parameter ranges for the reconstruction are defined in the
 * package:
 * {@code de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes}
 * 
 * 
 * @see de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.
 *      modes.ReconstructionModeAbstract
 * @author Leah Lackner
 */
public class ValueBorders {

	public static final double MIN_A_GENERATION = 0.01;
	public static final double MAX_A_GENERATION = 0.1;

	public static final double MIN_B_GENERATION = 0.05;
	public static final double MAX_B_GENERATION = 0.3;

	public static final double MIN_C_GENERATION = -65;
	public static final double MAX_C_GENERATION = -40;

	public static final double MIN_D_GENERATION = .05;
	public static final double MAX_D_GENERATION = 8;

	public static final double MIN_P1_GENERATION = 0.04;
	public static final double MAX_P1_GENERATION = 0.04;

	public static final double MIN_P2_GENERATION = 5;
	public static final double MAX_P2_GENERATION = 5;

	public static final double MIN_P3_GENERATION = 140;
	public static final double MAX_P3_GENERATION = 140;

	public static final double MIN_P4_GENERATION = 1;
	public static final double MAX_P4_GENERATION = 1;

	public static final double MIN_U_GENERATION = -30;
	public static final double MAX_U_GENERATION = 30;

	public static final double MIN_INPUT_GENERATION = 30;
	public static final double MAX_INPUT_GENERATION = 30;

	public static final double MIN_WEIGHT_GENERATION = 0;
	public static final double MAX_WEIGHT_GENERATION = 9.99999;

	public static final double PERCENTAGE_INHIBITORY_WEIGHTS = 0.3;

	public static final double MIN_V_START_GENERATION = -75;
	public static final double MAX_V_START_GENERATION = -50;

	public static final double NOISE_V_MIN_GENERATION = -2;
	public static final double NOISE_V_MAX_GENERATION = 2;

	public static final double NOISE_INPUT_MIN_GENERATION = -0.1;
	public static final double NOISE_INPUT_MAX_GENERATION = 0.1;

	// Disable the generated noise
	/*
	 * public static final double NOISE_V_MIN_GENERATION = 0; public static
	 * final double NOISE_V_MAX_GENERATION = 0;
	 * 
	 * public static final double NOISE_INPUT_MIN_GENERATION = 0; public static
	 * final double NOISE_INPUT_MAX_GENERATION = 0;
	 */

	/**
	 * The value which determines when the neuron has to be reset.
	 */
	public static final double SPIKE_RESET = 30;

	/**
	 * The value which determines the lowest value for which a spike has to be
	 * detected. Note that the neuron will not be reset at this value.
	 */
	public static final double SPIKE_RECOGNITION = 0;

}
