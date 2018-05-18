package com.psmon.cachedb.actors.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.psmon.cachedb.actors.TestActor;
import com.psmon.cachedb.extension.SpringExtension;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;

@Component
@Scope("prototype")
public class Workers extends AbstractActor {
    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), "Workers");
    
    @Autowired
    private ApplicationContext context;    
    
    private ActorRef roundrobinpool = null;
        
    @Override public void preStart() {
		log.info("Workers::preStart");
		SpringExtension ext = context.getBean(SpringExtension.class);		
		roundrobinpool = getContext().actorOf( FromConfig.getInstance().props( ext.props("testActor") ) , "router1");		
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
		.matchEquals("hi1",s->{
			log.info("Workers::Received String message: {}", s);
			roundrobinpool.tell(s, getSender() );					
		})
		.matchAny(o -> log.info("received unknown message"))
		.build();
	}
}
