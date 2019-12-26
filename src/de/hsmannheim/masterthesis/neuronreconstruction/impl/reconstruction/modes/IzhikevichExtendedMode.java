/** IzhikevichMode.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes;

/**
 * Reconstruction mode with an extended parameter set.
 * 
 * <p />
 * See also the {@code Model} class for details about the model equations.
 * 
 * @see de.hsmannheim.masterthesis.neuronreconstruction.impl.model.Model
 * 
 * @author Leah Lackner
 */
public class IzhikevichExtendedMode extends ReconstructionModeAbstract {

	private static final double MIN_A = -1;
	private static final double MAX_A = 1;

	private static final double MIN_B = -1;
	private static final double MAX_B = 1;

	private static final double MIN_C = -65;
	private static final double MAX_C = -30;

	private static final double MIN_D = -2;
	private static final double MAX_D = 10;

	private static final double MIN_P1 = 0.01;
	private static final double MAX_P1 = 0.1;

	private static final double MIN_P2 = 1;
	private static final double MAX_P2 = 15;

	private static final double MIN_P3 = 108;
	private static final double MAX_P3 = 150;

	private static final double MIN_P4 = 1;
	private static final double MAX_P4 = 1;

	private static final double MIN_U = -20;
	private static final double MAX_U = 15;

	public IzhikevichExtendedMode() {
		super(MIN_A, MAX_A, MIN_B, MAX_B, MIN_C, MAX_C, MIN_D, MAX_D, MIN_P1, MAX_P1, MIN_P2, MAX_P2, MIN_P3, MAX_P3,
				MIN_P4, MAX_P4, MIN_U, MAX_U);
	}

}
