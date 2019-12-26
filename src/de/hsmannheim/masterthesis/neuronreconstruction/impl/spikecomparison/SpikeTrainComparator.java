/** SpikeTrainComparator.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.model.ValueBorders;

/**
 * Preparation and execution of the spike comparison. Calls other Measure
 * classes for the calculation of the error.
 * 
 * @author Leah Lackner
 */
public class SpikeTrainComparator {

	/**
	 * Compares two spike trains.
	 * 
	 * @param targetSpikeTrain
	 *            the real measured spike train
	 * @param modelSpikeTrain
	 *            the simulated spike train, simulated in one run
	 * @param modelSpikeTrainSingleStepErrors
	 *            the simulated spike train, simulated step by step with the
	 *            real measured value as given value. In contrast to the
	 *            modelSpikeTrain parameter, only the error for one step is
	 *            calculated in this spike train.
	 * @param dt
	 *            the dt value of the model function and the spike train. 0.1
	 *            means that the values are measured in an interval of 0.1
	 *            seconds between each step
	 * 
	 * @return a double in the range from 0 to 1.0. A value nearer to zero means
	 *         that the spike trains are closer together. 1.0 is the worst
	 *         result.
	 */
	public static double compareSpikeTrains(double[] targetSpikeTrain, double[] modelSpikeTrain,
			double[] modelSpikeTrainSingleStepErrors, double dt, Map<Class<?>, Double> measures) {

		return new SpikeTrainComparator(targetSpikeTrain, modelSpikeTrain, modelSpikeTrainSingleStepErrors, dt)
				.getComparisonValue(measures);
	}

	double[] targetSpikeTrain;
	double[] modelSpikeTrain;
	double[] modelSpikeTrainSingleStepErrors;
	int[] targetSpikeTrainIndices;
	int[] modelSpikeTrainIndices;
	int length;
	double dt;

	private SpikeTrainComparator(double[] targetSpikeTrain, double[] modelSpikeTrain,
			double[] modelSpikeTrainSingleStepErrors, double dt) {
		this.targetSpikeTrain = targetSpikeTrain;
		this.modelSpikeTrain = modelSpikeTrain;
		this.modelSpikeTrainSingleStepErrors = modelSpikeTrainSingleStepErrors;
		this.length = Math.min(Math.min(targetSpikeTrain.length, modelSpikeTrain.length),
				modelSpikeTrainSingleStepErrors.length);
		this.dt = dt;

		this.targetSpikeTrainIndices = toSpikeIndices(targetSpikeTrain, dt);
		this.modelSpikeTrainIndices = toSpikeIndices(modelSpikeTrain, dt);
	}

	private double getComparisonValue(Map<Class<?>, Double> measures) {
		if (!Double.isFinite(new ValidityChecker().compare(this))) {
			return Double.POSITIVE_INFINITY;
		}

		Class<?>[] classArray = new Class<?>[measures.size()];
		Double[] weightArray = new Double[measures.size()];

		int i = 0;
		for (Map.Entry<Class<?>, Double> entry : measures.entrySet()) {
			classArray[i] = entry.getKey();
			weightArray[i] = entry.getValue();
			i++;
		}

		List<Double> measureValues = handleMeasures(classArray);

		double newresult = sumMeasures(classArray, measureValues, weightArray);

		return newresult;
	}

	private List<Double> handleMeasures(Class<?>[] measures) {
		List<Double> results = new LinkedList<>();
		try {
			List<SpikeTrainCmpInterface> measureInstances = new LinkedList<>();
			for (Class<?> measure : measures) {
				@SuppressWarnings("unchecked")
				Constructor<? extends SpikeTrainCmpInterface> constructor = ((Class<? extends SpikeTrainCmpInterface>) measure)
						.getConstructor();
				measureInstances.add(constructor.newInstance());
			}
			for (int i = 0; i < measureInstances.size(); i++) {
				SpikeTrainCmpInterface instance = measureInstances.get(i);
				double value = instance.compare(this);
				double normed = instance.normalize(value);
				System.out.println(instance.getClass().getSimpleName() + ": " + normed + " (original=" + value + ")");
				results.add(normed);
			}

		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException
				| IllegalArgumentException e) {
			// It is a programming error if a reflection exception is thrown
			// here
			throw new RuntimeException(e);
		}
		return results;
	}

	private double sumMeasures(Class<?>[] classArray, List<Double> measured, Double[] manualWeights) {
		boolean isManual = false;
		for (Double d : manualWeights) {
			// when one weight is not equal to 1, the automatic weight
			// adaptation is disabled
			if (d != 1.0)
				isManual = true;
		}

		double weightsSum = 0;
		double errorSum = 0;

		if (isManual) {
			System.out.println("Manual weights:");
			for (int i = 0; i < measured.size(); i++) {
				errorSum += measured.get(i) * manualWeights[i];
				weightsSum += manualWeights[i];
			}

		} else {
			System.out.println("Weight adaptation:");
			Collections.sort(measured, new Comparator<Double>() {

				@Override
				public int compare(Double o1, Double o2) {
					return o1.compareTo(o2);
				}
			});
			System.out.println(measured);

			double weight = 2;
			for (int i = 0; i < measured.size(); i++) {
				double measure = measured.get(i);
				weight = Math.pow(2, i);

				errorSum += measure * weight;
				weightsSum += weight;
				System.out.println(measure + " => " + (measure * weight));
			}
		}
		double normedError = errorSum / weightsSum;
		System.out.println("TotalError: " + normedError + " (original=" + errorSum + ")");
		return normedError;
	}

	/**
	 * Convert the array of membrane potentials to an array of spike indices.
	 */
	public static int[] toSpikeIndices(double[] vValues, double dt) {
		List<Integer> indices = new LinkedList<>();
		int i = 0;

		while (i < vValues.length) {
			if (vValues[i] >= ValueBorders.SPIKE_RECOGNITION) {
				double lastVal = vValues[i];
				i++;

				while (i < vValues.length) {
					if (vValues[i] < lastVal) {
						indices.add(i - 1);
						break;
					}
					lastVal = vValues[i];
					i++;
				}

				while (i < vValues.length) {
					if (vValues[i] < ValueBorders.SPIKE_RECOGNITION) {
						break;
					}
					i++;
				}
			} else {
				i++;
			}
		}
		int[] indicesArray = new int[indices.size()];
		for (i = 0; i < indices.size(); i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	static double doNormalize(double x, double min, double max) {
		x = Math.min(max, x);
		x = Math.max(min, x);
		x = (x - min) / (double) (max - min);
		if (x == 0)
			x = 0.00000001;
		return x;
	}

}
