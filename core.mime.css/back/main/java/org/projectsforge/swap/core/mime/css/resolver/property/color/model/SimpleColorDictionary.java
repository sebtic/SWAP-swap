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
package org.projectsforge.swap.core.mime.css.resolver.color.model;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.projectsforge.swap.core.mime.css.resolver.color.SRGBColor;
import org.projectsforge.swap.core.mime.css.resolver.color.model.ColorRule.ColorOrigin;
import org.projectsforge.swap.core.mime.css.resolver.ruleset.CssRule;

/**
 * The class provides a simple implementation of a dictionary allowing to
 * register new entries in the dictionary.
 * 
 * @author Sébastien Aupetit
 */
public class SimpleColorDictionary implements ColorDictionary {

  /**
   * The Interface ModifiableColorEntry.
   */
  interface ModifiableColorEntry extends ColorDictionary.Entry {
    /**
     * Adds the rule.
     * 
     * @param rule the rule
     */
    public void addRule(ColorRule rule);
  }

  /**
   * The Enum RuleSetManagement.
   */
  public enum RuleSetManagement {

    /** Must separate entries by rules. */
    SEPARATED_RULES,
    /** Must not separate entries by rules. */
    MERGED_RULES
  }

  /**
   * The Enum TypeManagement.
   */
  public enum TypeManagement {

    /** Must differentiate foreground and background entries. */
    DIFFERENTIATE_FG_AND_BG,
    /** Must not differentiate foreground and background entries. */
    UNDIFFERENTIATED_FG_AND_BG
  }

  /** The index. */
  private final Map<ModifiableColorEntry, Integer> index = new HashMap<ModifiableColorEntry, Integer>();

  /** The inverted index. */
  private final Map<Integer, ModifiableColorEntry> invertedIndex = new HashMap<Integer, ModifiableColorEntry>();

  /** The current index of a new entry. */
  private int curId = 0;

  /** The rule set management. */
  private final RuleSetManagement ruleSetManagement;

  /** The type management. */
  private final TypeManagement typeManagement;

  /**
   * Instantiates a new simple dictionary.
   * 
   * @param typeManagement the type management
   * @param ruleSetManagement the rule set management
   */
  public SimpleColorDictionary(final TypeManagement typeManagement,
      final RuleSetManagement ruleSetManagement) {
    this.typeManagement = typeManagement;
    this.ruleSetManagement = ruleSetManagement;
  }

  /**
   * Gets the color index.
   * 
   * @param type the type
   * @param color the color
   * @param rule the rule
   * @return the index of the entry
   */
  private int get(final Type type, final SRGBColor color, final ColorRule rule) {
    final ModifiableColorEntry entry;

    if (ruleSetManagement == RuleSetManagement.MERGED_RULES) {
      entry = new MergedColorDictionaryEntry(curId, type, color, rule);
    } else {
      entry = new SeparatedColorDictionaryEntry(curId, type, color, rule);
    }

    final Integer id = index.get(entry);
    if (id != null) {
      final ModifiableColorEntry realEntry = invertedIndex.get(id);
      realEntry.addRule(rule);
      return id;
    } else {
      index.put(entry, curId);
      invertedIndex.put(curId, entry);
      return curId++;
    }
  }

  /**
   * Gets the background index.
   * 
   * @param color the color
   * @param rule the rule
   * @return the index of the entry
   */
  public int getBackground(final SRGBColor color, final CssRule rule) {
    if (typeManagement == TypeManagement.DIFFERENTIATE_FG_AND_BG) {
      return get(Type.BACKGROUND_COLOR, color, new ColorRule(rule, ColorOrigin.BACKGROUND));
    } else {
      return get(Type.UNFIFFERENTIATED_COLOR, color, new ColorRule(rule, ColorOrigin.BACKGROUND));
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.Dictionary#getColor(int)
   */
  @Override
  public SRGBColor getColor(final int id) {
    return invertedIndex.get(id).getColor();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #getEntries()
   */
  @Override
  public List<Entry> getEntries() {
    return new AbstractList<Entry>() {
      @Override
      public Entry get(final int index) {
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
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #getEntry(int)
   */
  @Override
  public Entry getEntry(final int id) {
    return invertedIndex.get(id);
  }

  /**
   * Gets the foreground index.
   * 
   * @param color the color
   * @param rule the rule
   * @return the index of the entry
   */
  public int getForeground(final SRGBColor color, final CssRule rule) {
    if (typeManagement == TypeManagement.DIFFERENTIATE_FG_AND_BG) {
      return get(Type.FOREGROUND_COLOR, color, new ColorRule(rule, ColorOrigin.FOREGROUND));
    } else {
      return get(Type.UNFIFFERENTIATED_COLOR, color, new ColorRule(rule, ColorOrigin.FOREGROUND));
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #getRealDictionary()
   */
  @Override
  public ColorDictionary getRealDictionary() {
    return this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.Dictionary#getRules(int)
   */
  @Override
  public Set<ColorRule> getRules(final int id) {
    return invertedIndex.get(id).getRules();
  }

  /**
   * Gets the rule set management.
   * 
   * @return the rule set management
   */
  public RuleSetManagement getRuleSetManagement() {
    return ruleSetManagement;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.Dictionary#getType(int)
   */
  @Override
  public Type getType(final int id) {
    return invertedIndex.get(id).getType();
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
  public int size() {
    return curId;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getEntries().toString();
  }

}
