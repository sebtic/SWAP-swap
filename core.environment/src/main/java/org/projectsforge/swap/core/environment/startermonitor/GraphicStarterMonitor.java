/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SWAP. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SWAP. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.core.environment.startermonitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.StarterMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * A {@link StarterMonitor} which display a graphical progression.
 * 
 * @author Sébastien Aupetit
 */
public class GraphicStarterMonitor implements StarterMonitor {

  /**
   * The dialog.
   * 
   * @author Sébastien Aupetit
   */
  class ProgressMonitor extends JDialog {

    /** The logger. */
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GraphicStarterMonitor.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1677575437730976955L;

    /** The progress bar. */
    private final JProgressBar progressBar;

    /** The label. */
    private final JLabel label;

    /**
     * Instantiates a new progress monitor.
     */
    public ProgressMonitor() {
      super();
      setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/graphics/swap/icon.gif")));

      setTitle(environment.getName() + " is loading");

      final JPanel contentPane = new JPanel();
      getContentPane().add(contentPane);
      contentPane.setLayout(new BorderLayout());
      contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

      final JLabel logo = new JLabel();
      logo.setIcon(new ImageIcon(getClass().getResource("/graphics/swap/logo.jpg")));
      contentPane.add(logo, BorderLayout.NORTH);

      label = new JLabel("Loading the application...");
      contentPane.add(label, BorderLayout.CENTER);
      final Font font = label.getFont();
      label.setFont(font.deriveFont((float) (font.getSize() * .8)));

      progressBar = new JProgressBar();
      contentPane.add(progressBar, BorderLayout.SOUTH);
      progressBar.setIndeterminate(false);
      progressBar.setMaximum(maxStep);

      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      setUndecorated(true);
      contentPane.setBackground(Color.WHITE);
      setBackground(Color.WHITE);

      pack();

      label.setMinimumSize(new Dimension(logo.getWidth(), label.getHeight()));
      label.setMaximumSize(label.getMinimumSize());

      setLocationRelativeTo(null);
      setVisible(true);
      setResizable(false);
    }

    /**
     * Sets the note.
     * 
     * @param step the step
     * @param message the new note
     */
    public void setNote(final int step, final String message) {
      label.setText(message);
      progressBar.setValue(step);

    }
  }

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(GraphicStarterMonitor.class);

  /** The progress monitor. */
  private ProgressMonitor progressMonitor;

  /** The environment. */
  private Environment environment;

  /** The max step. */
  private int maxStep;

  /** The count. */
  private int count;

  /** The log format. */
  private String logFormat;

  /** The started flag. */
  private boolean started = false;

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.StarterMonitor#done()
   */
  @Override
  public void done() {
    progressMonitor.setVisible(false);
    progressMonitor.dispose();
    progressMonitor = null;
    started = true;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.configurer.StarterMonitor#nextStep(java.lang
   * .String)
   */
  @Override
  public void nextStep(final String message) {
    if (!started) {
      count++;
      if (progressMonitor != null) {
        progressMonitor.setNote(count, message);
      }
      logger.info(logFormat, count, message);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.configurer.StarterMonitor#setEnvironment(org
   * .projectsforge.swap.core.configurer.Environment)
   */
  @Override
  public void setEnvironment(final Environment environment) {
    this.environment = environment;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.StarterMonitor#setMaxStep(int)
   */
  @Override
  public void setMaxStep(final int maxStep) {
    this.maxStep = maxStep;
    this.count = 0;
    progressMonitor = new ProgressMonitor();
    logFormat = "[{}/" + maxStep + "] {}";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.configurer.StarterMonitor#updateStatus(java
   * .lang.String)
   */
  @Override
  public void updateStatus(final String message) {
    if (!started) {
      if (progressMonitor != null) {
        progressMonitor.setNote(count, message);
      }
      logger.debug(logFormat, count, message);
    }
  }
}
