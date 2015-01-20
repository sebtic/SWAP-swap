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
package org.projectsforge.swap.core.webui;

import java.util.List;
import org.projectsforge.swap.core.webui.velocity.VelocityToolsView;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

/**
 * The Class WebUIConfiguration.
 * 
 * @author Sébastien Aupetit
 */
@SuppressWarnings("deprecation")
public class WebUIConfiguration extends WebMvcConfigurationSupport {

  // http://www.petrikainulainen.net/programming/tips-and-tricks/creating-restful-urls-with-spring-mvc-3-1-part-one-default-servlet-handler/
  // http://stackoverflow.com/questions/10192877/spring-multi-language

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
   * #addInterceptors(org.springframework.web.servlet.config.annotation.
   * InterceptorRegistry)
   */
  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(new LocaleChangeInterceptor());
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
   * #addResourceHandlers(org.springframework.web.servlet.config.annotation.
   * ResourceHandlerRegistry)
   */
  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**").addResourceLocations("classpath:/WEB-INF/resources/")
        .setCachePeriod(0);
  }

  /**
   * Annotation method handler adapter.
   * 
   * @return the annotation method handler adapter
   */
  @Bean
  AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter() {
    return new AnnotationMethodHandlerAdapter();
  }

  /**
   * Commons multipart resolver.
   * 
   * @return the commons multipart resolver
   */
  @Bean
  CommonsMultipartResolver commonsMultipartResolver() {
    final CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
    commonsMultipartResolver.setMaxUploadSize(100000);
    return commonsMultipartResolver;
  }

  /*
   * @Bean public UrlBasedViewResolver urlBasedViewResolver() {
   * UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
   * viewResolver.setViewClass( TilesView.class ); viewResolver.setOrder( 2 );
   * return viewResolver; }
   */

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
   * #configureHandlerExceptionResolvers(java.util.List)
   */
  @Override
  protected void configureHandlerExceptionResolvers(
      final List<HandlerExceptionResolver> exceptionResolvers) {
    addDefaultHandlerExceptionResolvers(exceptionResolvers);
    for (final HandlerExceptionResolver resolver : exceptionResolvers) {
      if (resolver instanceof AbstractHandlerExceptionResolver) {
        ((AbstractHandlerExceptionResolver) resolver).setWarnLogCategory("proxy.webui");
      }
    }
  }

  /**
   * Message source.
   * 
   * @return the message source
   */
  @Bean
  public MessageSource messageSource() {
    final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("/WEB-INF/messages/messages");
    return messageSource;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
   * #requestMappingHandlerMapping()
   */
  @Override
  @Bean
  public RequestMappingHandlerMapping requestMappingHandlerMapping() {
    final RequestMappingHandlerMapping mapping = super.requestMappingHandlerMapping();
    mapping.setDetectHandlerMethodsInAncestorContexts(true);
    return mapping;
  }

  /**
   * Velocity configurer.
   * 
   * @return the velocity configurer
   */
  @Bean
  VelocityConfigurer velocityConfigurer() {
    final VelocityConfigurer velocityConfigurer = new VelocityConfigurer();
    velocityConfigurer.setConfigLocation(new ClassPathResource(
        "/servlets/velocity/velocity.properties"));
    return velocityConfigurer;
  }

  /**
   * Velocity view resolver.
   * 
   * @return the velocity view resolver
   */
  @Bean
  VelocityViewResolver velocityViewResolver() {
    final VelocityViewResolver velocityViewResolver = new VelocityViewResolver();
    velocityViewResolver.setCache(true);
    velocityViewResolver.setPrefix("");
    velocityViewResolver.setSuffix(".vm");
    velocityViewResolver.setOrder(0);
    velocityViewResolver.setContentType("text/html; charset=UTF-8");
    velocityViewResolver.setViewClass(VelocityToolsView.class);
    return velocityViewResolver;
  }
}
