package com.psmon.cachedb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.psmon.cachedb.extension.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

@SpringBootApplication
public class CachedbApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(CachedbApplication.class, args);
		
        ActorSystem system = context.getBean(ActorSystem.class);

        final LoggingAdapter log = Logging.getLogger(system, "Application");

        log.info("Starting up");

        SpringExtension ext = context.getBean(SpringExtension.class);
                
        ActorRef testActor = system.actorOf(ext.props("testActor"));
        
        testActor.tell("ready spring boot",null);        

	}
}
