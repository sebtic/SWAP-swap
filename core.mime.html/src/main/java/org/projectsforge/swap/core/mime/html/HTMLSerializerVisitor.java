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
 * <http://www.gnu.org/licenses/>. $Id: HTMLSerializerVisitor.java 125
 * 2012-04-04 14:59:59Z sebtic $
 */
package org.projectsforge.swap.core.mime.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringEscapeUtils;
import org.projectsforge.swap.core.mime.html.nodes.Comment;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.Node;
import org.projectsforge.swap.core.mime.html.nodes.Text;
import org.projectsforge.swap.core.mime.html.nodes.elements.APPLETElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.AREAElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.BASEElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.BASEFONTElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.BLOCKQUOTEElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.BODYElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.BRElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.BUTTONElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.CENTERElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.COLElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.DIVElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.DLElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.FIELDSETElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.FORMElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.FRAMEElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.FRAMESETElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.H1Element;
import org.projectsforge.swap.core.mime.html.nodes.elements.H2Element;
import org.projectsforge.swap.core.mime.html.nodes.elements.H3Element;
import org.projectsforge.swap.core.mime.html.nodes.elements.H4Element;
import org.projectsforge.swap.core.mime.html.nodes.elements.H5Element;
import org.projectsforge.swap.core.mime.html.nodes.elements.H6Element;
import org.projectsforge.swap.core.mime.html.nodes.elements.HEADElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.HRElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.HTMLElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.IFRAMEElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.IMGElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.INPUTElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.ISINDEXElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.LEGENDElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.LIElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.LINKElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.METAElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.NOSCRIPTElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.OBJECTElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.OLElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.PARAMElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.PElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.SCRIPTElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.SELECTElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.STYLEElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.TABLEElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.TDElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.TEXTAREAElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.TITLEElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.TRElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.ULElement;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.tasksexecutor.RecursiveTaskExecutorException;
import org.projectsforge.utils.visitor.VisitingMode;
import org.projectsforge.utils.visitor.Visitor;

// TODO: Auto-generated Javadoc
/**
 * A visitor which serialize the tree to a {@link Writer}.
 */
public class HTMLSerializerVisitor extends Visitor<Node, Void, Void> {

  /** The out. */
  private final StringBuilder out;

  /** The in script. */
  private boolean inScript = false;

  /** The Constant forbidenEndTags. */
  private static final Set<ICaseString> forbidenEndTags = new HashSet<ICaseString>(Arrays.asList(
      AREAElement.TAGNAME, BASEElement.TAGNAME, BASEFONTElement.TAGNAME, BRElement.TAGNAME,
      COLElement.TAGNAME, FRAMEElement.TAGNAME, HRElement.TAGNAME, IMGElement.TAGNAME,
      INPUTElement.TAGNAME, ISINDEXElement.TAGNAME, LINKElement.TAGNAME, METAElement.TAGNAME,
      PARAMElement.TAGNAME));

  /** The Constant newLineBeforeClosingTags. */
  private static final Set<ICaseString> newLineBeforeClosingTags = new HashSet<ICaseString>(
      Arrays.asList(HTMLElement.TAGNAME, BODYElement.TAGNAME, HEADElement.TAGNAME,
          FORMElement.TAGNAME, FRAMESETElement.TAGNAME, IFRAMEElement.TAGNAME, OLElement.TAGNAME,
          SELECTElement.TAGNAME, TABLEElement.TAGNAME, ULElement.TAGNAME, TRElement.TAGNAME,
          FIELDSETElement.TAGNAME, DIVElement.TAGNAME));

  /** The Constant newLineBeforeOpeningTags. */
  private static final Set<ICaseString> newLineBeforeOpeningTags = new HashSet<ICaseString>(
      Arrays.asList(BODYElement.TAGNAME, HEADElement.TAGNAME, DIVElement.TAGNAME,
          APPLETElement.TAGNAME, BLOCKQUOTEElement.TAGNAME, BUTTONElement.TAGNAME,
          DLElement.TAGNAME, FIELDSETElement.TAGNAME, FORMElement.TAGNAME, FRAMESETElement.TAGNAME,
          H1Element.TAGNAME, H2Element.TAGNAME, H3Element.TAGNAME, H4Element.TAGNAME,
          H5Element.TAGNAME, H6Element.TAGNAME, IFRAMEElement.TAGNAME, IMGElement.TAGNAME,
          LEGENDElement.TAGNAME, OBJECTElement.TAGNAME, OLElement.TAGNAME, PElement.TAGNAME,
          SELECTElement.TAGNAME, TABLEElement.TAGNAME, ULElement.TAGNAME, TITLEElement.TAGNAME,
          TRElement.TAGNAME, TDElement.TAGNAME, LIElement.TAGNAME, INPUTElement.TAGNAME,
          TEXTAREAElement.TAGNAME, STYLEElement.TAGNAME, SCRIPTElement.TAGNAME,
          CENTERElement.TAGNAME, METAElement.TAGNAME, LINKElement.TAGNAME));

