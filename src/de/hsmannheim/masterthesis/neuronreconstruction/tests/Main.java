/** Main.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.tests;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronAllSame;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronBalanced;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronExtendedSet;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronMainSpikeFrequency;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronMainSpikeTime;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronMainVoltageOverall;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronMainVoltageSingle;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronOnlySpikeFrequency;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronOnlySpikeTime;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronOnlyVoltageOverall;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronOnlyVoltageSingle;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronSingleNeuronSetAllSame;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata.RealNeuronSingleNeuronSetBalanced;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronAllSame;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronAllSameShortRunning;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronAllSameShortRunning2;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronBalanced;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronLongRunning;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronMainSpikeFrequency;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronMainSpikeTime;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronMainVoltageOverall;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronMainVoltageSingle;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronOnlySpikeFrequency;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronOnlySpikeTime;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronOnlyVoltageOverall;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.OneNeuronOnlyVoltageSingle;
import de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata.TenNeuronsBalanced;

/**
 * As a main program this program can be used to access the different tests on
 * the commandline.
 * 
 * @author Leah Lackner
 */
public class Main {

	public static void main(String[] args) {
		System.out.println("Spiking Neuron Reconstruction");
		System.out.println("=============================");
		System.out.println();
		System.out.println("Please read the README.md file which was part of this project");
		System.out.println();
		System.out.println(
				"Before you can run any tests on measured data you have to configure the contestdata directories.");
		System.out.println("The generateddata tests should run without any further configuration.");

		new Main(args);
	}

	private List<String> categories;
	private List<Class<?>> classes;

	public Main(String[] args) {
		categories = new LinkedList<>();
		classes = new LinkedList<>();

		addTestClass("generatedData", OneNeuronAllSameShortRunning.class);
		addTestClass("generatedData", OneNeuronAllSameShortRunning2.class);

		addTestClass("generatedData", OneNeuronAllSame.class);
		addTestClass("generatedData", OneNeuronBalanced.class);

		addTestClass("generatedData", OneNeuronMainSpikeFrequency.class);
		addTestClass("generatedData", OneNeuronOnlySpikeFrequency.class);

		addTestClass("generatedData", OneNeuronMainSpikeTime.class);
		addTestClass("generatedData", OneNeuronOnlySpikeTime.class);

		addTestClass("generatedData", OneNeuronMainVoltageOverall.class);
		addTestClass("generatedData", OneNeuronOnlyVoltageOverall.class);

		addTestClass("generatedData", OneNeuronMainVoltageSingle.class);
		addTestClass("generatedData", OneNeuronOnlyVoltageSingle.class);

		addTestClass("generatedData", TenNeuronsBalanced.class);

		addTestClass("generatedData", OneNeuronLongRunning.class);

		addTestClass("contestdata", RealNeuronAllSame.class);
		addTestClass("contestdata", RealNeuronBalanced.class);

		addTestClass("contestdata", RealNeuronMainSpikeFrequency.class);
		addTestClass("contestdata", RealNeuronOnlySpikeFrequency.class);

		addTestClass("contestdata", RealNeuronMainSpikeTime.class);
		addTestClass("contestdata", RealNeuronOnlySpikeTime.class);

		addTestClass("contestdata", RealNeuronMainVoltageOverall.class);
		addTestClass("contestdata", RealNeuronOnlyVoltageOverall.class);

		addTestClass("contestdata", RealNeuronMainVoltageSingle.class);
		addTestClass("contestdata", RealNeuronOnlyVoltageSingle.class);

		addTestClass("contestdata", RealNeuronExtendedSet.class);

		addTestClass("contestdata", RealNeuronSingleNeuronSetAllSame.class);
		addTestClass("contestdata", RealNeuronSingleNeuronSetBalanced.class);

		System.out.println();
		if (args != null && args.length == 1) {
			boolean testResult = runTest(Integer.parseInt(args[0]) - 1);
			if (testResult)
				return;
		}

		Scanner in = null;
		try {
			in = new Scanner(System.in);

			while (true) {
				printPrompt();
				String line = in.nextLine();
				int input = 0;
				try {
					input = Integer.parseInt(line);
				} catch (NumberFormatException e) {
					continue;
				}
				if (input == 0) {
					System.exit(0);
				}
				input--;
				boolean result = runTest(input);
				if (!result)
					continue;
				return;
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private boolean runTest(int i) {
		if (i >= classes.size() || i < 0) {
			System.err.println("Invalid input number");
			return false;
		}
		System.out.println("Run test: " + classes.get(i));
		System.out.println();
		try {
			classes.get(i).getMethod("main", String[].class).invoke(null, (Object) new String[] {});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void addTestClass(String category, Class<?> clazz) {
		categories.add(category);
		classes.add(clazz);
	}

	private void printPrompt() {
		System.out.println("---------");
		for (int i = 0; i < classes.size(); i++) {
			System.out.println((i + 1) + ") [" + categories.get(i) + "] " + classes.get(i).getSimpleName());
		}
		System.out.println("---------");
		System.out.println("0) quit");
		System.out.println();
		System.out.print("> ");
	}

}
