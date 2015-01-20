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
package org.projectsforge.swap.core.mime.css.resolver.font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.css.sac.LexicalUnit;

/**
 * The Class Font which store font properties and allows to parse CSS
 * attributes.
 * 
 * @author Sébastien Aupetit
 */
public class Font {

  /**
   * The Enum STYLE.
   */
  enum STYLE {

    /** The NORMAL. */
    NORMAL,
    /** The ITALIC. */
    ITALIC,
    /** The OBLIQUE. */
    OBLIQUE
  }

  /**
   * The Enum VARIANT.
   */
  enum VARIANT {

    /** The NORMAL. */
    NORMAL,
    /** The SMALLCAPS. */
    SMALLCAPS
  };

  /** The family. */
  private final List<String> family = new ArrayList<String>();

  /** The style. */
  private STYLE style;;

  /** The variant. */
  private VARIANT variant;

  /** The weight. */
  private int weight;

  /** The font size in point. */
  private int size;

  /** The line height as a multiple of the font size. */
  private float lineHeight;

  /**
   * Instantiates a new font.
   */
  public Font() {
    reset();
  }

  /**
   * Copy.
   * 
   * @param reference
   *          the reference
   */
  private void copy(final Font reference) {
    lineHeight = reference.lineHeight;
    size = reference.size;
    variant = reference.variant;
    style = reference.style;
    weight = reference.weight;
    family.clear();
    family.addAll(reference.family);
  }

  /**
   * Gets the family.
   * 
   * @return the family
   */
  public List<String> getFamily() {
    return Collections.unmodifiableList(family);
  }

  /**
   * Gets the line height as a multiple of the font size.
   * 
   * @return the line height as a multiple of the font size
   */
  public int getLineHeight() {
    return Math.round(getSize() * lineHeight);
  }

  /**
   * Gets the font size in point.
   * 
   * @return the font size in point
   */
  public int getSize() {
    return size;
  }

  /**
   * Gets the style.
   * 
   * @return the style
   */
  public STYLE getStyle() {
    return style;
  }

  /**
   * Gets the variant.
   * 
   * @return the variant
   */
  public VARIANT getVariant() {
    return variant;
  }

  /**
   * Gets the weight.
   * 
   * @return the weight
   */
  public int getWeight() {
    return weight;
  }

  /**
   * Make the font bolder than the parameter.
   * 
   * @param reference
   *          the reference
   */
  private void makeBolderThan(final Font reference) {
    switch (reference.getWeight()) {
      case 100:
      case 200:
      case 300:
        setWeight(400);
      case 400:
      case 500:
        setWeight(700);
      case 600:
      case 700:
      case 800:
      case 900:
        setWeight(900);
    }
  }

  /**
   * Make the font lighter than the parameter.
   * 
   * @param reference
   *          the reference
   */
  private void makeLighterThan(final Font reference) {
    switch (reference.getWeight()) {
      case 100:
      case 200:
      case 300:
      case 400:
      case 500:
        setWeight(100);
      case 600:
      case 700:
        setWeight(400);
      case 800:
      case 900:
        setWeight(700);
    }
  }

  /**
   * Parses the family.
   * 
   * @param value
   *          the value
   * @param reference
   *          the reference
   */
  public void parseFamily(LexicalUnit value, final Font reference) {
    family.clear();
    while (value != null) {
      switch (value.getLexicalUnitType()) {
        case LexicalUnit.SAC_IDENT:
          family.add(value.getStringValue());
          break;
        case LexicalUnit.SAC_STRING_VALUE:
          family.add(value.getStringValue());
          break;
        case LexicalUnit.SAC_INHERIT:
          family.clear();
          family.addAll(reference.family);
          return;
        case LexicalUnit.SAC_OPERATOR_COMMA:
          break;
      }
      value = value.getNextLexicalUnit();
    }
    if (family.size() == 0) {
      family.addAll(reference.family);
    }
  }

