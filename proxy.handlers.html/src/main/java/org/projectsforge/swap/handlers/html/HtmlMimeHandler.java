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
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.handlers.html;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PreDestroy;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.handlers.Handler;
import org.projectsforge.swap.core.handlers.HandlerContext;
import org.projectsforge.swap.core.handlers.HandlerExecutor;
import org.projectsforge.swap.core.handlers.Resource;
import org.projectsforge.swap.core.http.Mime;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.core.mime.html.HTMLSerializerVisitor;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.handlers.mime.MimeHandler;
import org.projectsforge.swap.handlers.mime.StatisticsCollector;
import org.projectsforge.utils.temporarystreams.TemporaryContentHolder;
import org.projectsforge.utils.temporarystreams.TemporaryStreamsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The MIME handler managing the transformation of HTML responses.
 * 
 * @author Sébastien Aupetit
 */
@Handler(singleton = true)
@Mime(mime = "text/html")
class HtmlMimeHandler implements MimeHandler {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(HtmlMimeHandler.class);

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The temporary streams factory. */
  @Autowired
  private TemporaryStreamsFactory temporaryStreamsFactory;

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.handlers.MimeHandler#handle(org.projectsforge.swap
   * .handlers.Context, org.projectsforge.swap.core.http.AbstractResponse)
   */
  @Override
  public void handle(final HandlerContext<MimeHandler> context, final StatisticsCollector statisticsCollector,
      final Response response) throws Exception {
    statisticsCollector.startTimer(StatisticsCollector.PROXY_HANDLER_HTML_ALL_TIMER_KEY);

    try {
      final AtomicBoolean cancelTransformation = new AtomicBoolean(false);

      final HandlerContext<HtmlTransformation> transformationContext = environment
          .autowireBean(new HandlerContext<HtmlTransformation>(context, HtmlTransformation.class,
              new HandlerExecutor<HtmlTransformation>() {
                @Override
                public void execute(final HandlerContext<HtmlTransformation> context, final Class<?> handlerClass,
                    final HtmlTransformation handler) throws Exception {
                  statisticsCollector.startTimer(StatisticsCollector.PROXY_HANDLER_HTML_DO_TIMER_KEY
                      + handler.getClass().getCanonicalName());
                  try {
                    if (!handler.transform(context, statisticsCollector, response)) {
                      cancelTransformation.set(true);
                      logger.debug("Cancelling transformation for {}", response.getRequest().getURL());
                    }
                  } finally {
                    statisticsCollector.stopTimer(StatisticsCollector.PROXY_HANDLER_HTML_DO_TIMER_KEY
                        + handler.getClass().getCanonicalName());
                  }
                }

                @Override
                public void shutdown(final HandlerContext<HtmlTransformation> context, final Class<?> handlerClass,
                    final HtmlTransformation handler) throws Exception {
                  for (final Method method : handler.getClass().getMethods()) {
                    if (method.getAnnotation(PreDestroy.class) != null && method.getParameterTypes().length == 0) {
                      try {
                        method.invoke(handler, new Object[] {});
                      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        logger.warn("An error occurred while calling " + method, e);
                      }
                    }
                  }
                }

              }, null));

      if (!transformationContext.execute()) {
        cancelTransformation.set(true);
        logger.debug("An exception was thrown. Canceling transformation for {}", response.getRequest());
      }

      final Resource<Document> document;
      if (cancelTransformation.get()) {
        document = null;
      } else {
        document = context.getResource(HtmlDomTransformation.HTML_DOM, Document.class);
      }

      statisticsCollector.setValue(StatisticsCollector.PROXY_HANDLER_HTML_CANCELEDTRANSFORMATION_VALUE_KEY,
          cancelTransformation.get());

      if (document != null) {
        statisticsCollector.setValue(StatisticsCollector.PROXY_HANDLER_HTML_TRANSFORMEDDOM_VALUE_KEY, document.get());
        statisticsCollector.startTimer(StatisticsCollector.PROXY_HANDLER_HTML_SERIALIZERESPONSE_TIMER_KEY);
        try {
          final StringBuilder sb = new StringBuilder();
          environment.autowireBean(new HTMLSerializerVisitor(sb)).recurse(document.get(), null);

          final Response transformedResponse = environment.autowireBean(new Response(response));
          final TemporaryContentHolder content = new TemporaryContentHolder(temporaryStreamsFactory);
          try (final OutputStream out = content.getOutputStream()) {
            out.write(sb.toString().getBytes());
          }
          transformedResponse.setContent(content);
          final Resource<Response> outputResponse = context.getResource(MimeHandler.OUTPUT_RESPONSE, Response.class);
          outputResponse.lockWrite();
          try {
            outputResponse.set(transformedResponse);
          } finally {
            outputResponse.unlockWrite();
          }
        } finally {
          statisticsCollector.stopTimer(StatisticsCollector.PROXY_HANDLER_HTML_SERIALIZERESPONSE_TIMER_KEY);
        }
      }
    } finally {
      statisticsCollector.stopTimer(StatisticsCollector.PROXY_HANDLER_HTML_ALL_TIMER_KEY);
    }
  }
}
