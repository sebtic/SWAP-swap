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
 * <http://www.gnu.org/licenses/>. $Id: CssPseudoElements.java 90 2011-06-21
 * 17:29:12Z sebtic $
 */
package org.projectsforge.swap.core.mime.css;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.icasestring.ICaseStringKeyCollections;

/**
 * The Class CssPseudoElements.
 * 
 * @author Sébastien Aupetit
 */
public final class CssPseudoElements {

  /** The Constant emptySet. */
  public static final Set<ICaseString> emptySet = ICaseStringKeyCollections
      .caseInsensitiveSet(new HashSet<ICaseString>());

  /** The Constant FIRST_LINE. */
  public static final ICaseString FIRST_LINE = new ICaseString("first-line");

  /** The Constant FIRST_LETTER. */
  public static final ICaseString FIRST_LETTER = new ICaseString("first-letter");

  /** The Constant BEFORE. */
  public static final ICaseString BEFORE = new ICaseString("before");

  /** The Constant AFTER. */
  public static final ICaseString AFTER = new ICaseString("after");

  /** The Constant allSets. */
  public static final List<Set<ICaseString>> allSets;

  static {
    final List<ICaseString> list = new ArrayList<ICaseString>();
    list.add(CssPseudoElements.FIRST_LETTER);
    list.add(CssPseudoElements.FIRST_LINE);
    list.add(CssPseudoElements.BEFORE);
    list.add(CssPseudoElements.AFTER);
    allSets = CssPseudoElements.generateAllSets(list);
  }

  /**
   * Generate all sets of pseudo elementsToRoot.
   * 
   * @param list the list
   * @return the list
   */
  private static List<Set<ICaseString>> generateAllSets(final List<ICaseString> list) {
    final List<Set<ICaseString>> result = new ArrayList<Set<ICaseString>>();

    // initialize counters
    final int tab[] = new int[list.size()];
    for (int i = 0; i < tab.length; ++i) {
      tab[i] = 0;
    }

    // enumerate
    while (tab[tab.length - 1] != 2) {
      final Set<ICaseString> newSet = new HashSet<ICaseString>();
      for (int i = 0; i < tab.length; ++i) {
        if (tab[i] == 1) {
          newSet.add(list.get(i));
        }
      }
      result.add(newSet);

      tab[0]++;
      for (int i = 0; i < tab.length - 1 && tab[i] > 1; ++i) {
        tab[i] = 0;
        tab[i + 1]++;
      }
    }

    return result;
  }

  /**
   * Instantiates a new pseudo elementsToRoot.
   */
  private CssPseudoElements() {
    // nothing to do
  }
}