  /**
   * Parses the font.
   * 
   * @param value
   *          the value
   * @param reference
   *          the reference
   */
  public void parseFont(LexicalUnit value, final Font reference) {
    if (value.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
      copy(reference);
      return;
    }

    if (value.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
      final String ident = value.getStringValue();

      if ("caption".equalsIgnoreCase(ident)) {
        // TODO improve this
        reset();
        return;
      }

      if ("icon".equalsIgnoreCase(ident)) {
        reset();
        setSize(Math.round(this.getSize() * 0.8f));
        return;
      }

      if ("menu".equalsIgnoreCase(ident)) {
        reset();
        makeBolderThan(this);
      }

      if ("message-box".equalsIgnoreCase(ident)) {
        reset();
        return;
      }

      if ("small-caption".equalsIgnoreCase(ident)) {
        reset();
        setSize(Math.round(this.getSize() * 0.8f));
        return;
      }

      if ("status-bar".equalsIgnoreCase(ident)) {
        reset();
        setSize(Math.round(this.getSize() * 0.8f));
        return;
      }
    }

    reset();

    // font-style
    if (parseStyle(value, reference)) {
      value = value.getNextLexicalUnit();
      if (value == null) {
        return;
      }
    }

    // font-variant
    if (parseVariant(value, reference)) {
      value = value.getNextLexicalUnit();
      if (value == null) {
        return;
      }
    }

    // font-weight
    if (parseWeight(value, reference)) {
      value = value.getNextLexicalUnit();
      if (value == null) {
        return;
      }
    }

    // font-size
    if (parseSize(value, reference)) {
      value = value.getNextLexicalUnit();
      if (value == null) {
        return;
      }
    }

    // line-height
    if (value.getLexicalUnitType() == LexicalUnit.SAC_OPERATOR_SLASH) {
      value = value.getNextLexicalUnit();
      if (value == null) {
        return;
      }
      if (parseLineHeight(value, reference)) {
        value = value.getNextLexicalUnit();
        if (value == null) {
          return;
        }
      }
    }

    // font-family
    parseFamily(value, reference);

  }

  /**
   * Parses the line height.
   * 
   * @param value
   *          the value
   * @param reference
   *          the reference
   * @return true, if successful
   */
  public boolean parseLineHeight(final LexicalUnit value, final Font reference) {
    // Default :
    // 1ex = 0.5em
    // 1px = 0.75pt
    // 1pt = 1/72 in
    // 1in = 2.54cm
    // 1pc = 12pt

    switch (value.getLexicalUnitType()) {
      case LexicalUnit.SAC_DIMENSION:
        lineHeight = value.getFloatValue();
        return true;
      case LexicalUnit.SAC_REAL:
        lineHeight = value.getFloatValue();
        return true;
      case LexicalUnit.SAC_INTEGER:
        lineHeight = value.getIntegerValue();
        return true;

      case LexicalUnit.SAC_EM:
        lineHeight = value.getFloatValue();
        return true;
      case LexicalUnit.SAC_EX:
        lineHeight = 0.5f * value.getFloatValue();
        return true;

      case LexicalUnit.SAC_PERCENTAGE:
        lineHeight = value.getFloatValue() / 100.0f;
        return true;

      case LexicalUnit.SAC_PIXEL:
        lineHeight = 0.75f * value.getFloatValue() / getSize();
        return true;
      case LexicalUnit.SAC_INCH:
        lineHeight = value.getFloatValue() / 72f / getSize();
        return true;
      case LexicalUnit.SAC_CENTIMETER:
        lineHeight = value.getFloatValue() / 2.54f / 72f / getSize();
        return true;
      case LexicalUnit.SAC_MILLIMETER:
        lineHeight = value.getFloatValue() / 10f / 2.54f / 72f / getSize();
        return true;
      case LexicalUnit.SAC_POINT:
        lineHeight = value.getFloatValue() / getSize();
        return true;
      case LexicalUnit.SAC_PICA:
        lineHeight = 12 * value.getFloatValue() / getSize();
        return true;
      case LexicalUnit.SAC_IDENT:
        if ("normal".equalsIgnoreCase(value.getStringValue())) {
          lineHeight = 1.1f;
        }
        return true;
      case LexicalUnit.SAC_INHERIT:
        lineHeight = reference.lineHeight;
        return true;
    }
    return false;

  }

