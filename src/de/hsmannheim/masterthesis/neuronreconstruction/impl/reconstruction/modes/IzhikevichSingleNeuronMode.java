/** IzhikevichMode.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes;

/**
 * Reconstruction mode with a parameter set based on the single (RS) neuron
 * model described by Izhikevich.
 * 
 * <p />
 * See also the {@code Model} class for details about the model equations.
 * 
 * @see de.hsmannheim.masterthesis.neuronreconstruction.impl.model.Model
 * 
 * @author Leah Lackner
 */
public class IzhikevichSingleNeuronMode extends ReconstructionModeAbstract {

	private static final double MIN_A = 0.01;
	private static final double MAX_A = 0.1;

	private static final double MIN_B = -0.1;
	private static final double MAX_B = 0.3;

	private static final double MIN_C = -65;
	private static final double MAX_C = -30;

	private static final double MIN_D = 0.05;
	private static final double MAX_D = 8;

	private static final double MIN_P1 = 0.04;
	private static final double MAX_P1 = 0.04;

	private static final double MIN_P2 = 4.1;
	private static final double MAX_P2 = 4.1;

	private static final double MIN_P3 = 108;
	private static final double MAX_P3 = 108;

	private static final double MIN_P4 = 1;
	private static final double MAX_P4 = 1;

	private static final double MIN_U = -20;
	private static final double MAX_U = 15;

	public IzhikevichSingleNeuronMode() {
		super(MIN_A, MAX_A, MIN_B, MAX_B, MIN_C, MAX_C, MIN_D, MAX_D, MIN_P1, MAX_P1, MIN_P2, MAX_P2, MIN_P3, MAX_P3,
				MIN_P4, MAX_P4, MIN_U, MAX_U);
	}

}
