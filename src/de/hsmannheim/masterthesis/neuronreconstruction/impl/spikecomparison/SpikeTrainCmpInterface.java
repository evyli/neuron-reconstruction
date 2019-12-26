/** SpikeTrainCmpInterface.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison;

/**
 * Interface for all error functions.
 * 
 * @author Leah Lackner
 */
public interface SpikeTrainCmpInterface {

	/**
	 * Interface method to rate a spike train.
	 * 
	 * @param comparator The comparator containing the spike trains
	 * 
	 * @return The rating
	 */
	double compare(SpikeTrainComparator comparator);
	
	/**
	 * Normalises the error value for the given comparison function.
	 * 
	 * @param value The value to be normalised.
	 * 
	 * @return The normalised value
	 */
	double normalize(double value);
}
