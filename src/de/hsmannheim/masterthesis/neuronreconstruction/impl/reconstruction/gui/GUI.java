/** GUI.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.jfree.ui.RefineryUtilities;

import de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.Individual;
import net.miginfocom.swing.MigLayout;

/**
 * The main implementation of the graphical user interface. The GUI is only
 * meant as a rough prototype for a few visualisations of the membrane potential
 * and the reconstruction.
 * 
 * @author Leah Lackner
 */
public class GUI extends JFrame {

	private static final long serialVersionUID = 2620812917599983552L;

	private static final String TITLE = "Spiking Neuron Reconstruction";

	private JLabel labelDatasetName;
	private JLabel labelOverallTime;
	private JLabel labelAverageTimePerNeuron;
	private JLabel labelAverageTimePerGeneration;
	private JLabel labelBestFitnessCurrentInd;
	private JLabel labelCurrentNeuron;
	private JLabel labelCurrentGeneration;

	private JLabel labelTimesteps;
	private JLabel labelDt;
	private JLabel labelPopulationSize;
	private JLabel labelFitnessThreshold;
	private JLabel labelGenerationThreshold;
	private JLabel labelAbortAfter;
	private JLabel labelMutationRate;
	private JLabel labelIndexSwingin;

	private JProgressBar progressbarAll;
	private JProgressBar progressbarCurrentNeuron;
	private JProgressBar progressbarCurrentNeuronCurrentGeneration;

	private Map<Integer, ChartPerNeuron> chartsMap;

	private int timesteps;
	private int swinginIndex;
	private int populationsize;

	private JPanel neuronPanel;
	private JPanel currentRow;

	private JPanel spikeTimeGraph;

	private long timePerNeuron;
	private long timePerGeneration;

	private long numberGenerations;

	private long startTime;

	private double[][] measuredVs;
	private double[][] inputs;
	private double dt;

