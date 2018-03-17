package com.psmon.cachedb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

@Configuration
public class ApplicationConfig {
	
	  private final ApplicationContext applicationContext;
	  private final SpringExtension springAkkaExtension;

	  @Autowired
	  public ApplicationConfig(ApplicationContext applicationContext, SpringExtension springAkkaExtension) {
	    this.applicationContext = applicationContext;
	    this.springAkkaExtension = springAkkaExtension;	    
	  }

	  @Bean
	  public ActorSystem actorSystem() {
	    final ActorSystem system = ActorSystem.create("default", akkaConfiguration());
	    springAkkaExtension.setApplicationContext(applicationContext);
	    return system;
	  }

	  @Bean
	  public Config akkaConfiguration() {
	    return ConfigFactory.load();
	  }
	
	   @EventListener(ApplicationReadyEvent.class)
	   public void doSomethingAfterStartup() {
	  }
	   
	   
}
