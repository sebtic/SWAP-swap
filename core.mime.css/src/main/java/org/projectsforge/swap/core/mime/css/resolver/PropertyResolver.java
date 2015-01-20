/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This
 * software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This software is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this software. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.core.mime.css.resolver;

import java.util.ArrayList;
import java.util.List;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.http.CacheManager;
import org.projectsforge.swap.core.http.Request;
import org.projectsforge.swap.core.mime.css.StylesheetsCollector;
import org.projectsforge.swap.core.mime.css.nodes.Stylesheet;
import org.projectsforge.swap.core.mime.html.nodes.Comment;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.Node;
import org.projectsforge.swap.core.mime.html.nodes.Text;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.HEADElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.HTMLElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.SCRIPTElement;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.tasksexecutor.RecursiveTask;
import org.projectsforge.utils.tasksexecutor.RecursiveTaskExecutorException;
import org.projectsforge.utils.tasksexecutor.RecursiveTaskExecutorFactory;
import org.projectsforge.utils.visitor.VisitingMode;
import org.projectsforge.utils.visitor.Visitor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class PropertyResolver.
 * 
 * @author Sébastien Aupetit
 */
public class PropertyResolver extends Visitor<Node, ResolverState, Void> {
  // TODO high complexity due to recursivity

  /** The stateRecorders. */
  private final StateRecorder[] stateRecorders;

  /** The ruleSetProcessors. */
  private final RuleSetProcessor[] ruleSetProcessors;

  /** The rule set resolver. */
  private RuleSetResolver ruleSetResolver;

  /** The cache manager. */
  @Autowired
  private CacheManager cacheManager;

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The base request. */
  private final Request baseRequest;

  /** The media. */
  private final ICaseString media;

  /** The user agent stylesheet. */
  private final Stylesheet userAgentStylesheet;

  /** The user stylesheet. */
  private final Stylesheet userStylesheet;

  /** The document. */
  private final Document document;

  /** The stylesheet collector. */
  @Autowired
  private StylesheetsCollector stylesheetCollector;

  /** The allow hover. */
  private boolean allowHover = true;

  /** The allow focus. */
  private boolean allowFocus = true;

  /** The allow active. */
  private boolean allowActive = true;

  /** The allow visited. */
  private boolean allowVisited = true;

  private static RuleSetProcessor[] EMPTY_RULE_SET_PROCESSOR_ARRAY = new RuleSetProcessor[0];

  private static StateRecorder[] EMPTY_STATE_RECORDER_ARRAY = new StateRecorder[0];

  /**
   * Instantiates a new property resolver.
   * 
   * @param document the document
   * @param baseRequest the base request
   * @param media the media
   * @param userAgentStylesheet the user agent stylesheet
   * @param userStylesheet the user stylesheet
   * @param processors the processors
   * @param recorders the recorders
   */
  public PropertyResolver(final Document document, final Request baseRequest, final ICaseString media,
      final Stylesheet userAgentStylesheet, final Stylesheet userStylesheet, final List<RuleSetProcessor> processors,
      final List<StateRecorder> recorders) {
    super(VisitingMode.SEQUENTIAL);
    this.document = document;
    this.baseRequest = baseRequest;
    this.media = media;
    this.userAgentStylesheet = userAgentStylesheet;
    this.userStylesheet = userStylesheet;
    this.ruleSetProcessors = processors.toArray(PropertyResolver.EMPTY_RULE_SET_PROCESSOR_ARRAY);
    this.stateRecorders = recorders.toArray(PropertyResolver.EMPTY_STATE_RECORDER_ARRAY);
  }

  /**
   * Checks if is allow active.
   * 
   * @return true, if is allow active
   */
  public boolean isAllowActive() {
    return allowActive;
  }

  /**
   * Checks if is allow focus.
   * 
   * @return true, if is allow focus
   */
  public boolean isAllowFocus() {
    return allowFocus;
  }

  /**
   * Checks if is allow hover.
   * 
   * @return true, if is allow hover
   */
  public boolean isAllowHover() {
    return allowHover;
  }

  /**
   * Checks if is allow visited.
   * 
   * @return true, if is allow visited
   */
  public boolean isAllowVisited() {
    return allowVisited;
  }

