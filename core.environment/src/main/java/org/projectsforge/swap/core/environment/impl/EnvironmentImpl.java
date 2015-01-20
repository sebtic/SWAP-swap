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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.PropertyConfigurator;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.StarterMonitor;
import org.projectsforge.swap.core.environment.filter.AnnotatedClassFilter;
import org.projectsforge.swap.core.environment.provider.AnnotatedClassesProvider;
import org.projectsforge.swap.core.environment.provider.InMemoryXmlContextProvider;
import org.projectsforge.swap.core.environment.provider.XmlContextsProvider;
import org.projectsforge.swap.core.environment.startermonitor.LoggerMonitorStarter;
import org.projectsforge.utils.annotations.AnnotationScanner;
import org.projectsforge.utils.annotations.AnnotationScannerImpl;
import org.projectsforge.utils.path.JRegExPathMatcher;
import org.projectsforge.utils.path.PathMatcherCollection;
import org.projectsforge.utils.path.PathRepository;
import org.projectsforge.utils.path.PathRepositoryFactory;
import org.projectsforge.utils.path.ResourcesDetector;
import org.projectsforge.utils.path.URISearcher;
import org.projectsforge.utils.propertyregistry.PropertyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.core.io.UrlResource;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.xml.sax.InputSource;

/**
 * The base class managing the execution environment.
 * 
 * @author Sébastien Aupetit
 */
public abstract class EnvironmentImpl implements org.projectsforge.swap.core.environment.Environment {

  /** The Constant SETTINGS_FILENAME. */
  public static final String SETTINGS_FILENAME = "settings.conf";

  /** The Constant ANNOTATION_SCANNER_BEAN_NAME. */
  public static final String ANNOTATION_SCANNER_BEAN_NAME = "org.projectsforge.swap.core.environment.AnnotationScanner";

  public static final String PROPERTY_REGISTRY_BEAN_NAME = "org.projectsforge.swap.core.environment.PropertyRegistry";

  /** The Constant CONFIGURATION_PROPERTIES_PLACE_HOLDER_BEAN_NAME. */
  public static final String CONFIGURATION_PROPERTIES_PLACE_HOLDER_BEAN_NAME = "org.projectsforge.swap.core.environment.ConfigurationPropertiesPlaceHolder";

  /** The Constant URI_SEARCHER_BEAN_NAME. */
  public static final String URI_SEARCHER_BEAN_NAME = "org.projectsforge.swap.core.environment.URISearcher";

  /** The Constant PATH_REPOSITORY_BEAN_NAME. */
  public static final String PATH_REPOSITORY_BEAN_NAME = "org.projectsforge.swap.core.environment.PathRepository";

  /** The Constant STARTER_MONITOR_BEAN_NAME. */
  public static final String STARTER_MONITOR_BEAN_NAME = "org.projectsforge.swap.core.environment.StarterMonitor";

  /** The configuration directory. */
  private File configurationDirectory;

  /** The user configuration file. */
  private File userConfigurationFile;

  /** The context. */
  private GenericWebApplicationContext context;

  /** The logger. */
  private Logger logger;

  /** The initial log4j settings. */
  private final Properties initialLog4jSettings = new Properties();

  /** The path repository. */
  private PathRepository pathRepository;

  /** The overrided configuration properties. */
  private final Properties overridedConfigurationProperties = new Properties();

  /** The starter monitor. */
  private StarterMonitor starterMonitor = new LoggerMonitorStarter();

  /** The spring annotations class to detect. */
  private static final Class<?>[] SPRING_ANNOTATIONS = new Class<?>[] { org.springframework.stereotype.Component.class,
      org.springframework.stereotype.Controller.class, org.springframework.stereotype.Service.class,
      org.springframework.context.annotation.Configuration.class };

  /** The annotated class provider. */
  private AnnotatedClassesProvider annotatedClassProvider = new AnnotatedClassesProvider() {
    @Override
    public Map<String, Class<?>> getClasses(final Environment environment) {
      final AnnotationScanner annotationScanner = context.getBean(AnnotationScanner.class);
      return annotationScanner.getClasses(AnnotationScanner.INCLUDE_PLAIN_CLASSES
      /* | AnnotationScanner.INCLUDE_INTERFACES */, false, EnvironmentImpl.SPRING_ANNOTATIONS);
    }

  };

