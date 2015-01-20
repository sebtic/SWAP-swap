// $codepro.audit.disable methodJavadoc
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
package org.projectsforge.swap.core.environment.impl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;

/**
 * A {@link WebApplicationContext} and {@link AbstractApplicationContext} which
 * delegates all methods calls to another application context.
 * 
 * @author Sébastien Aupetit
 */
public class WrapperWebApplicationContext extends AbstractApplicationContext implements
    WebApplicationContext {

  /** The application context. */
  private final AbstractApplicationContext applicationContext;

  /**
   * Instantiates a new wrapper web application context.
   * 
   * @param applicationContext the application context
   */
  public WrapperWebApplicationContext(final AbstractApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    if (!(applicationContext instanceof WebApplicationContext)) {
      throw new IllegalArgumentException(
          "The application context must be WebApplicationContext. Current: "
              + applicationContext.getClass().getName());
    }
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * addApplicationListener(org.springframework.context.ApplicationListener)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void addApplicationListener(final ApplicationListener listener) {
    applicationContext.addApplicationListener(listener);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * addBeanFactoryPostProcessor
   * (org.springframework.beans.factory.config.BeanFactoryPostProcessor)
   */
  @Override
  public void addBeanFactoryPostProcessor(final BeanFactoryPostProcessor beanFactoryPostProcessor) {
    applicationContext.addBeanFactoryPostProcessor(beanFactoryPostProcessor);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#close()
   */
  @Override
  public void close() {
    applicationContext.close();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#closeBeanFactory
   * ()
   */
  @Override
  protected void closeBeanFactory() {
    // nothing to do

  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#containsBean
   * (java.lang.String)
   */
  @Override
  public boolean containsBean(final String name) {
    return applicationContext.containsBean(name);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * containsBeanDefinition(java.lang.String)
   */
  @Override
  public boolean containsBeanDefinition(final String beanName) {
    return applicationContext.containsBeanDefinition(beanName);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * containsLocalBean(java.lang.String)
   */
  @Override
  public boolean containsLocalBean(final String name) {
    return applicationContext.containsLocalBean(name);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#destroy()
   */
  @Override
  public void destroy() {
    applicationContext.destroy();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    return applicationContext.equals(obj);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * findAnnotationOnBean(java.lang.String, java.lang.Class)
   */
  @Override
  public <A extends Annotation> A findAnnotationOnBean(final String beanName,
      final Class<A> annotationType) {
    return applicationContext.findAnnotationOnBean(beanName, annotationType);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getAliases
   * (java.lang.String)
   */
  @Override
  public String[] getAliases(final String name) {
    return applicationContext.getAliases(name);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * getApplicationListeners()
   */
  @Override
  public Collection<ApplicationListener<?>> getApplicationListeners() {
    return applicationContext.getApplicationListeners();
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * getAutowireCapableBeanFactory()
   */
  @Override
  public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
    return applicationContext.getAutowireCapableBeanFactory();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getBean(
   * java.lang.Class)
   */
  @Override
  public <T> T getBean(final Class<T> requiredType) throws BeansException {
    return applicationContext.getBean(requiredType);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getBean(
   * java.lang.String)
   */
  @Override
  public Object getBean(final String name) throws BeansException {
    return applicationContext.getBean(name);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getBean(
   * java.lang.String, java.lang.Class)
   */
  @Override
  public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
    return applicationContext.getBean(name, requiredType);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getBean(
   * java.lang.String, java.lang.Object[])
   */
  @Override
  public Object getBean(final String name, final Object... args) throws BeansException {
    return applicationContext.getBean(name, args);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * getBeanDefinitionCount()
   */
  @Override
  public int getBeanDefinitionCount() {
    return applicationContext.getBeanDefinitionCount();
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * getBeanDefinitionNames()
   */
  @Override
  public String[] getBeanDefinitionNames() {
    return applicationContext.getBeanDefinitionNames();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getBeanFactory
   * ()
   */
  @Override
  public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
    return applicationContext.getBeanFactory();
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * getBeanFactoryPostProcessors()
   */
  @Override
  public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
    return applicationContext.getBeanFactoryPostProcessors();
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * getBeanNamesForType(java.lang.Class)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public String[] getBeanNamesForType(final Class type) {
    return applicationContext.getBeanNamesForType(type);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * getBeanNamesForType(java.lang.Class, boolean, boolean)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public String[] getBeanNamesForType(final Class type, final boolean includeNonSingletons,
      final boolean allowEagerInit) {
    return applicationContext.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getBeansOfType
   * (java.lang.Class)
   */
  @Override
  public <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeansException {
    return applicationContext.getBeansOfType(type);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getBeansOfType
   * (java.lang.Class, boolean, boolean)
   */
  @Override
  public <T> Map<String, T> getBeansOfType(final Class<T> type, final boolean includeNonSingletons,
      final boolean allowEagerInit) throws BeansException {
    return applicationContext.getBeansOfType(type, includeNonSingletons, allowEagerInit);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * getBeansWithAnnotation(java.lang.Class)
   */
  @Override
  public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType)
      throws BeansException {
    return applicationContext.getBeansWithAnnotation(annotationType);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.core.io.DefaultResourceLoader#getClassLoader()
   */
  @Override
  public ClassLoader getClassLoader() {
    return applicationContext.getClassLoader();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getDisplayName
   * ()
   */
  @Override
  public String getDisplayName() {
    return applicationContext.getDisplayName();
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#getId()
   */
  @Override
  public String getId() {
    return applicationContext.getId();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getMessage
   * (org.springframework.context.MessageSourceResolvable, java.util.Locale)
   */
  @Override
  public String getMessage(final MessageSourceResolvable resolvable, final Locale locale)
      throws NoSuchMessageException {
    return applicationContext.getMessage(resolvable, locale);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getMessage
   * (java.lang.String, java.lang.Object[], java.util.Locale)
   */
  @Override
  public String getMessage(final String code, final Object[] args, final Locale locale)
      throws NoSuchMessageException {
    return applicationContext.getMessage(code, args, locale);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getMessage
   * (java.lang.String, java.lang.Object[], java.lang.String, java.util.Locale)
   */
  @Override
  public String getMessage(final String code, final Object[] args, final String defaultMessage,
      final Locale locale) {
    return applicationContext.getMessage(code, args, defaultMessage, locale);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getParent()
   */
  @Override
  public ApplicationContext getParent() {
    return applicationContext.getParent();
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * getParentBeanFactory()
   */
  @Override
  public BeanFactory getParentBeanFactory() {
    return applicationContext.getParentBeanFactory();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.core.io.DefaultResourceLoader#getResource(java.lang
   * .String)
   */
  @Override
  public Resource getResource(final String location) {
    return applicationContext.getResource(location);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getResources
   * (java.lang.String)
   */
  @Override
  public Resource[] getResources(final String locationPattern) throws IOException {
    return applicationContext.getResources(locationPattern);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.web.context.WebApplicationContext#getServletContext()
   */
  @Override
  public ServletContext getServletContext() {
    return ((WebApplicationContext) applicationContext).getServletContext();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getStartupDate
   * ()
   */
  @Override
  public long getStartupDate() {
    return applicationContext.getStartupDate();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#getType(
   * java.lang.String)
   */
  @Override
  public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
    return applicationContext.getType(name);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return applicationContext.hashCode();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#isActive()
   */
  @Override
  public boolean isActive() {
    return applicationContext.isActive();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#isPrototype
   * (java.lang.String)
   */
  @Override
  public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
    return applicationContext.isPrototype(name);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#isRunning()
   */
  @Override
  public boolean isRunning() {
    return applicationContext.isRunning();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#isSingleton
   * (java.lang.String)
   */
  @Override
  public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
    return applicationContext.isSingleton(name);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#isTypeMatch
   * (java.lang.String, java.lang.Class)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public boolean isTypeMatch(final String name, final Class targetType)
      throws NoSuchBeanDefinitionException {
    return applicationContext.isTypeMatch(name, targetType);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#publishEvent
   * (org.springframework.context.ApplicationEvent)
   */
  @Override
  public void publishEvent(final ApplicationEvent event) {
    applicationContext.publishEvent(event);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#refresh()
   */
  @Override
  public void refresh() throws BeansException, IllegalStateException {
    applicationContext.refresh();
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * refreshBeanFactory()
   */
  @Override
  protected void refreshBeanFactory() throws BeansException, IllegalStateException { // $codepro.audit.disable
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#
   * registerShutdownHook()
   */
  @Override
  public void registerShutdownHook() {
    applicationContext.registerShutdownHook();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.core.io.DefaultResourceLoader#setClassLoader(java.lang
   * .ClassLoader)
   */
  @Override
  public void setClassLoader(final ClassLoader classLoader) {
    applicationContext.setClassLoader(classLoader);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#setDisplayName
   * (java.lang.String)
   */
  @Override
  public void setDisplayName(final String displayName) {
    applicationContext.setDisplayName(displayName);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#setId(java
   * .lang.String)
   */
  @Override
  public void setId(final String id) {
    applicationContext.setId(id);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#setParent
   * (org.springframework.context.ApplicationContext)
   */
  @Override
  public void setParent(final ApplicationContext parent) {
    applicationContext.setParent(parent);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#start()
   */
  @Override
  public void start() {
    applicationContext.start();
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.context.support.AbstractApplicationContext#stop()
   */
  @Override
  public void stop() {
    applicationContext.stop();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.context.support.AbstractApplicationContext#toString()
   */
  @Override
  public String toString() {
    return applicationContext.toString();
  }
}
