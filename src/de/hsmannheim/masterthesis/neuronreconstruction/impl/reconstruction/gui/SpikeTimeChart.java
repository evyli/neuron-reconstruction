/** SpikeTimeChart.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.spikecomparison.SpikeTrainComparator;
import net.miginfocom.swing.MigLayout;

/**
 * Panel for the visualisation of the full network spike time chart.
 * 
 * @author Leah Lackner
 */
public class SpikeTimeChart extends JPanel {

	private static final long serialVersionUID = -2728916139494147511L;

	public SpikeTimeChart(double dt, double[][] vsMeasured, double[][] vsReconstructed, int swinginIndex) {
		List<Map<Integer, Boolean>> measuredList = toSpikeMaps(vsMeasured, dt);
		List<Map<Integer, Boolean>> reconstructedList = toSpikeMaps(vsReconstructed, dt);

		XYSeries seriesMeasured = new XYSeries("measured");
		XYSeries seriesReconstructed = new XYSeries("reconstructed");

		for (int i = 0; i < vsMeasured.length; i++) {
			for (int j = 0; j < vsMeasured[0].length; j++) {
				Map<Integer, Boolean> map = measuredList.get(j);
				if (map.get(i)) {
					seriesMeasured.add(i, j);
				}
			}
		}
		for (int i = swinginIndex; i < vsMeasured.length; i++) {
			for (int j = 0; j < vsMeasured[0].length; j++) {
				Map<Integer, Boolean> map = reconstructedList.get(j);
				if (map.get(i)) {
					seriesReconstructed.add(i, j);
				}
			}
		}

		XYSeriesCollection collection = new XYSeriesCollection();
		collection.addSeries(seriesMeasured);
		collection.addSeries(seriesReconstructed);

		JFreeChart chart = ChartFactory.createScatterPlot("Spike time simulation", "t", "neurons", collection);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.getDomainAxis().setRangeWithMargins(0, vsMeasured.length);
		plot.getDomainAxis().setAutoRange(false);
		plot.getRangeAxis().setRangeWithMargins(-1, vsMeasured[0].length);

		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesStroke(0, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
				new float[] { 6.0f, 6.0f }, 0.0f));
		renderer.setSeriesPaint(1, Color.blue);

		ValueMarker marker = new ValueMarker(swinginIndex);
		marker.setPaint(Color.BLACK);
		marker.setStroke(new BasicStroke(2));
		plot.addDomainMarker(marker);

		ChartPanel chartPanel = new ChartPanel(chart);

		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "center", "top"));
		add(chartPanel);
	}

	private double[][] exchangeIndexOrder(double[][] vValues) {
		double[][] result = new double[vValues[0].length][vValues.length];
		for (int i = 0; i < vValues.length; i++) {
			for (int j = 0; j < vValues[i].length; j++) {
				result[j][i] = vValues[i][j];
			}
		}
		return result;
	}

	private List<Map<Integer, Boolean>> toSpikeMaps(double[][] vValues, double dt) {
		double[][] exchanged = exchangeIndexOrder(vValues);

		List<Map<Integer, Boolean>> resultList = new LinkedList<>();
		for (int n = 0; n < exchanged.length; n++) {
			Map<Integer, Boolean> spikeMap = new HashMap<>();
			resultList.add(spikeMap);

			putToSpikeMap(spikeMap, exchanged[n], dt);
		}
		return resultList;
	}

	private void putToSpikeMap(Map<Integer, Boolean> spikeMap, double[] ds, double dt) {
		int[] indices = SpikeTrainComparator.toSpikeIndices(ds, dt);
		Set<Integer> spikeSet = new HashSet<>();

		for (int i = 0; i < indices.length; i++) {
			spikeSet.add(indices[i]);
		}

		for (int i = 0; i < ds.length; i++) {
			spikeMap.put(i, spikeSet.contains(i));
		}
	}

}
