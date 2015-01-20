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
 * $Id: DateUtil.java 9 2010-03-08 22:04:08Z sebtic $
 */
package org.projectsforge.swap.core.http;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A utility class whose code is based on Jetty 7 HttpFields class.
 */
public abstract class DateUtil {

  /** The Constant GMT. */
  private final static TimeZone GMT = TimeZone.getTimeZone("GMT");

  /** The Constant dateReceiveFormat. */
  private final static String dateReceiveFormat[] = { "EEE, dd MMM yyyy HH:mm:ss zzz",
      "EEE, dd-MMM-yy HH:mm:ss", "EEE MMM dd HH:mm:ss yyyy", "EEE, dd MMM yyyy HH:mm:ss",
      "EEE dd MMM yyyy HH:mm:ss zzz", "EEE dd MMM yyyy HH:mm:ss", "EEE MMM dd yyyy HH:mm:ss zzz",
      "EEE MMM dd yyyy HH:mm:ss", "EEE MMM-dd-yyyy HH:mm:ss zzz", "EEE MMM-dd-yyyy HH:mm:ss",
      "dd MMM yyyy HH:mm:ss zzz", "dd MMM yyyy HH:mm:ss", "dd-MMM-yy HH:mm:ss zzz",
      "dd-MMM-yy HH:mm:ss", "MMM dd HH:mm:ss yyyy zzz", "MMM dd HH:mm:ss yyyy",
      "EEE MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy",
      "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss" };

  /** The Constant dateReceiveParser. */
  private final static SimpleDateFormat dateReceiveParser[] = new SimpleDateFormat[DateUtil.dateReceiveFormat.length];

  static {
    DateUtil.GMT.setID("GMT");
    for (int i = 0; i < DateUtil.dateReceiveParser.length; i++) {
      if (DateUtil.dateReceiveParser[i] == null) {
        DateUtil.dateReceiveParser[i] = new SimpleDateFormat(DateUtil.dateReceiveFormat[i],
            Locale.US);
        DateUtil.dateReceiveParser[i].setTimeZone(DateUtil.GMT);
      }
    }
  }

  /**
   * Parses the.
   * 
   * @param dateVal
   *          the date val
   * @return the long
   */
  public static synchronized long parse(final String dateVal) {
    for (final SimpleDateFormat element : DateUtil.dateReceiveParser) {
      try {
        final Date date = (Date) element.parseObject(dateVal);
        return date.getTime();
      } catch (final java.lang.Exception e) {
      }
    }

    if (dateVal.endsWith(" GMT")) {
      final String val = dateVal.substring(0, dateVal.length() - 4);

      for (final SimpleDateFormat element : DateUtil.dateReceiveParser) {
        try {
          final Date date = (Date) element.parseObject(val);
          return date.getTime();
        } catch (final java.lang.Exception e) {
        }
      }
    }
    return -1;
  }

}
