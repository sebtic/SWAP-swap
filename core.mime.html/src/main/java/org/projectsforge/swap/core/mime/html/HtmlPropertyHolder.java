package org.projectsforge.swap.core.mime.html;

import org.projectsforge.swap.core.mime.html.parser.ParserType;
import org.projectsforge.utils.propertyregistry.EnumProperty;
import org.projectsforge.utils.propertyregistry.IntegerProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.SystemValueHolder;

public class HtmlPropertyHolder implements PropertyHolder {

  public static final EnumProperty<ParserType> htmlParser = new EnumProperty<>("htmlparser",
      ParserType.class, ParserType.TAGSOUP);

  public static final IntegerProperty cachedNamesMaxSize = new IntegerProperty(
      new SystemValueHolder("org.projectsforge.swap.mime.html.cachedNamesMaxSize"), 2048);

}
