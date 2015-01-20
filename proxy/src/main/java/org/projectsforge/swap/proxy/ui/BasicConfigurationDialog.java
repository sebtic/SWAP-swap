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
package org.projectsforge.swap.proxy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.proxy.proxy.ProxyPropertyHolder;
import org.projectsforge.swap.proxy.starter.BrowserPropertyHolder;
import org.projectsforge.swap.proxy.webui.WebUIPropertyHolder;

/**
 * The basic configuration dialog.
 */
public class BasicConfigurationDialog extends JDialog {

  /** The environment. */
  private Environment environment;

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The table. */
  private final JTable table = new JTable();

  /**
   * The Constructor.
   * 
   * @param environment the environment
   */
  @SuppressWarnings("serial")
  public BasicConfigurationDialog(final Environment environment) {
    super((JDialog) null, "Basic configuration settings", ModalityType.APPLICATION_MODAL);
    this.environment = environment;

    setLayout(new BorderLayout());

    setIconImage(Toolkit.getDefaultToolkit().getImage(
        getClass().getResource("/graphics/swap/icon.gif")));

    table.setModel(new DefaultTableModel(getProperties(), new String[] { "Property", "Value" }) {
      Class<?>[] columnTypes = new Class<?>[] { String.class, String.class };

      boolean[] columnEditables = new boolean[] { false, true };

      @Override
      public Class<?> getColumnClass(final int columnIndex) {
        return columnTypes[columnIndex];
      }

      @Override
      public boolean isCellEditable(final int row, final int column) {
        return columnEditables[column];
      }
    });
    add(table, BorderLayout.CENTER);

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    add(buttonPanel, BorderLayout.SOUTH);

    final JButton saveBtn = new JButton(
        "Save changes (changes are effective after restart of the proxy)");
    saveBtn.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        table.getCellEditor().stopCellEditing();
        saveProperties();
      }
    });
    buttonPanel.add(saveBtn);

    final JButton closeBtn = new JButton("Discard changes");
    closeBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        setVisible(false);
      }
    });
    buttonPanel.add(closeBtn, BorderLayout.SOUTH);

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Gets the properties.
   * 
   * @return the properties
   */
  private Object[][] getProperties() {
    return new Object[][] {
        { "HTTP proxy port", ProxyPropertyHolder.httpPort.get().toString() },
        { "HTTPS proxy port", ProxyPropertyHolder.httpSecurePort.get().toString() },
        { "Proxy listening host (modification not recommanded)",
            ProxyPropertyHolder.httpHost.get().toString() },
        { "Internal server URL", WebUIPropertyHolder.hostname.get().toString() },
        { "Browser command", BrowserPropertyHolder.browserCommand.get().toString() },
        { "Browser args ({url} will be replaced by the URL)",
            BrowserPropertyHolder.browserAgrs.get().toString() } };
  }

  /**
   * Save properties.
   */
  private void saveProperties() {
    ProxyPropertyHolder.httpPort.set(Integer.parseInt((String) table.getModel().getValueAt(0, 1)));
    ProxyPropertyHolder.httpSecurePort.set(Integer.parseInt((String) table.getModel().getValueAt(1,
        1)));
    ProxyPropertyHolder.httpHost.set((String) table.getModel().getValueAt(2, 1));
    WebUIPropertyHolder.hostname.set((String) table.getModel().getValueAt(3, 1));
    BrowserPropertyHolder.browserCommand.set((String) table.getModel().getValueAt(4, 1));
    BrowserPropertyHolder.browserAgrs.set((String) table.getModel().getValueAt(5, 1));
    environment.saveConfigurationProperties();
  }
}