  /** The xml context provider. */
  private XmlContextsProvider xmlContextProvider = new XmlContextsProvider() {
    @Override
    public Set<URL> getUris(final Environment environment) {
      return xmlContextMatcher.getMatchedPaths();
    }
  };

  /** The name. */
  private final String name;

  /** The in memory xml context provider. */
  private InMemoryXmlContextProvider inMemoryXmlContextProvider = new InMemoryXmlContextProvider() {
    @Override
    public Collection<InputSource> getInputSources(final org.projectsforge.swap.core.environment.Environment environment) {
      return Collections.emptyList();
    }
  };

  /** The Constant ENVIRONMENT_BEAN_NAME. */
  public static final String ENVIRONMENT_BEAN_NAME = "org.projectsforge.swap.configuration.Environment";

  /** The allow user interaction. */
  private boolean allowUserInteraction = false;

  /** The xml context matcher. */
  private JRegExPathMatcher<Set<URL>> xmlContextMatcher;

  /** The annotated class filter. */
  private AnnotatedClassFilter annotatedClassFilter = new AnnotatedClassFilter() {

    @Override
    public boolean filterAnnotatedClass(final Class<?> annotatedClass) {
      // Exclude WebMvcConfigurationSupport because it conflict with embedded
      // server
      if (WebMvcConfigurationSupport.class.isAssignableFrom(annotatedClass)) {
        return true;
      }
      return false;
    }
  };

  private final ReentrantLock stoppedLock = new ReentrantLock();

  private final Condition stoppedCondition = stoppedLock.newCondition();

