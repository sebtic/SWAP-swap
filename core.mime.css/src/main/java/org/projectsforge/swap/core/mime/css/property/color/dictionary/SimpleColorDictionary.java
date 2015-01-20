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
 * <http://www.gnu.org/licenses/>. \$Id$
 */
package org.projectsforge.swap.core.mime.css.property.color.dictionary;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.projectsforge.swap.core.mime.css.property.color.ImmutableSRGBColor;
import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;
import org.projectsforge.swap.core.mime.css.property.color.dictionary.ColorRule.ColorOrigin;
import org.projectsforge.swap.core.mime.css.resolver.CssRule;

/**
 * The class provides a simple implementation of a dictionary allowing to
 * register new entries in the dictionary.
 * 
 * @param <TColor> the generic type
 * @author Sébastien Aupetit
 */
public class SimpleColorDictionary<TColor extends SRGBColor> extends ColorDictionary<TColor> {

  /**
   * The Enum RuleSetManagement.
   * 
   * @author Sébastien Aupetit
   */
  public enum RuleSetManagement {

    /** Must separate entries by rules. */
    SEPARATED_RULES,
    /** Must not separate entries by rules. */
    MERGED_RULES
  }

  /**
   * The Enum TypeManagement.
   * 
   * @author Sébastien Aupetit
   */
  public enum TypeManagement {

    /** Must differentiate foreground and background entries. */
    DIFFERENTIATE_FG_AND_BG,
    /** Must not differentiate foreground and background entries. */
    UNDIFFERENTIATED_FG_AND_BG
  }

  /** The index. */
  private final Map<ModifiableColorDictionaryEntry<TColor>, Integer> index = new HashMap<ModifiableColorDictionaryEntry<TColor>, Integer>();

  /** The inverted index. */
  private final List<ModifiableColorDictionaryEntry<TColor>> entries = new ArrayList<>();

  /** The rule set management. */
  private final RuleSetManagement ruleSetManagement;

  /** The type management. */
  private final TypeManagement typeManagement;

  /** The color class. */
  private final Class<TColor> colorClass;

  /**
   * Instantiates a new simple dictionary.
   * 
   * @param colorClass the color class
   * @param typeManagement the type management
   * @param ruleSetManagement the rule set management
   */
  public SimpleColorDictionary(final Class<TColor> colorClass, final TypeManagement typeManagement,
      final RuleSetManagement ruleSetManagement) {
    this.colorClass = colorClass;
    this.typeManagement = typeManagement;
    this.ruleSetManagement = ruleSetManagement;
  }

  /**
   * Gets the color index.
   * 
   * @param type the type
   * @param color the color (assumed to be immutable or to be non shared
   *          instances
   * @param rule the rule
   * @return the index of the entry
   */
  private int get(final ColorEntryType type, final TColor color, final ColorRule rule) {
    final ModifiableColorDictionaryEntry<TColor> entry;

    if (ruleSetManagement == RuleSetManagement.MERGED_RULES) {
      entry = new MergedColorDictionaryEntry<TColor>(entries.size(), type, color, rule);
    } else {
      entry = new SeparatedColorDictionaryEntry<TColor>(entries.size(), type, color, rule);
    }

    synchronized (this) {
      final Integer id = index.get(entry);
      if (id != null) {
        final ModifiableColorDictionaryEntry<TColor> realEntry = entries.get(id);
        realEntry.addRule(rule);
        return id;
      } else {
        entries.add(entry);
        index.put(entry, entries.size() - 1);
        return entries.size() - 1;
      }
    }
  }

  /**
   * Gets the background index.
   * 
   * @param srgbColor the srgb color
   * @param rule the rule
   * @return the index of the entry
   */
  @SuppressWarnings("unchecked")
  public int getBackground(final SRGBColor srgbColor, final CssRule rule) {
    TColor color;
    if (srgbColor instanceof ImmutableSRGBColor && colorClass == ImmutableSRGBColor.class) {
      // for an immutable srgb color, we need not to duplicate the color
      color = (TColor) srgbColor;
    } else {
      try {
        color = colorClass.getConstructor(SRGBColor.class).newInstance(srgbColor);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        throw new IllegalArgumentException("Can not create instance", e);
      }
    }

    if (typeManagement == TypeManagement.DIFFERENTIATE_FG_AND_BG) {
      return get(ColorEntryType.BACKGROUND_COLOR, color,
          new ColorRule(rule, ColorOrigin.BACKGROUND));
    } else {
      return get(ColorEntryType.UNFIFFERENTIATED_COLOR, color, new ColorRule(rule,
          ColorOrigin.BACKGROUND));
    }
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.resolver.color.model.
   * ColorDictionary #getEntries()
   */
  @Override
  public List<ColorDictionaryEntry<TColor>> getEntries() {
    return new AbstractList<ColorDictionaryEntry<TColor>>() {
      @Override
      public ColorDictionaryEntry<TColor> get(final int index) {
        return getEntry(index);
      }

      @Override
      public int size() {
        return SimpleColorDictionary.this.size();
      }
    };
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.resolver.color.model.
   * ColorDictionary #getEntry(int)
   */
  @Override
  public synchronized ColorDictionaryEntry<TColor> getEntry(final int id) {
    return entries.get(id);
  }

  /**
   * Gets the foreground index.
   * 
   * @param srgbColor the srgb color
   * @param rule the rule
   * @return the index of the entry
   */
  @SuppressWarnings("unchecked")
  public int getForeground(final SRGBColor srgbColor, final CssRule rule) {
    TColor color;
    if (srgbColor instanceof ImmutableSRGBColor && colorClass == ImmutableSRGBColor.class) {
      // for an immutable srgb color, we need not to duplicate the color
      color = (TColor) srgbColor;
    } else {
      try {
        color = colorClass.getConstructor(SRGBColor.class).newInstance(srgbColor);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        throw new IllegalArgumentException("Can not create instance", e);
      }
    }

    if (typeManagement == TypeManagement.DIFFERENTIATE_FG_AND_BG) {
      return get(ColorEntryType.FOREGROUND_COLOR, color,
          new ColorRule(rule, ColorOrigin.FOREGROUND));
    } else {
      return get(ColorEntryType.UNFIFFERENTIATED_COLOR, color, new ColorRule(rule,
          ColorOrigin.FOREGROUND));
    }
  }

  /**
   * Gets the rule set management.
   * 
   * @return the rule set management
   */
  public RuleSetManagement getRuleSetManagement() {
    return ruleSetManagement;
  }

  /**
   * Gets the type management.
   * 
   * @return the type management
   */
  public TypeManagement getTypeManagement() {
    return typeManagement;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.plugins.cssimprover.model.Dictionary#size()
   */
  @Override
  public synchronized int size() {
    return entries.size();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + typeManagement + "," + ruleSetManagement + "," + getEntries().toString() + "]";
  }

}
