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
 * <http://www.gnu.org/licenses/>. $Id: CacheManager.java 125 2012-04-04
 * 14:59:59Z sebtic $
 */
package org.projectsforge.swap.core.http;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.handlers.HandlersManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A cache manager.
 * 
 * @author Sébastien Aupetit
 */
@Component("http.cacheManager")
public class CacheManager {

  /** The request to response cache. */
  final Map<RequestIdentity, SoftReference<Response>> requestToResponseCache = new HashMap<>();

  /** The handler manager. */
  @Autowired
  private HandlersManager handlerManager;

  /** The cached mimes. */
  private final Set<String> cachedMimes = new HashSet<String>();

  /** The environment. */
  @Autowired
  private Environment environment;

  /**
   * Clear soft references.
   */
  private void clearSoftReferences() {
    final Iterator<Entry<RequestIdentity, SoftReference<Response>>> it = requestToResponseCache.entrySet().iterator();
    while (it.hasNext()) {
      final Entry<RequestIdentity, SoftReference<Response>> entry = it.next();
      if (entry.getValue().get() == null) {
        it.remove();
      }
    }
  }

  /**
   * Gets the response.
   * 
   * @param request the request
   * @return the response
   */
  Response getResponse(final Request request) {
    // filter headers
    request.getHeaders().removeAll(RequestHeaderConstants.forwardRequestFilter);

    request.getLogger().debug("Request headers : {}", request.getHeaders());

    if ("GET".equals(request.getMethod()) && request.isCacheable()) {
      synchronized (this) {
        clearSoftReferences();

        final RequestIdentity identity = new RequestIdentity(request);

        final SoftReference<Response> responseReference = requestToResponseCache.get(identity);
        Response response;
        if (responseReference != null) {
          response = responseReference.get();
        } else {
          response = null;
        }

        if (response != null && response.isDirect()) {
          response = null;
        }

        if (response == null) {
          response = environment.autowireBean(new Response(request));
        }

        requestToResponseCache.put(identity, new SoftReference<Response>(response));
        return response;
      }
    } else {
      // can not be cached
      return environment.autowireBean(new Response(request));
    }
  }

  /**
   * Gets a snapshot of the cache.
   * 
   * @return the snapshot
   */
  public synchronized List<Response> getSnapshot() {
    clearSoftReferences();

    final List<Response> snapshot = new ArrayList<Response>();
    for (final SoftReference<Response> reference : requestToResponseCache.values()) {
      final Response response = reference.get();
      if (response != null) {
        snapshot.add(response);
      }
    }
    return snapshot;
  }

  /**
   * Instantiates a new cache manager.
   */
  @PostConstruct
  void init() {
    // Compute the set of handled mimes. Not handled mimes are direct access
    for (final Class<?> clazz : handlerManager.getHandlersByInterface(IndirectContent.class)) {
      final Mime mimeAnnotation = clazz.getAnnotation(Mime.class);
      if (mimeAnnotation != null) {
        for (final String mime : mimeAnnotation.mime()) {
          if (!"*".equals(mime)) {
            cachedMimes.add(mime);
          }
        }
      }
    }
  }

  /**
   * Checks if is mime handled.
   * 
   * @param mime the mime
   * @return true, if is mime handled
   */
  public boolean isMimeHandled(final String mime) {
    return cachedMimes.contains(mime);
  }

  /**
   * Instantiates a new request based on another request. Among other,
   * authentification and cookies are added to the new request. Two call with
   * same parameters can return the same request instance.
   * 
   * @param context the context request
   * @param uri the uri
   * @return the abstract request
   */
  public Request newContextualRequest(final Request context, final String uri) {
    try {
      return environment.autowireBean(new Request(context, uri));
    } catch (final MalformedURLException e) {
      if (uri.startsWith("//")) {
        try {
          return environment.autowireBean(new Request(context, uri.substring(2)));
        } catch (final MalformedURLException e1) {
          throw new IllegalArgumentException("Invalid URI", e);
        }
      } else {
        throw new IllegalArgumentException("Invalid URI", e);
      }

    }
  }

  /**
   * Instantiates a new request for a URL. Two call with same parameters can
   * return the same request instance.
   * 
   * @param url the URL
   * @return the abstract request
   */
  public Request newRequest(final URL url) {
    return environment.autowireBean(new Request(url));
  }

  /**
   * New servlet request.
   * 
   * @param httpServletRequest the http servlet request
   * @return the abstract request
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public Request newServletRequest(final HttpServletRequest httpServletRequest) throws IOException {
    return environment.autowireBean(new Request(httpServletRequest));
  }

}
