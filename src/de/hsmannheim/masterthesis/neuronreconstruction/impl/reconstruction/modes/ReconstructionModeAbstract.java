/** ReconstructionModeAbstract.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.modes;

/**
 * Base class for all reconstruction modes. Required to restrict the parameter
 * ranges for the reconstruction.
 * 
 * @author Leah Lackner
 */
public abstract class ReconstructionModeAbstract {

	private double minA;
	private double maxA;

	private double minB;
	private double maxB;

	private double minC;
	private double maxC;

	private double minD;
	private double maxD;

	private double minP1;
	private double maxP1;

	private double minP2;
	private double maxP2;

	private double minP3;
	private double maxP3;

	private double minP4;
	private double maxP4;

	private double minU;
	private double maxU;

	public ReconstructionModeAbstract(double minA, double maxA, double minB, double maxB, double minC, double maxC,
			double minD, double maxD, double minP1, double maxP1, double minP2, double maxP2, double minP3,
			double maxP3, double minP4, double maxP4, double minU, double maxU) {
		super();
		this.minA = minA;
		this.maxA = maxA;
		this.minB = minB;
		this.maxB = maxB;
		this.minC = minC;
		this.maxC = maxC;
		this.minD = minD;
		this.maxD = maxD;
		this.minP1 = minP1;
		this.maxP1 = maxP1;
		this.minP2 = minP2;
		this.maxP2 = maxP2;
		this.minP3 = minP3;
		this.maxP3 = maxP3;
		this.minP4 = minP4;
		this.maxP4 = maxP4;
		this.minU = minU;
		this.maxU = maxU;
	}

	public double getMinA() {
		return minA;
	}

	public double getMaxA() {
		return maxA;
	}

	public double getMinB() {
		return minB;
	}

	public double getMaxB() {
		return maxB;
	}

	public double getMinC() {
		return minC;
	}

	public double getMaxC() {
		return maxC;
	}

	public double getMinD() {
		return minD;
	}

	public double getMaxD() {
		return maxD;
	}

	public double getMinP1() {
		return minP1;
	}

	public double getMaxP1() {
		return maxP1;
	}

	public double getMinP2() {
		return minP2;
	}

	public double getMaxP2() {
		return maxP2;
	}

	public double getMinP3() {
		return minP3;
	}

	public double getMaxP3() {
		return maxP3;
	}

	public double getMinP4() {
		return minP4;
	}

	public double getMaxP4() {
		return maxP4;
	}

	public double getMinU() {
		return minU;
	}

	public double getMaxU() {
		return maxU;
	}

}
