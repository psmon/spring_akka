package com.psmon.cachedb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import com.psmon.cachedb.extension.SpringExtension;

import akka.actor.ActorSystem;

@Configuration
@Lazy
@ComponentScan(basePackages = { "com.psmon.cachedb.services",
    "com.psmon.cachedb.actors", "com.psmon.cachedb.extension" })
public class ApplicationConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpringExtension springExtension;

    @Bean
    public ActorSystem actorSystem() {

        ActorSystem system = ActorSystem
            .create("AkkaTestApp", akkaConfiguration());
        springExtension.initialize(applicationContext);
        
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
	

	   
