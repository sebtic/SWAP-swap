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
import java.awt.TrayIcon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;

import org.projectsforge.swap.core.environment.impl.EnvironmentImpl;
import org.projectsforge.swap.proxy.starter.BrowserPropertyHolder;
import org.projectsforge.swap.proxy.webui.WebUIPropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;

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

  private SystemTray systemTray;

  /**
   * Advanced configuration.
   */
  private void advancedConfiguration() {
    final List<String> args = new ArrayList<>();
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
   * Initializes the component.
   *
   * @throws AWTException
   *           the aWT exception
   */
  @PostConstruct
  public void init() throws AWTException {
    systemTray = SystemTray.get();
    if (systemTray == null) {
      logger.error("Unable to load SystemTray!");
      throw new IllegalStateException("Unable to load SystemTray!");
    }

    systemTray.setImage(MainUI.class.getResource("/graphics/swap/icon.gif"));
    systemTray.setStatus("Smart Web Accessibility Proxy");

    systemTray.getMenu().add(new MenuItem("Basic settings", e -> basicConfiguration()));
    systemTray.getMenu().add(new MenuItem("Advanced settings", e -> advancedConfiguration()));
    systemTray.getMenu().add(new MenuItem("Exit", e -> {
      environment.stop();
      System.exit(0);
    }));
  }
}
