/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SWAP.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SWAP. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.mime.css.resolver.color.model;

import java.util.ArrayList;
import org.projectsforge.swap.core.mime.css.resolver.Properties;
import org.projectsforge.swap.core.mime.css.resolver.PropertyResolver;
import org.projectsforge.swap.core.mime.css.resolver.State;
import org.projectsforge.swap.core.mime.css.resolver.color.SRGBColor;
import org.projectsforge.swap.core.mime.css.resolver.ruleset.CssRule;
import org.projectsforge.swap.core.mime.html.nodes.Text;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.BODYElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ColorRecorder.
 */
public class ColorRecorder implements PropertyResolver.Recorder {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(ColorRecorder.class);

  /** The color dictionary. */
  private final SimpleColorDictionary colorDictionary;

  /** The fg bg registry. */
  private final FgBgRegistry fgBgRegistry;

  /**
   * The Constructor.
   * 
   * @param colorDictionary
   *          the color dictionary
   * @param fgBgRegistry
   *          the fg bg registry
   */
  public ColorRecorder(final SimpleColorDictionary colorDictionary, final FgBgRegistry fgBgRegistry) {
    this.colorDictionary = colorDictionary;
    this.fgBgRegistry = fgBgRegistry;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.PropertyResolver.Recorder
   * #afterElement(java.util.ArrayList, java.util.ArrayList,
   * java.util.ArrayList)
   */
  @Override
  public void afterElement(final ArrayList<AbstractElement> elements,
      final ArrayList<State> states, final ArrayList<Properties> properties) {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.PropertyResolver.Recorder
   * #beforeElement(java.util.ArrayList, java.util.ArrayList,
   * java.util.ArrayList)
   */
  @Override
  public void beforeElement(final ArrayList<AbstractElement> elements,
      final ArrayList<State> states, final ArrayList<Properties> properties) {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.PropertyResolver.Recorder
   * #element(java.util.ArrayList, java.util.ArrayList, java.util.ArrayList)
   */
  @Override
  public void element(final ArrayList<AbstractElement> elements, final ArrayList<State> states,
      final ArrayList<Properties> properties) {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.PropertyResolver.Recorder
   * #text(java.util.ArrayList, java.util.ArrayList, java.util.ArrayList,
   * org.projectsforge.swap.core.mime.html.nodes.Text)
   */
  @Override
  public void text(final ArrayList<AbstractElement> elements, final ArrayList<State> states,
      final ArrayList<Properties> properties, final Text text) {

    // are we in the body tag ?
    boolean inBody = false;
    for (final AbstractElement element : elements) {
      if (element instanceof BODYElement) {
        inBody = true;
      }
    }

    if (!inBody) {
      return;
    }

    final String content = text.getCleanedContent();

    if (content.isEmpty()) {
      return;
    }

    final Properties prop = properties.get(properties.size() - 1);

    final SRGBColor foregroundColor = prop.get(Properties.CSS_COLOR, SRGBColor.class);
    final SRGBColor backgroundColor = prop.get(Properties.CSS_BACKGROUNDCOLOR, SRGBColor.class);

    final CssRule foregroundRule = prop.get(Properties.CSS_RULE_COLOR, CssRule.class);
    final CssRule backgroundRule = prop.get(Properties.CSS_RULE_BACKGROUNDCOLOR, CssRule.class);

    // final Font font = prop.get(Properties.CSS_FONT, Font.class);
    // TODO manage font size

    try {
      fgBgRegistry.register(colorDictionary.getForeground(foregroundColor, foregroundRule),
          colorDictionary.getBackground(backgroundColor, backgroundRule), content.length());
    } catch (final IllegalArgumentException e) {
      logger.debug("text: {}, elements: {}, states: {}, properties: {}, exception: e",
          new Object[] { text, elements, states, properties, e });
    }
  }
}
