package org.projectsforge.swap.core.persistence;

import org.projectsforge.utils.propertyregistry.BooleanProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class PersistencePropertyHolder implements PropertyHolder {

  public static final BooleanProperty hibernateShowSql = new BooleanProperty("hibernate.showSql",
      false);

  public static final BooleanProperty hibernateGloballyQuotedIdentifiers = new BooleanProperty(
      "hibernate.globally_quoted_identifiers", true);

  public static final StringProperty databaseName = new StringProperty("database.path", "swap");

}
