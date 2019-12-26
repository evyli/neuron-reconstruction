/** ShowWeightsView.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;

/**
 * Frame in which the reconstructed weights are printed inside of a text pane.
 * 
 * @author Leah Lackner
 */
public class ShowWeightsView extends JFrame {

	private static final long serialVersionUID = 821954200551073099L;

	private JTextPane weightsLabel;

	public ShowWeightsView() {
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "center", "top"));

		weightsLabel = new JTextPane();
		weightsLabel.setContentType("text/html");
		weightsLabel.setBackground(Color.WHITE);
		weightsLabel.setEditable(false);
		setWeights(0, null);

		JPanel scrollPaneContentPanel = new ScrollablePanel();
		scrollPaneContentPanel.setBackground(Color.WHITE);
		scrollPaneContentPanel.setLayout(new MigLayout("", "grow", "top"));
		JScrollPane scrollPane = new JScrollPane(scrollPaneContentPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		scrollPaneContentPanel.add(weightsLabel);

		this.add(scrollPane, "span");

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}

	public void setWeights(int neuronIndex, double[] weights) {
		setTitle("Individual " + neuronIndex + " - weights");
		StringBuffer text = new StringBuffer();
		text.append("<html>");
		text.append("<b>Individual " + neuronIndex + "</b><br />");
		text.append("<b>Weights:</b><br />");
		text.append("<br />");

		if (weights != null) {
			for (int i = 0; i < weights.length; i++) {
				if (i == neuronIndex)
					text.append("<b>");
				text.append("&nbsp; <i>" + i + "</i>&nbsp; - &nbsp; ");
				text.append(weights[i]);
				text.append("<br />");
				if (i == neuronIndex)
					text.append("</b>");
			}
		}
		text.append("</html>");
		weightsLabel.setText(text.toString());
	}
}