	public GUI(String datasetName, double[][] inputs, double[][] vs, double dt, int inputIdxDelimiter,
			int generationThreshold, double fitnessThreshold, int populationSize, int abortAfterGenerations,
			double mutationRate) {
		super(TITLE);
		chartsMap = new HashMap<>();

		setUIFont(new javax.swing.plaf.FontUIResource("Monospace", Font.PLAIN, 12));
		JPanel panel = new JPanel();
		setContentPane(panel);

		panel.setLayout(new MigLayout("", "center", "top"));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setBackground(new Color(224, 224, 224));
		panel.setLocale(Locale.UK);

		String headerBefore = "<html><h2>";
		String headerAfter = "</h2></html>";
		String labelBefore = "<html><font size=\"4\"><b>";
		String labelAfter = "</b></font></html>";

		String columnWidth = "width 500:500:500";

		JLabel label = new JLabel(headerBefore + TITLE + headerAfter);
		panel.add(label, "span");
		panel.add(createGap(10), "span");

		JLabel label40 = new JLabel(labelBefore + "Dataset name:" + labelAfter);
		panel.add(label40, columnWidth);
		labelDatasetName = new JLabel();
		panel.add(labelDatasetName, "wrap");

		panel.add(createSeparator(), "span");
		panel.add(createGap(10), "span");

		JLabel label4 = new JLabel(labelBefore + "Overall Time:" + labelAfter);
		panel.add(label4, columnWidth);
		labelOverallTime = new JLabel();
		panel.add(labelOverallTime, "wrap");

		JLabel label190 = new JLabel(labelBefore + "Average time per neuron:" + labelAfter);
		panel.add(label190, columnWidth);
		labelAverageTimePerNeuron = new JLabel();
		panel.add(labelAverageTimePerNeuron, "wrap");

		JLabel label290 = new JLabel(labelBefore + "Average time per generation:" + labelAfter);
		panel.add(label290, columnWidth);
		labelAverageTimePerGeneration = new JLabel();
		panel.add(labelAverageTimePerGeneration, "wrap");

		JLabel label200 = new JLabel(labelBefore + "Current neuron:" + labelAfter);
		panel.add(label200, columnWidth);
		labelCurrentNeuron = new JLabel();
		panel.add(labelCurrentNeuron, "wrap");

		JLabel label300 = new JLabel(labelBefore + "Current generation:" + labelAfter);
		panel.add(label300, columnWidth);
		labelCurrentGeneration = new JLabel();
		panel.add(labelCurrentGeneration, "wrap");

		JLabel label192 = new JLabel(labelBefore + "Fitness current neuron:" + labelAfter);
		panel.add(label192, columnWidth);
		labelBestFitnessCurrentInd = new JLabel();
		panel.add(labelBestFitnessCurrentInd, "wrap");

		JLabel label7 = new JLabel(labelBefore + "All neurons progress:" + labelAfter);
		panel.add(label7, "span");
		progressbarAll = new JProgressBar(0, 100);
		progressbarAll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
		progressbarAll.setStringPainted(true);
		panel.add(progressbarAll, "span");

		panel.add(createGap(10), "span");
		JLabel label90 = new JLabel(labelBefore + "Generation progress for current neuron:" + labelAfter);
		panel.add(label90, "span");
		progressbarCurrentNeuron = new JProgressBar(0, 100);
		progressbarCurrentNeuron.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
		progressbarCurrentNeuron.setStringPainted(true);
		panel.add(progressbarCurrentNeuron, "span");

		panel.add(createGap(10), "span");
		JLabel label8 = new JLabel(labelBefore + "Estimated progress in current generation:" + labelAfter);
		panel.add(label8, "span");
		progressbarCurrentNeuronCurrentGeneration = new JProgressBar(0, 100);
		progressbarCurrentNeuronCurrentGeneration.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
		progressbarCurrentNeuronCurrentGeneration.setStringPainted(true);
		panel.add(progressbarCurrentNeuronCurrentGeneration, "span");

		panel.add(createGap(70), "span");

		JPanel scrollPaneContentPanel = new ScrollablePanel();
		scrollPaneContentPanel.setBackground(Color.WHITE);
		scrollPaneContentPanel.setLayout(new MigLayout("", "grow", "top"));
		JScrollPane scrollPane = new JScrollPane(scrollPaneContentPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JLabel label20 = new JLabel(labelBefore + "Timesteps:" + labelAfter);
		scrollPaneContentPanel.add(label20, columnWidth);
		labelTimesteps = new JLabel();
		scrollPaneContentPanel.add(labelTimesteps, "wrap");

		JLabel label21 = new JLabel(labelBefore + "dt:" + labelAfter);
		scrollPaneContentPanel.add(label21, columnWidth);
		labelDt = new JLabel();
		scrollPaneContentPanel.add(labelDt, "wrap");

		JLabel label23 = new JLabel(labelBefore + "Population size:" + labelAfter);
		scrollPaneContentPanel.add(label23, columnWidth);
		labelPopulationSize = new JLabel();
		scrollPaneContentPanel.add(labelPopulationSize, "wrap");

		JLabel label24 = new JLabel(labelBefore + "Fitness threshold:" + labelAfter);
		scrollPaneContentPanel.add(label24, columnWidth);
		labelFitnessThreshold = new JLabel();
		scrollPaneContentPanel.add(labelFitnessThreshold, "wrap");

		JLabel label25 = new JLabel(labelBefore + "Generation threshold:" + labelAfter);
		scrollPaneContentPanel.add(label25, columnWidth);
		labelGenerationThreshold = new JLabel();
		scrollPaneContentPanel.add(labelGenerationThreshold, "wrap");

		JLabel label26 = new JLabel(labelBefore + "Abort after generations without change:" + labelAfter);
		scrollPaneContentPanel.add(label26, columnWidth);
		labelAbortAfter = new JLabel();
		scrollPaneContentPanel.add(labelAbortAfter, "wrap");

		JLabel label27 = new JLabel(labelBefore + "Mutation rate:" + labelAfter);
		scrollPaneContentPanel.add(label27, columnWidth);
		labelMutationRate = new JLabel();
		scrollPaneContentPanel.add(labelMutationRate, "wrap");

		JLabel label28 = new JLabel(labelBefore + "Index after parameter swing in:" + labelAfter);
		scrollPaneContentPanel.add(label28, columnWidth);
		labelIndexSwingin = new JLabel();
		scrollPaneContentPanel.add(labelIndexSwingin, "wrap");

		scrollPaneContentPanel.add(createGap(10), "span");
		scrollPaneContentPanel.add(createSeparator(), "span");

		neuronPanel = new JPanel();
		neuronPanel.setBackground(Color.WHITE);
		neuronPanel.setLayout(new MigLayout("", "grow", "top"));

		scrollPaneContentPanel.add(neuronPanel, "span");

		spikeTimeGraph = new JPanel();
		spikeTimeGraph.setBackground(Color.WHITE);
		spikeTimeGraph.setLayout(new MigLayout("", "grow", "top"));
		scrollPaneContentPanel.add(spikeTimeGraph, "span");

		panel.add(scrollPane, "span");
		panel.add(createGap(10), "span");

		JButton button = new JButton(labelBefore + "Abort and exit" + labelAfter);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel.add(button, "span");

		setPreferredSize(new Dimension(1000, 800));
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		init(datasetName, inputs, vs, dt, inputIdxDelimiter, generationThreshold, fitnessThreshold, populationSize,
				abortAfterGenerations, mutationRate);
	}

	private void init(String datasetName, double[][] inputs, double[][] vs, double dt, int inputIdxDelimiter,
			int generationThreshold, double fitnessThreshold, int populationSize, int abortAfterGenerations,
			double mutationRate) {
		timesteps = inputs.length;
		swinginIndex = inputIdxDelimiter;
		populationsize = populationSize;

		setLabelOverallTime(0 + "");
		setLabelBestFitnessCurrentInd("None");
		setLabelAverageTimePerGeneration("Unknown");
		setLabelAverageTimePerNeuron("Unknown");
		setLabelCurrentNeuron("" + 0);
		setLabelCurrentGeneration("" + 0);

		setLabelAbortAfter(abortAfterGenerations + "");
		setLabelDatasetName(datasetName);
		setLabelDt("" + dt);
		setLabelFitnessThreshold(fitnessThreshold + "");
		setLabelGenerationThreshold(generationThreshold + "");
		setLabelIndexSwingin(inputIdxDelimiter + "");
		setLabelMutationRate(mutationRate + "");

		setLabelPopulationSize(populationSize + "");

		setLabelTimesteps(timesteps + "");

		progressbarAll.setMaximum(inputs[0].length);
		progressbarCurrentNeuron.setMaximum(generationThreshold);
		progressbarCurrentNeuronCurrentGeneration.setMaximum(populationSize);

		progressbarAll.setValue(0);
		progressbarCurrentNeuron.setValue(0);
		progressbarCurrentNeuronCurrentGeneration.setValue(0);

		startTime = System.nanoTime();

		measuredVs = vs;
		this.inputs = inputs;
		this.dt = dt;
	}

	public void actionUpdateOverall(int num) {
		progressbarAll.setValue(num);
		setLabelCurrentGeneration("" + 0 + "/" + labelGenerationThreshold.getText().replace("gray", "black"));
		setLabelBestFitnessCurrentInd("-");
		timePerNeuron = System.nanoTime() - startTime;
	}

	public void actionUpdatePerGenerationCount(int num) {
		if (num == populationsize) {
			timePerGeneration = System.nanoTime() - startTime;
			numberGenerations++;
			setLabelAverageTimePerGeneration(toTime(timePerGeneration / numberGenerations));
		}
		progressbarCurrentNeuronCurrentGeneration.setValue(num);

		setLabelOverallTime(toTime(System.nanoTime() - startTime));
	}

	public void actionUpdatePerNeuron(int neuronIdx, Individual individual, int generation) {
		actionUpdatePerNeuronIntern(neuronIdx, individual, generation);
	}

	public void actionUpdatePerNeuronAfter(int neuronIdx, Individual individual, int generation) {
		actionUpdatePerNeuronIntern(neuronIdx, individual, generation);
		ChartPerNeuron chartPerNeuron = getChartPerNeuron(neuronIdx);
		chartPerNeuron.updateIndividual(individual);
	}

	public void actionReconstructionIsDone(double[][] vsMeasured, double[][] vsReconstructed) {
		setLabelCurrentNeuron("" + progressbarAll.getMaximum() + "/" + progressbarAll.getMaximum());
		progressbarAll.setValue(progressbarAll.getMaximum());
		progressbarCurrentNeuron.setValue(progressbarCurrentNeuron.getMaximum());
		progressbarCurrentNeuronCurrentGeneration.setValue(progressbarCurrentNeuronCurrentGeneration.getMaximum());

		drawSpikeTimeGraphic(vsMeasured, vsReconstructed);

		JOptionPane.showMessageDialog(this, "The Reconstruction has finished.", "Reconstruction succeed",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void drawSpikeTimeGraphic(double[][] vsMeasured, double[][] vsReconstructed) {
		spikeTimeGraph.add(new SpikeTimeChart(dt, vsMeasured, vsReconstructed, swinginIndex));
	}

	private void actionUpdatePerNeuronIntern(int neuronIdx, Individual individual, int generation) {
		progressbarCurrentNeuron.setValue(generation);

		if (individual.getFitness() != 0.0)
			setLabelBestFitnessCurrentInd(individual.getFitness() + "");

		setLabelCurrentNeuron("" + neuronIdx + "/" + progressbarAll.getMaximum());
		progressbarAll.setValue(neuronIdx);
		if (neuronIdx > 0)
			setLabelAverageTimePerNeuron(toTime(timePerNeuron / neuronIdx));
		setLabelCurrentGeneration("" + generation + "/" + labelGenerationThreshold.getText().replace("gray", "black"));
		progressbarCurrentNeuron.setValue(generation);
		getChartPerNeuron(neuronIdx);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
	}

	private String toTime(long ns) {
		return timeConversion(ns / 1000000000);
	}

	private String timeConversion(long totalSeconds) {

		final int MINUTES_IN_AN_HOUR = 60;
		final int SECONDS_IN_A_MINUTE = 60;

		long seconds = totalSeconds % SECONDS_IN_A_MINUTE;
		long totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
		long minutes = totalMinutes % MINUTES_IN_AN_HOUR;
		long hours = totalMinutes / MINUTES_IN_AN_HOUR;

		return hours + ":" + minutes + ":" + seconds;
	}

	private ChartPerNeuron getChartPerNeuron(int neuronIdx) {
		ChartPerNeuron chart = chartsMap.get(neuronIdx);
		if (chart == null) {
			chart = new ChartPerNeuron(neuronIdx, swinginIndex, dt, inputs, measuredVs);
			chartsMap.put(neuronIdx, chart);
			addChartToGUI(chart);
		}
		return chart;
	}

	private void addChartToGUI(ChartPerNeuron chart) {
		if (currentRow == null) {
			currentRow = new JPanel();
			currentRow.setBackground(Color.WHITE);
			currentRow.setLayout(new MigLayout("", "center", "top"));
			if (neuronPanel.getComponents().length != 0)
				neuronPanel.add(createSeparator(), "span");
			neuronPanel.add(currentRow, "span");

			currentRow.add(chart);
		} else {
			currentRow.add(createVerticalSeparator(), "center");
			currentRow.add(chart, "wrap");
			currentRow = null;
		}
	}

	private String value(String content) {
		String valueBefore = "<html><font size=\"3\" color=\"gray\">";
		String valueAfter = "</font></html>";

		return valueBefore + content + valueAfter;
	}

	private String valueChanging(String content) {
		String valueChangingBefore = "<html><font size=\"3\" color=\"black\">";
		String valueChangingAfter = "</font></html>";

		return valueChangingBefore + content + valueChangingAfter;
	}

	private void setLabelDatasetName(String labelDatasetName) {
		this.labelDatasetName.setText(value(labelDatasetName));
	}

	private void setLabelOverallTime(String labelOverallTime) {
		this.labelOverallTime.setText(valueChanging(labelOverallTime));
	}

	private void setLabelCurrentNeuron(String labelCurrentNeuron) {
		this.labelCurrentNeuron.setText(valueChanging(labelCurrentNeuron));
	}

	private void setLabelCurrentGeneration(String labelCurrentGeneration) {
		this.labelCurrentGeneration.setText(valueChanging(labelCurrentGeneration));
	}

	private void setLabelAverageTimePerNeuron(String labelAverageTimePerNeuron) {
		this.labelAverageTimePerNeuron.setText(valueChanging(labelAverageTimePerNeuron));
	}

	private void setLabelAverageTimePerGeneration(String labelAverageTimePerGeneration) {
		this.labelAverageTimePerGeneration.setText(valueChanging(labelAverageTimePerGeneration));
	}

	private void setLabelBestFitnessCurrentInd(String labelBestFitnessCurrentInd) {
		this.labelBestFitnessCurrentInd.setText(valueChanging(labelBestFitnessCurrentInd));
	}

	private void setLabelTimesteps(String labelTimesteps) {
		this.labelTimesteps.setText(value(labelTimesteps));
	}

	private void setLabelDt(String labelDt) {
		this.labelDt.setText(value(labelDt));
	}

	private void setLabelPopulationSize(String labelPopulationSize) {
		this.labelPopulationSize.setText(value(labelPopulationSize));
	}

	private void setLabelFitnessThreshold(String labelFitnessThreshold) {
		this.labelFitnessThreshold.setText(value(labelFitnessThreshold));
	}

	private void setLabelGenerationThreshold(String labelGenerationThreshold) {
		this.labelGenerationThreshold.setText(value(labelGenerationThreshold));
	}

	private void setLabelAbortAfter(String labelAbortAfter) {
		this.labelAbortAfter.setText(value(labelAbortAfter));
	}

	private void setLabelMutationRate(String labelMutationRate) {
		this.labelMutationRate.setText(value(labelMutationRate));
	}

	private void setLabelIndexSwingin(String labelIndexSwingin) {
		this.labelIndexSwingin.setText(value(labelIndexSwingin));
	}

	private void setUIFont(javax.swing.plaf.FontUIResource f) {
		try {
			Enumeration<Object> keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value != null && value instanceof javax.swing.plaf.FontUIResource)
					UIManager.put(key, f);
			}
		} catch (Exception e) {
		}
	}

	private JSeparator createSeparator() {
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setPreferredSize(new Dimension(Integer.MAX_VALUE, 5));
		return separator;
	}

	private JSeparator createVerticalSeparator() {
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(5, 300));
		return separator;
	}

	private JComponent createGap(int height) {
		JLabel label = new JLabel("");
		label.setPreferredSize(new Dimension(Integer.MAX_VALUE, height));
		return label;
	}
}