  /**
   * Instantiates a new environment.
   * 
   * @param name the name of the environment
   */
  protected EnvironmentImpl(final String name) {
    this.name = name;

    configurationDirectory = new File(new File(System.getProperty("java.io.tmpdir")), "swaptemp-"
        + UUID.randomUUID().toString());

    initialLog4jSettings.setProperty("log4j.rootCategory", "DEBUG, CONSOLE");
    initialLog4jSettings.setProperty("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
    initialLog4jSettings.setProperty("log4j.appender.CONSOLE.Threshold", "DEBUG");
    initialLog4jSettings.setProperty("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
    initialLog4jSettings.setProperty("log4j.appender.CONSOLE.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
    initialLog4jSettings.setProperty("log4j.logger.org.projectsforge.utils.path", "INFO");
    initialLog4jSettings.setProperty("log4j.logger.org.projectsforge.utils.path.ManifestClassPathDetector", "ERROR");
    initialLog4jSettings.setProperty("log4j.logger.org.springframework.web.context", "INFO");
    initialLog4jSettings.setProperty("log4j.logger.org.eclipse.jetty", "INFO");
    initialLog4jSettings.setProperty("log4j.logger.jndi", "INFO");

    pathRepository = PathRepositoryFactory.getNewPathRepository(true);
    pathRepository.addPathDetector(new ResourcesDetector("config/defaultlog4j.properties"));
    pathRepository.addPathDetector(new ResourcesDetector("config/log4j.properties"));
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.E#autowireBean(T)
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T autowireBean(final T instance) {
    context.getAutowireCapableBeanFactory().autowireBean(instance);
    return (T) context.getAutowireCapableBeanFactory().initializeBean(instance, Objects.toString(instance));
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.Environment#getAllowUserInteraction()
   */
  @Override
  public boolean getAllowUserInteraction() {
    return allowUserInteraction;
  }

  /**
   * Gets the annotated class filter.
   * 
   * @return the annotated class filter
   */
  public AnnotatedClassFilter getAnnotatedClassFilter() {
    return annotatedClassFilter;
  }

  /**
   * Gets the annotated class provider.
   * 
   * @return the annotated class provider
   */
  public AnnotatedClassesProvider getAnnotatedClassProvider() {
    return annotatedClassProvider;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.E#getContext()
   */
  @Override
  public GenericWebApplicationContext getContext() {
    return context;
  }

  /**
   * Gets the in memory xml context provider.
   * 
   * @return the in memory xml context provider
   */
  public InMemoryXmlContextProvider getInMemoryXmlContextProvider() {
    return inMemoryXmlContextProvider;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.E#getLogger()
   */
  @Override
  public Logger getLogger() {
    return logger;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.environment.Environment#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.environment.Environment#getPathRepository()
   */
  @Override
  public PathRepository getPathRepository() {
    return pathRepository;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.E#getStarterMonitor()
   */
  @Override
  public StarterMonitor getStarterMonitor() {
    return starterMonitor;
  }

  /**
   * Gets the user configuration file.
   * 
   * @return the user configuration file
   */
  public File getUserConfigurationFile() {
    return userConfigurationFile;
  }

  /**
   * Gets the xml context provider.
   * 
   * @return the xml context provider
   */
  public XmlContextsProvider getXmlContextProvider() {
    return xmlContextProvider;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.configurer.E#saveConfigurationProperties()
   */
  @Override
  public synchronized void saveConfigurationProperties() {
    context.getBean(PropertyRegistry.class).save(userConfigurationFile);
  }

  /**
   * Sets the allow user interaction.
   * 
   * @param allowUserInteraction the new allow user interaction
   */
  public void setAllowUserInteraction(final boolean allowUserInteraction) {
    this.allowUserInteraction = allowUserInteraction;
  }

  /**
   * Sets the annotated class filter.
   * 
   * @param annotatedClassFilter the new annotated class filter
   */
  public void setAnnotatedClassFilter(final AnnotatedClassFilter annotatedClassFilter) {
    this.annotatedClassFilter = annotatedClassFilter;
  }

  /**
   * Sets the annotated class provider.
   * 
   * @param annotatedClassProvider the new annotated class provider
   */
  public void setAnnotatedClassProvider(final AnnotatedClassesProvider annotatedClassProvider) {
    this.annotatedClassProvider = annotatedClassProvider;
  }

  /**
   * Sets the configuration directory.
   * 
   * @param configurationDirectory the new configuration directory
   */
  public void setConfigurationDirectory(final File configurationDirectory) {
    this.configurationDirectory = configurationDirectory;
  }

  /**
   * Sets the in memory xml context provider.
   * 
   * @param inMemoryXmlContextProvider the new in memory xml context provider
   */
  public void setInMemoryXmlContextProvider(final InMemoryXmlContextProvider inMemoryXmlContextProvider) {
    this.inMemoryXmlContextProvider = inMemoryXmlContextProvider;
  }

  /**
   * Sets the override configuration properties.
   * 
   * @param key the key
   * @param value the value
   */
  public void setOverridedConfigurationProperties(final String key, final String value) {
    overridedConfigurationProperties.put(key, value);
  }

  /**
   * Sets the path repository.
   * 
   * @param pathRepository the new path repository
   */
  public void setPathRepository(final PathRepository pathRepository) {
    this.pathRepository = pathRepository;
  }

  /**
   * Sets the starter monitor.
   * 
   * @param starterMonitor the new starter monitor
   */
  public void setStarterMonitor(final StarterMonitor starterMonitor) {
    this.starterMonitor = starterMonitor;
  }

  /**
   * Sets the user configuration file.
   * 
   * @param userConfigurationFile the new user configuration file
   */
  public void setUserConfigurationFile(final File userConfigurationFile) {
    this.userConfigurationFile = userConfigurationFile;
  }

  /**
   * Sets the xml context provider.
   * 
   * @param xmlContextProvider the new xml context provider
   */
  public void setXmlContextProvider(final XmlContextsProvider xmlContextProvider) {
    this.xmlContextProvider = xmlContextProvider;
  }

  /**
   * Start.
   * 
   * @throws Exception the exception
   */
  public void start() throws Exception {
    // Step 1 : logging

    // Step 1.1 : define minimal logging configuration
    PropertyConfigurator.configure(initialLog4jSettings);
    // Step 1.2 : redirect commons logging to SLF4J
    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SLF4JLogFactory");
    // Step 1.3 : get the EnvironmentImpl logger
    logger = LoggerFactory.getLogger(getClass());

    // Step 1.4 : detect main log4j configuration
    URL log4jUrl = Thread.currentThread().getContextClassLoader().getResource("config/log4j.properties");
    if (log4jUrl == null) {
      log4jUrl = Thread.currentThread().getContextClassLoader().getResource("config/defaultlog4j.properties");
    }
    Properties mainLog4jProperties;
    logger.info("Main Log4j configuration is given by {}", log4jUrl);
    try (final InputStream is = log4jUrl.openStream()) {
      mainLog4jProperties = new Properties();
      mainLog4jProperties.load(is);
    } catch (final IOException e) {
      logger.warn("Can not load properties from {}. Fallbacking to initial settings", log4jUrl);
      mainLog4jProperties = new Properties();
      mainLog4jProperties.putAll(initialLog4jSettings);
    }
    PropertyConfigurator.configure(mainLog4jProperties);

    logger.info("Configuration directory set to {}", configurationDirectory);
    configurationDirectory.mkdirs();
    if (userConfigurationFile == null) {
      userConfigurationFile = new File(configurationDirectory, EnvironmentImpl.SETTINGS_FILENAME);
    }
    logger.info("User settings are stored in {}", userConfigurationFile);
    EnvironmentPropertyHolder.configurationDirectory.set(configurationDirectory);

    // Step 2 : Create an empty context

    // Step 2.1 : register the environment in the context
    context = new GenericWebApplicationContext();
    context.setDisplayName(name);
    context.getBeanFactory().registerSingleton(EnvironmentImpl.ENVIRONMENT_BEAN_NAME, this);

    // Step 2.2 : register the StarterMonitor object and the related bean post
    // processor
    starterMonitor.setEnvironment(this);
    context.getBeanFactory().registerSingleton(EnvironmentImpl.STARTER_MONITOR_BEAN_NAME, starterMonitor);
    context.getBeanFactory().addBeanPostProcessor(new StarterMonitorBeanPostProcessor(starterMonitor));

    starterMonitor.setMaxStep(9);

    try {

      starterMonitor.nextStep("Initial configuration");
      // Step 3 : prepare for autodetection mechanisms

      // Step 3.1 : path repositories and detected paths
      context.getBeanFactory().registerSingleton(EnvironmentImpl.PATH_REPOSITORY_BEAN_NAME, pathRepository);

      // Step 3.2 : URI searcher facility
      final URISearcher uriSearcher = new URISearcher();
      uriSearcher.setSearchPaths(pathRepository.getPaths());
      context.getBeanFactory().registerSingleton(EnvironmentImpl.URI_SEARCHER_BEAN_NAME, uriSearcher);

      // Step 3.3 : Scanning class path
      starterMonitor.nextStep("Scanning class path");
      final Comparator<URL> comparator = new Comparator<URL>() {

        @Override
        public int compare(final URL o1, final URL o2) {
          return o1.toExternalForm().compareTo(o2.toExternalForm());
        }
      };
      final JRegExPathMatcher<Set<URL>> componentsLog4jPropertiesMatcher = new JRegExPathMatcher<Set<URL>>(new jregex.Pattern(
          ".*-log4j-properties\\.properties$"), new TreeSet<URL>(comparator));
      final JRegExPathMatcher<Set<URL>> annotationScannerIgnoredPackageMatcher = new JRegExPathMatcher<Set<URL>>(new jregex.Pattern(
          ".*-ignorespackage\\.annotationscanner$"), new TreeSet<URL>(comparator));
      xmlContextMatcher = new JRegExPathMatcher<Set<URL>>(new jregex.Pattern(".*-spring-context\\.xml"), new TreeSet<URL>(
          comparator));

      final PathMatcherCollection matchers = new PathMatcherCollection(componentsLog4jPropertiesMatcher,
          annotationScannerIgnoredPackageMatcher, xmlContextMatcher);
      uriSearcher.search(matchers);

      starterMonitor.nextStep("Logger configuration");
      // Step 4 : update logging configuration for components
      final Properties log4jProperties = new Properties();
      for (final URL url : componentsLog4jPropertiesMatcher.getMatchedPaths()) {
        logger.info("Component Log4j properties found: {}", url);
        try (final InputStream is = url.openStream()) {
          final Properties properties = new Properties();
          properties.load(is);
          log4jProperties.putAll(properties);
        } catch (final IOException e) {
          logger.warn("Can not load properties from {}. Ignoring settings", url);
        }
      }
      log4jProperties.putAll(mainLog4jProperties);
      PropertyConfigurator.configure(log4jProperties);

      // Step 5 : annotation scanning
      starterMonitor.nextStep("Scanning classes for annotations");

      // Step 5.1 : detect packages to ignore while scanning for annotations
      final Set<String> ignoredPackages = new TreeSet<String>();
      for (final URL url : annotationScannerIgnoredPackageMatcher.getMatchedPaths()) {
        try (final BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()))) {
          String line;
          while ((line = input.readLine()) != null) {
            if (!line.isEmpty()) {
              ignoredPackages.add(line.trim());
            }
          }
        } catch (final IOException e) {
          if (logger.isWarnEnabled()) {
            logger.warn("An error occurred while reading content of " + url, e);
          }
        }
      }
      logger.info("Packages to ignore while scanning for annotations: {}", ignoredPackages);

      // Step 5.2 : scanning
      final AnnotationScannerImpl annotationScanner = new AnnotationScannerImpl();
      annotationScanner.scan(pathRepository.getPaths(), ignoredPackages);
      context.getBeanFactory().registerSingleton(EnvironmentImpl.ANNOTATION_SCANNER_BEAN_NAME, annotationScanner);

      // Step 6 : properties
      starterMonitor.nextStep("Building property registry");

      // Step 6.1 : initialize property registry
      final PropertyRegistry propertyRegistry = new PropertyRegistry(annotationScanner);
      context.getBeanFactory().registerSingleton(EnvironmentImpl.PROPERTY_REGISTRY_BEAN_NAME, propertyRegistry);

      // Step 6.3 : load properties from user configuration
      propertyRegistry.load(userConfigurationFile);

      // Step 6.4 : override properties
      propertyRegistry.overrideProperties(overridedConfigurationProperties);

      final PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer() {
        @Override
        protected String resolvePlaceholder(final String placeholder, final Properties props) {
          return propertyRegistry.getPropertyValue(placeholder);
        }
      };
      context.getBeanFactory().registerSingleton(EnvironmentImpl.CONFIGURATION_PROPERTIES_PLACE_HOLDER_BEAN_NAME, ppc);

      // Step 6 : annotations scanning

      // Step 6.3 : fill context with bean definitions
      final XmlBeanDefinitionReader xmlBeanReader = new XmlBeanDefinitionReader(context);
      final AnnotatedBeanDefinitionReader annotationBeanReader = new AnnotatedBeanDefinitionReader(context);

      // annotation configuration
      starterMonitor.nextStep("Registering annotated spring classes");
      final Map<String, Class<?>> classes = annotatedClassProvider.getClasses(this);

      for (final Entry<String, Class<?>> entry : classes.entrySet()) {
        if (!annotatedClassFilter.filterAnnotatedClass(entry.getValue())) {
          if (logger.isInfoEnabled()) {
            logger.info("Registering annotated class in spring context: {}", entry.getKey());
          }
          annotationBeanReader.registerBean(entry.getValue());
        }
      }

      // xml configuration
      starterMonitor.nextStep("Registering XML spring contexts");
      final TreeSet<URL> xmlContexts = new TreeSet<URL>(comparator);
      xmlContexts.addAll(xmlContextProvider.getUris(this));
      for (final URL url : xmlContexts) {
        logger.info("Reading beans definitions from XML for spring: {}", url);
        xmlBeanReader.loadBeanDefinitions(new UrlResource(url));
      }

      // direct input source
      starterMonitor.nextStep("Registering in memory XML spring contexts");
      // disable validation
      xmlBeanReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
      for (final InputSource inputSource : inMemoryXmlContextProvider.getInputSources(this)) {
        xmlBeanReader.loadBeanDefinitions(inputSource);
      }

      // Step 8 : context startup
      starterMonitor.nextStep("Starting context");

      context.registerShutdownHook();
      context.refresh();
      context.start();
    } finally {
      starterMonitor.done();

    }
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.Environment#stop()
   */
  @Override
  public void stop() {
    try {
      context.close();
      context.stop();
      stoppedLock.lock();
      try {
        stoppedCondition.signalAll();
      } finally {
        stoppedLock.unlock();
      }
    } catch (final Exception e) {
      logger.warn("Could not stop context without error", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return context.toString();
  }

  public void waitForStop() throws InterruptedException {
    stoppedLock.lock();
    try {
      stoppedCondition.await();
    } finally {
      stoppedLock.unlock();
    }
  }

}
