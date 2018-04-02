package com.psmon.cachedb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

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
    

    @Autowired
    private Environment env;

    @Bean
    public ActorSystem actorSystem() {

        ActorSystem system = ActorSystem
            .create("AkkaTestApp", akkaConfiguration());
        springExtension.initialize(applicationContext);
        
        return system;
    }

    @Bean
    public Config akkaConfiguration() {
/*
 *  	
        String akkaip = env.getProperty("akka.remote.netty.tcp.ip");
        String port = env.getProperty("akka.remote.netty.tcp.port");
 
        // 기존 Akka설정과(application.conf) + 최종계산된 Spring의 설정(appplication.properties)를 머징시키는방법
        // AKKA에서 필요한 기능들 설정은 충분히한후, 변경요소를 Spring설정을 노출하여 변경하는 방식
        return ConfigFactory.load().withValue("akka.remote.netty.tcp.ip", ConfigValueFactory.fromAnyRef(akkaip))
                .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port));
*/
    	
    	
        return ConfigFactory.load();
    }

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
	}
	
}
	

	   
