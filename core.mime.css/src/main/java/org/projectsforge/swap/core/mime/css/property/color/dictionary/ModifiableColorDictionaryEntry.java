/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.mime.css.property.color.dictionary;

import java.util.Set;
import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;

/**
 * The Class ModifiableColorDictionaryEntry.
 * 
 * @param <TColor> the generic type
 * @author Sébastien Aupetit
 */
abstract class ModifiableColorDictionaryEntry<TColor extends SRGBColor> extends
    ColorDictionaryEntry<TColor> {
  
  /**
   * Instantiates a new modifiable color dictionary entry.
   * 
   * @param id the id
   * @param type the type
   * @param color the color
   * @param rules the rules
   */
  public ModifiableColorDictionaryEntry(final int id, final ColorEntryType type,
      final TColor color, final Set<ColorRule> rules) {
    super(id, type, color, rules);
  }

  /**
   * Adds the rule.
   * 
   * @param rule the rule
   */
  public abstract void addRule(ColorRule rule);
}
