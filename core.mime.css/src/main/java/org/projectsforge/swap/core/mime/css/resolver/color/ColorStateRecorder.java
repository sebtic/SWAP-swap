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
package org.projectsforge.swap.core.mime.css.resolver.color;

import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;
import org.projectsforge.swap.core.mime.css.property.color.dictionary.FgBgRegistry;
import org.projectsforge.swap.core.mime.css.property.color.dictionary.SimpleColorDictionary;
import org.projectsforge.swap.core.mime.css.resolver.Properties;
import org.projectsforge.swap.core.mime.css.resolver.ResolverState;
import org.projectsforge.swap.core.mime.css.resolver.StateRecorder;
import org.projectsforge.swap.core.mime.html.nodes.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ColorRecorder.
 * 
 * @param <TColor>
 *          the generic type
 * @author Sébastien Aupetit
 */
public class ColorStateRecorder<TColor extends SRGBColor> implements StateRecorder {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(ColorStateRecorder.class);

  /** The color dictionary. */
  private final SimpleColorDictionary<TColor> colorDictionary;

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
  public ColorStateRecorder(final SimpleColorDictionary<TColor> colorDictionary, final FgBgRegistry fgBgRegistry) {
    this.colorDictionary = colorDictionary;
    this.fgBgRegistry = fgBgRegistry;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.StateRecorder#element(org
   * .projectsforge.swap.core.mime.css.resolver.ResolverState)
   */
  @Override
  public void element(final ResolverState resolverState) {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.projectsforge.swap.core.mime.css.resolver.StateRecorder#text(org.
   * projectsforge.swap.core.mime.css.resolver.ResolverState,
   * org.projectsforge.swap.core.mime.html.nodes.Text)
   */
  @Override
  public void text(final ResolverState parentResolverState, final Text text) {
    // are we in the body tag ?
    if (!parentResolverState.isInBodyTagBranch()) {
      return;
    }

    final String content = text.getCleanedContent();

    if (content.isEmpty()) {
      return;
    }

    final Properties prop = parentResolverState.getElementProperties();

    final ColorAndRule foregroundCar = prop.get(ColorRuleSetProcessor.CSS_FOREGROUND_COLOR, ColorAndRule.class);
    final ColorAndRule backgroundCar = prop.get(BackgroundColorRuleSetProcessor.CSS_BACKGROUND_COLOR,
        ColorAndRule.class);

    // no color information
    if (foregroundCar == null || backgroundCar == null) {
      return;
    }

    // final Font font = prop.get(Properties.CSS_FONT, Font.class);
    // TODO manage font size

    // thread safety
    try {
      fgBgRegistry.register(colorDictionary.getForeground(foregroundCar.color, foregroundCar.rule),
          colorDictionary.getBackground(backgroundCar.color, backgroundCar.rule), content.length());
    } catch (final IllegalArgumentException e) {
      if (logger.isDebugEnabled())
        logger.debug("text: " + text + ", parentResolverState: " + parentResolverState, e);
    }

  }
}
