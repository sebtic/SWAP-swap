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
 * <http://www.gnu.org/licenses/>. $Id: PropertyResolver.java 125 2012-04-04
 * 14:59:59Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver;

import java.util.ArrayList;
import java.util.List;
import org.projectsforge.swap.core.http.CacheManager;
import org.projectsforge.swap.core.http.Request;
import org.projectsforge.swap.core.mime.css.nodes.Stylesheet;
import org.projectsforge.swap.core.mime.css.resolver.ruleset.CssRuleSet;
import org.projectsforge.swap.core.mime.css.resolver.ruleset.RuleSetResolver;
import org.projectsforge.swap.core.mime.css.resolver.stylesheet.StylesheetsCollector;
import org.projectsforge.swap.core.mime.html.nodes.Comment;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.Node;
import org.projectsforge.swap.core.mime.html.nodes.Text;
import org.projectsforge.swap.core.mime.html.nodes.elements.AElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.SCRIPTElement;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.visitor.VisitingMode;
import org.projectsforge.utils.visitor.Visitor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The class that resolves properties for the elements of a HTML tree.
 * 
 * @author Sébastien Aupetit
 */
public class PropertyResolver {

  /**
   * The Interface Processor which allows to do computation of properties.
   */
  public interface Processor {

    /**
     * Process a rule set for an element.
     * 
     * @param document the document
     * @param elements the deque of elements from root to current element
     * @param states the deque of states from root to current element
     * @param properties the deque of properties from root to current element
     * @param ruleSet the rule set applicable to the element
     */
    void process(Document document, ArrayList<AbstractElement> elements, ArrayList<State> states,
        ArrayList<Properties> properties, CssRuleSet ruleSet);

  }

  class PropertyResolverVisitor extends Visitor<Node, Void, Void> {

    public PropertyResolverVisitor() {
      super(VisitingMode.SEQUENTIAL);
    }

    /**
     * Visit.
     * 
     * @param element the element
     */
    public void visit(final AbstractElement element) {
      elements.add(element);

      for (final Recorder recorder : recorders) {
        recorder.beforeElement(elements, states, properties);
      }

      // for all state combination
      for (int value = 0; value < State.MAX_VALUE; ++value) {
        final State state = State.getState(value);

        // TODO shortcut
        if (value != 0) {
          continue;
        }

        if (!(element instanceof AElement) && state.visited) {
          // visited is useful only for AElement
          continue;
        }

        // TODO verify if some combination are useless to speed up things

        states.add(state);
        properties.add(new Properties());

        final CssRuleSet ruleSet = ruleSetResolver.getRuleSet(element, null);
        for (final Processor processor : processors) {
          processor.process(document, elements, states, properties, ruleSet);
        }

        for (final Recorder recorder : recorders) {
          recorder.element(elements, states, properties);
        }

        recurse(element.getChildren());

        properties.remove(properties.size() - 1);
        states.remove(states.size() - 1);
      }

      for (final Recorder recorder : recorders) {
        recorder.afterElement(elements, states, properties);
      }

      elements.remove(elements.size() - 1);
    }

    /**
     * Visit comment.
     * 
     * @param comment the comment
     */
    public void visit(final Comment comment) {
      // do not recurse since nothing is stylable here
      // text node are also children of comment nodes
    }

    /**
     * Visit.
     * 
     * @param document the document
     */
    public void visit(final Document document) {

      // collect stylesheets
      final CollectStylesheetVisitor visitor = new CollectStylesheetVisitor(cacheManager,
          baseRequest, media);
      visitor.recurse(new Node[] { document }, null);

      // build ruleset resolver
      ruleSetResolver = new RuleSetResolver(document, visitor.getCollectedStylesheets(),
          userAgentStylesheet, userStylesheet, media);

      // apply to the document
      recurse(document.getChildren());
    }

    /**
     * Visit.
     * 
     * @param node the node
     */
    public void visit(final Node node) {
      recurse(node.getChildren());
    }

    public void visit(final SCRIPTElement element) {
      // do not recurse since nothing is stylable here
      // text node can be children of script nodes
    }

    /**
     * Visit a text.
     * 
     * @param text the text
     */
    public void visit(final Text text) {
      for (final Recorder recorder : recorders) {
        recorder.text(elements, states, properties, text);
      }
    }
  }

  /**
   * The Interface Recorder which allows to record the properties associated
   * with an element in a given state.
   */
  public interface Recorder {

    /**
     * Inform that the recording of the element has been done.
     * 
     * @param elements the deque of elements from root to current element
     * @param states the deque of states from root to the parent of the current
     *          element
     * @param properties the deque of properties from root to the parent of the
     *          current element
     */
    void afterElement(ArrayList<AbstractElement> elements, ArrayList<State> states,
        ArrayList<Properties> properties);

    /**
     * Inform that the recording of the element will start.
     * 
     * @param elements the deque of elements from root to current element
     * @param states the deque of states from root to the parent of the current
     *          element
     * @param properties the deque of properties from root to the parent of the
     *          current element
     */
    void beforeElement(ArrayList<AbstractElement> elements, ArrayList<State> states,
        ArrayList<Properties> properties);

    /**
     * Record the properties for an element in the given state.
     * 
     * @param elements the deque of elements from root to current element
     * @param states the deque of states from root to current element
     * @param properties the deque of properties from root to current element
     */
    void element(ArrayList<AbstractElement> elements, ArrayList<State> states,
        ArrayList<Properties> properties);

    /**
     * Record the properties for a text.
     * 
     * @param elements the deque of elements from root to the parent of the
     *          current
     * @param states the deque of states from root to the parent of the current
     * @param properties the deque of properties from root to the parent of the
     *          current
     * @param text the text
     */
    void text(ArrayList<AbstractElement> elements, ArrayList<State> states,
        ArrayList<Properties> properties, Text text);
  }

  /** The document. */
  private final Document document;

  /** The media. */
  private final ICaseString media;

  /** The cache manager. */
  @Autowired
  private CacheManager cacheManager;

  /** The base request. */
  private final Request baseRequest;

  /** The user agent stylesheet. */
  private final Stylesheet userAgentStylesheet;

  /** The user stylesheet. */
  private final Stylesheet userStylesheet;

  /** The rule set resolver. */
  private RuleSetResolver ruleSetResolver;

  /** The elements. */
  private final ArrayList<AbstractElement> elements = new ArrayList<AbstractElement>();

  /** The states. */
  private final ArrayList<State> states = new ArrayList<State>();

  /** The properties. */
  private final ArrayList<Properties> properties = new ArrayList<Properties>();

  /** The recorders. */
  private final List<Recorder> recorders;

  /** The processors. */
  private final List<Processor> processors;

  /**
   * Instantiates a new property resolver.
   * 
   * @param cacheManager the cache manager
   * @param baseRequest the base request
   * @param document the document
   * @param media the media
   * @param userAgentStylesheet the user agent stylesheet
   * @param userStylesheet the user stylesheet
   * @param processors the property resolver processors
   * @param recorders the properties recorder
   */
  public PropertyResolver(final Request baseRequest, final Document document,
      final ICaseString media, final Stylesheet userAgentStylesheet,
      final Stylesheet userStylesheet, final List<Processor> processors,
      final List<Recorder> recorders) {
    this.baseRequest = baseRequest;
    this.document = document;
    this.media = media;
    this.userAgentStylesheet = userAgentStylesheet;
    this.userStylesheet = userStylesheet;
    this.processors = processors;
    this.recorders = recorders;
  }

  /**
   * Resolve the elements of the document.
   */
  public void resolve() {
    new PropertyResolverVisitor().recurse(document);
  }
}
