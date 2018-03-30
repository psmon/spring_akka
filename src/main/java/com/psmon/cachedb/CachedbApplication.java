package com.psmon.cachedb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.psmon.cachedb.extension.SpringExtension;

import akka.actor.ActorRef;

import akka.actor.ActorSelection;
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
        
        //액터 생성... 
        ActorRef testActor = system.actorOf(ext.props("testActor"),"service1");
        
        ActorSelection testActor2 = system.actorSelection("user/service1");
        
        ActorSelection testActorRemote = system.actorSelection("akka.tcp://app@localhost:2552/user/service1");
        
        //참조를 통한 전송 ( 로컬 객체관리에 유리 )
        testActor.tell("ready spring boot",null);
        
        //액터 셀렉션을 통한 전송(
        testActor2.tell("ready spring boot -again", null);
        
        //리모트를 통한 전송
        testActorRemote.tell("ready spring boot -again too", null);
        
        
	}
}
