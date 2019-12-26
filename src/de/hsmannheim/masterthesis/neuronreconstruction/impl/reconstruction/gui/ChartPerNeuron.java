/** ChartPerNeuron.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.model.Model;
import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.Individual;
import net.miginfocom.swing.MigLayout;

/**
 * Panel to visualise a single neuron reconstruction which is updated in each
 * time step.
 * 
 * @author Leah Lackner
 */
public class ChartPerNeuron extends JPanel {

	private static final long serialVersionUID = -4904939493490360556L;

	private JTextPane fitnessLabel;
	private JTextPane paramsLabel;
	private JButton showWeightsButton;
	private ShowWeightsView weightsView;

	private int neuronIdx;
	private double dt;
	private double[][] measuredV;
	private double[][] inputs;
	private int swinginIndex;

	private JFreeChart chart;
	private XYSeries seriesReconstructed;

	public ChartPerNeuron(int neuronIdx, int swinginIndex, double dt, double[][] inputs, double[][] measuredV) {
		this.dt = dt;
		this.neuronIdx = neuronIdx;
		this.swinginIndex = swinginIndex;
		this.inputs = inputs;
		this.measuredV = measuredV;
		this.weightsView = new ShowWeightsView();

		createChartPanel();
	}

	private void createChartPanel() {
		JFreeChart chart = createChart();
		JPanel chartPanel = new ChartPanel(chart);
		chartPanel.setLayout(new MigLayout("", "center", "top"));
		chartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		chartPanel.setPreferredSize(new Dimension(chartPanel.getPreferredSize().width, 300));

		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "center", "top"));

		add(chartPanel, "span");

		fitnessLabel = new JTextPane();
		fitnessLabel.setContentType("text/html");
		fitnessLabel.setEditable(false);
		this.add(fitnessLabel, "span");
		updateFitnessLabel(Double.MAX_VALUE);

		paramsLabel = new JTextPane();
		paramsLabel.setContentType("text/html");
		paramsLabel.setEditable(false);
		this.add(paramsLabel, "span");
		updateParamsLabel("");

		showWeightsButton = new JButton("Show weights");
		this.add(showWeightsButton, "span");

		showWeightsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!weightsView.isVisible()) {
					weightsView.setPreferredSize(new Dimension(600, 400));
					weightsView.pack();
					RefineryUtilities.centerFrameOnScreen(weightsView);
					weightsView.setVisible(true);
				}
			}
		});
	}

	private JFreeChart createChart() {
		XYSeries seriesMeasured = new XYSeries("measured");
		seriesReconstructed = new XYSeries("reconstructed");

		XYSeriesCollection collection = new XYSeriesCollection();
		collection.addSeries(seriesMeasured);
		collection.addSeries(seriesReconstructed);

		chart = ChartFactory.createXYLineChart("Neuron " + neuronIdx, "t", "v(t)", collection, PlotOrientation.VERTICAL,
				true, // include legend
				true, // tooltips
				false // urls
		);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.getDomainAxis().setRangeWithMargins(0, measuredV.length);
		plot.getDomainAxis().setAutoRange(false);
		plot.getRangeAxis().setRangeWithMargins(-80, 50);

		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesStroke(0, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
				new float[] { 6.0f, 6.0f }, 0.0f));
		renderer.setSeriesPaint(1, Color.blue);

		ValueMarker marker = new ValueMarker(swinginIndex);
		marker.setPaint(Color.BLACK);
		marker.setStroke(new BasicStroke(2));
		plot.addDomainMarker(marker);

		for (int i = 0; i < measuredV.length; i++) {
			seriesMeasured.add(i, measuredV[i][neuronIdx]);
		}

		return chart;
	}

	/**
	 * Updates the representation with the given individual data. The weights
	 * view will also be updated.
	 */
	public void updateIndividual(Individual individual) {
		seriesReconstructed.clear();

		updateFitnessLabel(individual.getFitness());
		weightsView.setWeights(individual.getModel().neuronIndex, individual.getModel().weights);
		updateParamsLabel(extractParams(individual));
		for (int i = 0; i < swinginIndex; i++) {
			seriesReconstructed.addOrUpdate(i, measuredV[i][neuronIdx]);
		}

		Model simulation = new Model(individual.getModel());
		for (int i = swinginIndex; i < inputs.length; i++) {
			double input = inputs[i][neuronIdx];
			simulation.calculateNetwork(dt, measuredV[i - 1], input);
			seriesReconstructed.add(i, simulation.v);
		}
	}

	private void updateFitnessLabel(double value) {
		fitnessLabel.setText("Fitness: " + value);
	}

	private String extractParams(Individual individual) {
		StringBuilder params = new StringBuilder();

		params.append("<html>");
		params.append("<b>a</b>&nbsp; = ").append(individual.getModel().a).append("<br />");
		params.append("<b>b</b>&nbsp;  = ").append(individual.getModel().b).append("<br />");
		params.append("<b>c</b>&nbsp;  = ").append(individual.getModel().c).append("<br />");
		params.append("<b>d</b>&nbsp;  = ").append(individual.getModel().d).append("<br />");
		params.append("<b>p1</b> = ").append(individual.getModel().p1).append("<br />");
		params.append("<b>p2</b> = ").append(individual.getModel().p2).append("<br />");
		params.append("<b>p3</b> = ").append(individual.getModel().p3).append("<br />");
		params.append("<b>p4</b> = ").append(individual.getModel().p4).append("<br />");
		params.append("<b>u</b>&nbsp;  = ").append(individual.getModel().u).append("<br />");
		params.append("<b>uS</b> = ").append(individual.getModel().startU);

		params.append("</html>");

		return params.toString();
	}

	private void updateParamsLabel(String value) {
		paramsLabel.setText(value);
	}

}
