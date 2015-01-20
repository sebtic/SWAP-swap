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
package org.projectsforge.swap.core.persistence;

import org.hibernate.AssertionFailure;
import org.hibernate.cfg.EJB3NamingStrategy;
import org.hibernate.internal.util.StringHelper;

/**
 * An hibernate naming strategy which preserves namespaces in table naming.
 * 
 * @author Sébastien Aupetit
 */
public class NamespaceNamingStrategy extends EJB3NamingStrategy {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Adds the underscores.
   * 
   * @param name the name
   * @return the string
   */
  protected static String addUnderscores(final String name) {
    return name.replace('.', '_');
  }

  /*
   * (non-Javadoc)
   * @see
   * org.hibernate.cfg.EJB3NamingStrategy#classToTableName(java.lang.String)
   */
  @Override
  public String classToTableName(final String className) {
    return addUnderscores(className);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.hibernate.cfg.EJB3NamingStrategy#collectionTableName(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String collectionTableName(final String ownerEntity, final String ownerEntityTable,
      final String associatedEntity, final String associatedEntityTable, final String propertyName) {
    return tableName(new StringBuilder(ownerEntityTable)
        .append("_")
        .append(
            associatedEntityTable != null ? associatedEntityTable : addUnderscores(propertyName))
        .toString());
  }

  /*
   * (non-Javadoc)
   * @see
   * org.hibernate.cfg.EJB3NamingStrategy#foreignKeyColumnName(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String foreignKeyColumnName(final String propertyName, final String propertyEntityName,
      final String propertyTableName, final String referencedColumnName) {
    final String header = propertyName != null ? addUnderscores(propertyName) : propertyTableName;
    if (header == null) {
      throw new AssertionFailure("NamingStrategy not properly filled");
    }
    return columnName(header + "_" + referencedColumnName);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.hibernate.cfg.EJB3NamingStrategy#logicalCollectionColumnName(java.lang
   * .String, java.lang.String, java.lang.String)
   */
  @Override
  public String logicalCollectionColumnName(final String columnName, final String propertyName,
      final String referencedColumn) {
    return StringHelper.isNotEmpty(columnName) ? columnName : propertyName + "_" + referencedColumn;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.hibernate.cfg.EJB3NamingStrategy#logicalCollectionTableName(java.lang
   * .String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String logicalCollectionTableName(final String tableName, final String ownerEntityTable,
      final String associatedEntityTable, final String propertyName) {
    if (tableName != null) {
      return tableName;
    } else {
      // use of a stringbuffer to workaround a JDK bug
      return new StringBuffer(ownerEntityTable).append("_")
          .append(associatedEntityTable != null ? associatedEntityTable : propertyName).toString();
    }

  }

  /*
   * (non-Javadoc)
   * @see
   * org.hibernate.cfg.EJB3NamingStrategy#logicalColumnName(java.lang.String,
   * java.lang.String)
   */
  @Override
  public String logicalColumnName(final String columnName, final String propertyName) {
    return StringHelper.isNotEmpty(columnName) ? columnName : propertyName;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.hibernate.cfg.EJB3NamingStrategy#propertyToColumnName(java.lang.String)
   */
  @Override
  public String propertyToColumnName(final String propertyName) {
    return addUnderscores(propertyName);
  }
}