  /** The xhtml. */
  private boolean xhtml = false;

  private String encoding;

  /**
   * The Constructor.
   * 
   * @param out the out
   */
  public HTMLSerializerVisitor(final StringBuilder out) {
    super(VisitingMode.SEQUENTIAL);
    this.out = out;
  }

  /**
   * Close tag.
   * 
   * @param element the element
   */
  private void closeTag(final AbstractElement element) {
    if (!HTMLSerializerVisitor.forbidenEndTags.contains(element.getTagName())) {
      // if (!element.getChildren().isEmpty()
      // &&
      // HTMLSerializerVisitor.openingTagsWithNewLine.contains(element.getName()))
      // {
      // out.append('\n');
      // }

      if (element.hasChildren()
          && HTMLSerializerVisitor.newLineBeforeClosingTags.contains(element.getTagName())) {
        if (!(element.getLastChild(AbstractElement.class) instanceof Text)) {
          out.append('\n');
        }
      }
      out.append("</").append(element.getTagName()).append('>');
    }
  }

  /**
   * Open tag.
   * 
   * @param element the element
   */
  private void openTag(final AbstractElement element) {
    if (HTMLSerializerVisitor.newLineBeforeOpeningTags.contains(element.getTagName())) {
      out.append('\n');
    }

    out.append('<').append(element.getTagName());
    if (element.hasAttributes()) {
      out.append(' ').append(element.getAttributesAsString(encoding));
    }
    if (HTMLSerializerVisitor.forbidenEndTags.contains(element.getTagName()) && xhtml) {
      out.append('/');
    }
    out.append('>');

    // if (!element.getChildren().isEmpty()
    // &&
    // HTMLSerializerVisitor.openingTagsWithNewLine.contains(element.getName()))
    // {
    // out.append('\n');
    // }
  }

  /**
   * Visit {@link AbstractElement}.
   * 
   * @param element the element
   * @throws IOException the IO exception
   * @throws RecursiveTaskExecutorException the recursive task executor
   *           exception
   */
  public void visit(final AbstractElement element) throws IOException,
      RecursiveTaskExecutorException {
    openTag(element);

    recurse(element.getChildrenCollection(), null);

    closeTag(element);
  }

  /**
   * Visit {@link Comment}.
   * 
   * @param comment the comment
   * @throws IOException the IO exception
   */
  public void visit(final Comment comment) throws IOException {
    out.append("<!--\n")// .append(StringEscapeUtils.escapeHtml(comment.getComment()))
        .append(comment.getComment()).append("\n-->\n");
  }

  /**
   * Visit {@link Document}.
   * 
   * @param document the document
   * @throws IOException the IO exception
   * @throws RecursiveTaskExecutorException the recursive task executor
   *           exception
   */
  public void visit(final Document document) throws IOException, RecursiveTaskExecutorException {
    if (document.getPublicId() != null && document.getPublicId().contains("xhtml1")) {
      xhtml = true;
    } else {
      xhtml = false;
    }

    if (document.getRootElement() != null) {
      out.append("<!DOCTYPE ").append(document.getRootElement());
      if (document.getPublicId() != null && !document.getPublicId().isEmpty()) {
        out.append(" PUBLIC \"").append(document.getPublicId()).append("\"");
      }
      if (document.getSystemId() != null && !document.getSystemId().isEmpty()) {
        out.append(" \"").append(document.getSystemId()).append("\"");
      }
      out.append(">\n");
    }
    
    encoding = document.getEncoding();

    for (final Node child : document.getChildrenCollection()) {
      recurse(child, null);
    }
  }

  /**
   * Visit {@link Node} (default).
   * 
   * @param node the node
   * @throws RecursiveTaskExecutorException the recursive task executor
   *           exception
   */
  public void visit(final Node node) throws RecursiveTaskExecutorException {
    recurse(node.getChildrenCollection(), null);
  }

  /**
   * Visit {@link NOSCRIPTElement}.
   * 
   * @param element the element
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws RecursiveTaskExecutorException the recursive task executor
   *           exception
   */
  public void visit(final NOSCRIPTElement element) throws IOException,
      RecursiveTaskExecutorException {
    inScript = true;
    visit((AbstractElement) element);
    inScript = false;
  }

  /**
   * Visit {@link SCRIPTElement}.
   * 
   * @param element the script
   * @throws IOException the IO exception
   * @throws RecursiveTaskExecutorException the recursive task executor
   *           exception
   */
  public void visit(final SCRIPTElement element) throws IOException, RecursiveTaskExecutorException {
    inScript = true;
    visit((AbstractElement) element);
    inScript = false;
  }

  /**
   * Visit {@link Text}.
   * 
   * @param text the text
   * @throws IOException the IO exception
   */
  public void visit(final Text text) throws IOException {
    if (inScript) {
      out.append(text.getContent());
    } else {
      out.append(StringEscapeUtils.escapeHtml(text.getContent()));// .append('\n');
    }
  }
}
