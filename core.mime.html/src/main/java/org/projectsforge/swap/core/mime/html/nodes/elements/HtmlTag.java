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
package org.projectsforge.swap.core.mime.html.nodes.elements;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface HtmlTag.
 * 
 * @author Sébastien Aupetit
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HtmlTag {
}