  /**
   * Resolve.
   * 
   * @throws RecursiveTaskExecutorException
   */
  public void resolve() throws RecursiveTaskExecutorException {

    // build ruleset resolver
    ruleSetResolver = environment.autowireBean(new RuleSetResolver(stylesheetCollector.getStylesheets(document,
        baseRequest, media), userAgentStylesheet, userStylesheet, media));

    // apply to the document
    recurse(document.getChildrenCollection(), null);

    ruleSetResolver = null; // allow GC to do its work
  }

  /**
   * Sets the allow active.
   * 
   * @param allowActive the new allow active
   */
  public void setAllowActive(final boolean allowActive) {
    this.allowActive = allowActive;
  }

  /**
   * Sets the allow focus.
   * 
   * @param allowFocus the new allow focus
   */
  public void setAllowFocus(final boolean allowFocus) {
    this.allowFocus = allowFocus;
  }

  /**
   * Sets the allow hover.
   * 
   * @param allowHover the new allow hover
   */
  public void setAllowHover(final boolean allowHover) {
    this.allowHover = allowHover;
  }

  /**
   * Sets the allow visited.
   * 
   * @param allowVisited the new allow visited
   */
  public void setAllowVisited(final boolean allowVisited) {
    this.allowVisited = allowVisited;
  }

  /**
   * Visit.
   * 
   * @param element the element
   * @param parentResolverState the parent resolver state
   * @throws RecursiveTaskExecutorException
   */
  void visit(final AbstractElement element, final ResolverState parentResolverState)
      throws RecursiveTaskExecutorException {

    // for all state combination
    final ArrayList<RecursiveTask> tasks = new ArrayList<>();
    for (int value = 0; value < State.MAX_VALUE; ++value) {
      final State state = State.getState(value);

      if (!allowHover && state.hovered) {
        continue;
      }
      if (!allowFocus && state.focused) {
        continue;
      }
      if (!allowActive && state.active) {
        continue;
      }
      if (!allowVisited && state.visited) {
        continue;
      }

      final ResolverState resolverState = new ResolverState(element, state, parentResolverState);

      if (resolverState.hasValidState()) {
        tasks.add(new RecursiveTask() {
          @Override
          public void run() throws RecursiveTaskExecutorException {
            final CssRuleSet ruleSet = ruleSetResolver.getRuleSet(resolverState);

            for (final RuleSetProcessor ruleSetProcessor : ruleSetProcessors) {
              ruleSetProcessor.process(resolverState, ruleSet.getRules());
            }

            for (final StateRecorder stateRecorder : stateRecorders) {
              stateRecorder.element(resolverState);
            }

            recurse(element.getChildrenCollection(), resolverState);
          }
        });
      }
    }
    if (VisitingMode.SEQUENTIAL.equals(getDefaultVisitingMode())) {
      RecursiveTaskExecutorFactory.getInstance().executeSequentially(tasks);
    } else {
      RecursiveTaskExecutorFactory.getInstance().execute(tasks);
    }
  }

  /**
   * Visit comment.
   * 
   * @param comment the comment
   */
  void visit(final Comment comment) {
    // do not recurse since nothing is stylable here
    // text node are also children of comment nodes
  }

  /**
   * Visit.
   * 
   * @param element the element
   */
  void visit(final HEADElement element) {
    // nothing to do, we cut the visiting here
  }

  void visit(final HTMLElement element) throws RecursiveTaskExecutorException {
    recurse(element.getChildrenCollection(), new ResolverState(element, State.getState(0), null));
  }

  /**
   * Visit.
   * 
   * @param node the node
   * @param resolverState the resolver state
   * @throws RecursiveTaskExecutorException
   */
  void visit(final Node node, final ResolverState resolverState) throws RecursiveTaskExecutorException {
    recurse(node.getChildrenCollection(), resolverState);
  }

  /**
   * Visit.
   * 
   * @param element the element
   */
  void visit(final SCRIPTElement element) {
    // do not recurse since nothing is stylable here
    // text node can be children of script nodes
  }

  /**
   * Visit a text.
   * 
   * @param text the text
   * @param parentResolverState the parent resolver state
   */
  void visit(final Text text, final ResolverState parentResolverState) {
    for (final StateRecorder stateRecorder : stateRecorders) {
      stateRecorder.text(parentResolverState, text);
    }
  }
}
