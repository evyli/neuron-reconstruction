/** ReconstructionMode.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes;

/**
 * Java enumeration containing all implemented reconstruction modes.
 * 
 * @author Leah Lackner
 */
public enum ReconstructionMode {

	IZHIKEVICH(new IzhikevichMode()), IZHIKEVICH_EXTENDED(new IzhikevichExtendedMode()), IZHIKEVICH_SINGLE_NEURON(
			new IzhikevichSingleNeuronMode());

	private ReconstructionMode(ReconstructionModeAbstract mode) {
		this.mode = mode;
	}

	private ReconstructionModeAbstract mode;

	public ReconstructionModeAbstract getMode() {
		return mode;
	}

}
