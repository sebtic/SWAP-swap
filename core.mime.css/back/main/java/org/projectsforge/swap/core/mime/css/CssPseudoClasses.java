package org.projectsforge.swap.core.mime.css;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.projectsforge.utils.icasestring.ICaseString;

/**
 * The constants for pseudo classes.
 * 
 * @author Sébastien Aupetit
 */
public final class CssPseudoClasses {

  /** The Constant FIRST_CHILD. */
  public static final ICaseString FIRST_CHILD = new ICaseString("first-child");

  /** The Constant ACTIVE. */
  public static final ICaseString ACTIVE = new ICaseString("active");

  /** The Constant FOCUS. */
  public static final ICaseString FOCUS = new ICaseString("focus");

  /** The Constant HOVER. */
  public static final ICaseString HOVER = new ICaseString("hover");

  /** The Constant LANG. */
  public static final ICaseString LANG = new ICaseString("lang");

  /** The Constant LINK. */
  public static final ICaseString LINK = new ICaseString("link");

  /** The Constant VISITED. */
  public static final ICaseString VISITED = new ICaseString("visited");

  /** The Constant allSets which list the set of influencing pseudo-classes. */
  public static final List<Set<ICaseString>> allSets;

  static {
    final List<ICaseString> list = new ArrayList<ICaseString>();
    list.add(CssPseudoClasses.ACTIVE);
    list.add(CssPseudoClasses.FOCUS);
    list.add(CssPseudoClasses.HOVER);
    list.add(CssPseudoClasses.LINK);
    list.add(CssPseudoClasses.VISITED);

    allSets = CssPseudoClasses.generateAllSets(list);
  }

  /**
   * Generate all the sets of influencing pseudo-classes.
   * 
   * @param list the list
   * @return the list
   */
  private static List<Set<ICaseString>> generateAllSets(final List<ICaseString> list) {
    final List<Set<ICaseString>> result = new ArrayList<Set<ICaseString>>();

    // initialize the counters
    final int tab[] = new int[list.size()];
    for (int i = 0; i < tab.length; ++i) {
      tab[i] = 0;
    }

    // do the enumeration
    while (tab[tab.length - 1] != 2) {
      final Set<ICaseString> newSetLink = new HashSet<ICaseString>();
      final Set<ICaseString> newSetVisited = new HashSet<ICaseString>();

      for (int i = 0; i < tab.length; ++i) {
        if (tab[i] == 1) {
          newSetLink.add(list.get(i));
          newSetVisited.add(list.get(i));
        }
      }
      newSetLink.add(CssPseudoClasses.LINK);
      result.add(newSetLink);

      newSetVisited.add(CssPseudoClasses.VISITED);
      result.add(newSetVisited);

      tab[0]++;
      for (int i = 0; i < tab.length - 1 && tab[i] > 1; ++i) {
        tab[i] = 0;
        tab[i + 1]++;
      }
    }

    return result;
  }

  /**
   * Instantiates a new css pseudo classes.
   */
  private CssPseudoClasses() {
    // nothing to do
  }
}
