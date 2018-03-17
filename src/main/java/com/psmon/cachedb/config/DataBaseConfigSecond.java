package com.psmon.cachedb.config;

import java.util.HashMap;
import java.util.Map;

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
  entityManagerFactoryRef = "secondEntityManagerFactory",
  transactionManagerRef = "secondTransactionManager",
  basePackages = {"com.psmon.cachedb.data.second"})
public class DataBaseConfigSecond {
	
	  @Bean(name = "secondDataSource")
	  @ConfigurationProperties(prefix = "app.datasource.second")
	  public DataSource secondDataSource() {
	    return DataSourceBuilder.create().build();
	  }

	  @Bean(name = "secondEntityManagerFactory")
	  public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(
			  EntityManagerFactoryBuilder builder, 
			  @Qualifier("secondDataSource") DataSource secondDataSource,
			  Environment env) {
		Map<String, Object> properties = new HashMap<String, Object>();		
		properties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("second.hibernate.hbm2ddl.auto"));
		properties.put("hibernate.dialect", env.getRequiredProperty("second.hibernate.dialect"));
		properties.put("hibernate.show_sql", env.getRequiredProperty("second.hibernate.show_sql"));
		properties.put("hibernate.use_sql_comments", env.getRequiredProperty("second.hibernate.use_sql_comments"));
		properties.put("hibernate.format_sql", env.getRequiredProperty("second.hibernate.format_sql"));
		
		
	    return builder
	      .dataSource(secondDataSource)
	      .packages("com.psmon.cachedb.data.second")
	      .persistenceUnit("second")
	      .properties(properties)
	      .build();
	  }

	  @Bean(name = "secondTransactionManager")
	  public PlatformTransactionManager secondTransactionManager(
	    @Qualifier("secondEntityManagerFactory") EntityManagerFactory secondEntityManagerFactory) {
	    return new JpaTransactionManager(secondEntityManagerFactory);
	  }
	
}
