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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.JOptionPane;

import org.projectsforge.swap.core.environment.impl.EnvironmentImpl;
import org.projectsforge.swap.proxy.starter.BrowserPropertyHolder;
import org.projectsforge.swap.proxy.webui.WebUIPropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The bean that manages UI.
 * 
 * @author Sébastien Aupetit
 */
@Component("mainUI")
public class MainUI {

  /** The environment. */
  @Autowired
  private EnvironmentImpl environment;

  /** The logger. */
  private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MainUI.class);

  /** The tray icon. */
  private TrayIcon trayIcon;

  /**
   * Advanced configuration.
   */
  private void advancedConfiguration() {
    final List<String> args = new ArrayList<String>();
    final String url = WebUIPropertyHolder.hostname.get();
    args.add(BrowserPropertyHolder.browserCommand.get());
    args.addAll(Arrays.asList(BrowserPropertyHolder.browserAgrs.get().replaceAll("\\{url\\}", url).split(" ")));
    try {
      Runtime.getRuntime().exec(args.toArray(new String[0]));
    } catch (final IOException e) {

      JOptionPane.showMessageDialog(null,
          "Can not launch browser. Please specify a valid executable name with the parameter {url} for the URL.",
          "Browser can not be executed", JOptionPane.ERROR_MESSAGE | JOptionPane.CLOSED_OPTION);
    }
  }

  /**
   * Basic configuration.
   */
  private synchronized void basicConfiguration() {
    new BasicConfigurationDialog(environment).setVisible(true);
  }

  /**
   * Finalizes the component.
   */
  @PreDestroy
  public void finish() {
    if (SystemTray.isSupported()) {
      trayIcon.setPopupMenu(null);
      SystemTray.getSystemTray().remove(trayIcon);
    }
  }

  /**
   * Initializes the component.
   * 
   * @throws AWTException
   *           the aWT exception
   */
  @PostConstruct
  public void init() throws AWTException {
    if (SystemTray.isSupported()) {

      // setup systray icon
      Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/graphics/swap/icon.gif"));
      final Dimension dim = SystemTray.getSystemTray().getTrayIconSize();
      if ("linux".equalsIgnoreCase(System.getProperty("os.name"))) {
        image = image.getScaledInstance(dim.width - 2, dim.height - 2, Image.SCALE_SMOOTH);
      } else {
        image = image.getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH);
      }

      trayIcon = new TrayIcon(image, "Smart Web Accessibility Proxy");
      trayIcon.setImageAutoSize(false);

      final PopupMenu popup = new PopupMenu();

      final MenuItem basicConfigureItem = new MenuItem("Basic settings");
      basicConfigureItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          basicConfiguration();
        }
      });
      popup.add(basicConfigureItem);

      final MenuItem advancedConfigureItem = new MenuItem("Advanced settings");
      advancedConfigureItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          advancedConfiguration();
        }
      });
      popup.add(advancedConfigureItem);

      final MenuItem exitItem = new MenuItem("Exit");
      exitItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          environment.stop();
        }
      });
      popup.add(exitItem);
      trayIcon.setPopupMenu(popup);
      // need to be ad the end on mac os x
      SystemTray.getSystemTray().add(trayIcon);
    } else {
      logger.error("Can not initialize systay");
      throw new IllegalStateException("Can not initialize systray");
    }

  }
}
