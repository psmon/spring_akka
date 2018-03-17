package com.psmon.cachedb.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
  basePackages = {"com.psmon.cachedb.data.primary"})
public class DataBaseConfigPrimary {

	  @Primary
	  @Bean(name = "dataSource")
	  @ConfigurationProperties(prefix = "app.datasource.primary")
	  public DataSource dataSource() {
	    return DataSourceBuilder.create().build();
	  }

	  @Primary
	  @Bean(name = "entityManagerFactory")	  
	  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			  EntityManagerFactoryBuilder builder, 
			  @Qualifier("dataSource") DataSource dataSource,
			  Environment env) {
		Map<String, Object> properties = new HashMap<String, Object>();		
		properties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("primary.hibernate.hbm2ddl.auto"));
		properties.put("hibernate.dialect", env.getRequiredProperty("primary.hibernate.dialect"));
		
		properties.put("hibernate.show_sql", env.getRequiredProperty("primary.hibernate.show_sql"));
		properties.put("hibernate.use_sql_comments", env.getRequiredProperty("primary.hibernate.use_sql_comments"));
		properties.put("hibernate.format_sql", env.getRequiredProperty("primary.hibernate.format_sql"));				

	    return builder
	      .dataSource(dataSource)
	      .packages("com.psmon.cachedb.data.primary")
	      .persistenceUnit("primary")
	      .properties(properties)
	      .build();
	  }

	  @Primary
	  @Bean(name = "transactionManager")
	  public PlatformTransactionManager transactionManager(
	    @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
	    return new JpaTransactionManager(entityManagerFactory);
	  }

}
