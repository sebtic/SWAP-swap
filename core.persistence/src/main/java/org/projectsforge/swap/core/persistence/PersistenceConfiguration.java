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

import java.beans.PropertyVetoException;
import java.io.File;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.projectsforge.swap.core.environment.impl.EnvironmentPropertyHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * The Class PersistenceConfiguration.
 * 
 * @author Sébastien Aupetit
 */
@Configuration
@EnableJpaRepositories()
@EnableTransactionManagement
public class PersistenceConfiguration {

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.transaction.annotation.TransactionManagementConfigurer
   * #annotationDrivenTransactionManager()
   */

  /*
   * @Override public PlatformTransactionManager
   * annotationDrivenTransactionManager() { return
   * transactionManager(entityManagerFactory, sessionFactory) //return new
   * DataSourceTransactionManager(dataSource()); }
   */

  /**
   * Data source.
   * 
   * @return the data source
   * @throws PropertyVetoException
   */
  @Bean
  public DataSource dataSource() throws PropertyVetoException {
    final String url = "jdbc:h2:" + EnvironmentPropertyHolder.configurationDirectory.get()
        + File.separator + PersistencePropertyHolder.databaseName.get() + ";TRACE_LEVEL_FILE=2";

    /*
     * final SimpleDriverDataSource dmds = new SimpleDriverDataSource(); //
     * dmds.setDriverClassName("org.h2.Driver");
     * dmds.setDriverClass(org.h2.Driver.class); dmds.setUrl(url);
     * dmds.setUsername("user"); dmds.setPassword("");
     */

    final ComboPooledDataSource cpds = new ComboPooledDataSource();
    cpds.setDriverClass("org.h2.Driver");
    cpds.setJdbcUrl(url);
    cpds.setUser("user");
    cpds.setPassword("");
    cpds.setMinPoolSize(5);
    cpds.setMaxPoolSize(20);
    cpds.setInitialPoolSize(5);
    cpds.setAcquireIncrement(1);
    cpds.setMaxStatements(50);

    return cpds;

  }

  /**
   * Entity manager factory.
   * 
   * @param jpaVendorAdapter the jpa vendor adapter
   * @param dataSource the data source
   * @return the entity manager factory
   * @throws PropertyVetoException
   */

  @Bean
  public EntityManagerFactory entityManagerFactory() throws PropertyVetoException {
    final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(dataSource());
    factory.setPersistenceUnitName("SWAP");
    factory.setJpaVendorAdapter(jpaVendorAdapter());
    factory.setPersistenceUnitPostProcessors(entityScannerPersistenceUnitPostProcessor());
    factory.getJpaPropertyMap().put("hibernate.globally_quoted_identifiers",
        PersistencePropertyHolder.hibernateGloballyQuotedIdentifiers.getHeldValue());
    factory.getJpaPropertyMap().put("hibernate.ejb.naming_strategy",
        "org.projectsforge.swap.core.persistence.NamespaceNamingStrategy");
    factory.getJpaPropertyMap().put("hibernate.cglib.use_reflection_optimizer", "true");

    factory.afterPropertiesSet();
    return factory.getObject();
  }

  /**
   * Entity scanner persistence unit post processor.
   * 
   * @return the entity scanner persistence unit post processor
   */
  @Bean
  EntityScannerPersistenceUnitPostProcessor entityScannerPersistenceUnitPostProcessor() {
    return new EntityScannerPersistenceUnitPostProcessor();
  }

  /**
   * Some jpa dialect.
   * 
   * @return the jpa dialect
   */

  @Bean
  JpaDialect jpaDialect() {
    return new HibernateJpaDialect();
  }

  /**
   * Jpa vendor adapter.
   * 
   * @param showSql the show sql
   * @return the jpa vendor adapter
   */
  @Bean
  JpaVendorAdapter jpaVendorAdapter() {
    final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setGenerateDdl(true);
    jpaVendorAdapter.setShowSql(PersistencePropertyHolder.hibernateShowSql.get());
    jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
    jpaVendorAdapter.setDatabase(Database.H2);
    return jpaVendorAdapter;
  }

  /**
   * Persistence annotation bean post processor.
   * 
   * @return the persistence annotation bean post processor
   */
  @Bean
  PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
    return new PersistenceAnnotationBeanPostProcessor();
  }

  /**
   * Persistence exception translation post processor.
   * 
   * @return the persistence exception translation post processor
   */

  @Bean
  PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  /**
   * Transaction manager.
   * 
   * @param entityManagerFactory the entity manager factory
   * @return the platform transaction manager
   * @throws PropertyVetoException
   */
  @Bean
  public PlatformTransactionManager transactionManager() throws PropertyVetoException {
    final JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory());
    return txManager;

  }
}