  /**
   * Parses the size.
   * 
   * @param value
   *          the value
   * @param reference
   *          the reference
   * @return true, if successful
   */
  public boolean parseSize(final LexicalUnit value, final Font reference) {
    // coefficient approximatif entre niveau : 1.15 avec arrondi à L'entier le
    // plus proche

    if (value.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
      setSize(reference.getSize());
      return true;
    }

    if (value.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
      final String identifier = value.getStringValue();

      if ("xx-small".equalsIgnoreCase(identifier)) {
        setSize(8);
        return true;
      }
      if ("x-small".equalsIgnoreCase(identifier)) {
        setSize(9);
        return true;
      }
      if ("small".equalsIgnoreCase(identifier)) {
        setSize(10);
        return true;
      }
      if ("medium".equalsIgnoreCase(identifier)) {
        setSize(12);
        return true;
      }
      if ("large".equalsIgnoreCase(identifier)) {
        setSize(14);
        return true;
      }
      if ("x-large".equalsIgnoreCase(identifier)) {
        setSize(16);
        return true;
      }
      if ("xx-large".equalsIgnoreCase(identifier)) {
        setSize(18);
        return true;
      }
      if ("larger".equalsIgnoreCase(identifier)) {
        setSize((int) Math.round(reference.getSize() * 1.15));
        return true;
      }
      if ("smaller".equalsIgnoreCase(identifier)) {
        setSize((int) Math.round(reference.getSize() / 1.15));
        return true;
      }
    }
    return false;
  }

  /**
   * Parses the style.
   * 
   * @param value
   *          the value
   * @param reference
   *          the reference
   * @return true, if successful
   */
  public boolean parseStyle(final LexicalUnit value, final Font reference) {

    if (value.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
      style = reference.style;
      return true;
    }

    if (value.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
      final String identifier = value.getStringValue();
      if ("normal".equalsIgnoreCase(identifier)) {
        style = STYLE.NORMAL;
        return true;
      }
      if ("italic".equalsIgnoreCase(identifier)) {
        style = STYLE.ITALIC;
        return true;
      }
      if ("oblique".equalsIgnoreCase(identifier)) {
        style = STYLE.OBLIQUE;
        return true;
      }
    }
    return false;
  }

  /**
   * Parses the variant.
   * 
   * @param value
   *          the value
   * @param reference
   *          the reference
   * @return true, if successful
   */
  public boolean parseVariant(final LexicalUnit value, final Font reference) {
    if (value.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
      variant = reference.variant;
      return true;
    }

    if (value.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
      final String identifier = value.getStringValue();
      if ("normal".equalsIgnoreCase(identifier)) {
        variant = VARIANT.NORMAL;
        return true;
      }
      if ("small-caps".equalsIgnoreCase(identifier)) {
        variant = VARIANT.SMALLCAPS;
        return true;
      }
    }
    return false;
  }

  /**
   * Parses the weight.
   * 
   * @param value
   *          the value
   * @param reference
   *          the reference
   * @return true, if successful
   */
  public boolean parseWeight(final LexicalUnit value, final Font reference) {
    if (value.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
      setWeight(reference.getWeight());
      return true;
    }

    if (value.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
      final String identifier = value.getStringValue();

      if ("normal".equalsIgnoreCase(identifier)) {
        setWeight(400);
        return true;
      }
      if ("bold".equalsIgnoreCase(identifier)) {
        setWeight(700);
        return true;
      }
      if ("bolder".equalsIgnoreCase(identifier)) {
        makeBolderThan(reference);
        return true;
      }
      if ("lighter".equalsIgnoreCase(identifier)) {
        makeLighterThan(reference);
        return true;
      }
    }

    if (value.getLexicalUnitType() == LexicalUnit.SAC_INTEGER) {
      setWeight(value.getIntegerValue());
      return true;
    }
    return false;
  }

  /**
   * Reset.
   */
  public void reset() {
    lineHeight = 1.1f; // normal
    size = 12; // medium
    variant = VARIANT.NORMAL; // normal
    style = STYLE.NORMAL; // normal
    weight = 400; // normal
    family.clear();
    family.add("sans serif");
  }

  /**
   * Sets the font size in point.
   * 
   * @param size
   *          the new font size in point
   */
  private void setSize(final int size) {
    this.size = size;
    if (this.size < 8) {
      this.size = 8;
    }
    if (this.size > 18) {
      this.size = 18;
    }
  }

  /**
   * Sets the weight.
   * 
   * @param weight
   *          the new weight
   */
  private void setWeight(final int weight) {
    // rounding
    this.weight = 100 * (weight / 100);
    // shrinking
    if (this.weight < 100) {
      this.weight = 100;
    }
    if (this.weight > 900) {
      this.weight = 900;
    }
  }
}

// http://www.w3.org/TR/CSS21/fonts.html#font-styling
