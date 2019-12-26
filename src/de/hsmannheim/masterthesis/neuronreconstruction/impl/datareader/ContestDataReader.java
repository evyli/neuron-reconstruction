/** ContestDataReader.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.datareader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;

/**
 * Helper to read the input files for the contest data.
 * 
 * @author Leah Lackner
 */
public class ContestDataReader {

	public final int lines;
	public double[][] data;

	/**
	 * Read multiple files simultaneously.
	 * 
	 * Reads the input and voltage values. The data has the same length as the
	 * shortest file.
	 * 
	 * @param files
	 *            The files to be read.
	 * 
	 * @throws IOException
	 */
	public ContestDataReader(String... files) throws IOException {
		int lines = Integer.MAX_VALUE;
		for (String s : files) {
			lines = Math.min(lines, countLines(s));
		}
		this.lines = lines;

		data = new double[files.length][lines];

		for (int i = 0; i < files.length; i++) {
			readLines(data[i], files[i]);
		}
	}

	/**
	 * @param filePath
	 *            The path of the file
	 * @return The number of lines for the file at the given path
	 * @throws IOException
	 */
	private static int countLines(String filePath) throws IOException {
		try (FileReader fileReader = new FileReader(new File(filePath));
				LineNumberReader lnr = new LineNumberReader(fileReader)) {
			while (lnr.skip(Long.MAX_VALUE) > 0) {
			}
			return (lnr.getLineNumber() + 1);

		}
	}

	/**
	 * Read a file line by line in an output array. Each line will be parsed as
	 * an double value.
	 * 
	 * @param array
	 *            The array to be filled. The size should match the number of
	 *            lines in the file. (Output parameter)
	 * @param filePath
	 *            The path of the file
	 * @throws IOException
	 */
	private static void readLines(double[] array, String filePath) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(new File(filePath).toPath())) {
			String line = null;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				if (i >= array.length)
					break;
				array[i++] = Double.parseDouble(line);
			}
		}
	}
}
