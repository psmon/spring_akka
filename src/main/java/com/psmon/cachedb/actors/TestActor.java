package com.psmon.cachedb.actors;

import java.util.Arrays;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import akka.stream.*;
import akka.stream.javadsl.*;

@Component
@Scope("prototype")
public class TestActor extends UntypedActor {
    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), "TestActor");

    @Override
    public void onReceive(Object message) throws Exception {    	    	
    	if(message instanceof String) {	//String뿐만 아니라 모든 Java객체 통신가능
    		log.info("Incommessage {}", message);
    		sender().tell("너의 메시지에 응답을함", ActorRef.noSender());    		
    	}else {
    		log.info("Unhandle Message {}", message);    		
    	}
    	
    }
}
