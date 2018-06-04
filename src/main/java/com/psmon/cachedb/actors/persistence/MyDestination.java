package com.psmon.cachedb.actors.persistence;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;

@Component
@Scope("prototype")
public class MyDestination extends AbstractActor {
	  @Override
	  public Receive createReceive() {
	    return receiveBuilder()
	      .match(Msg.class, msg -> {
	    	//메시지를 받고 할일을 하고..
	    	//....	    	  
	    	  
	    	//수신 메시지 확인됨을 보냄..
	        getSender().tell(new Confirm(msg.deliveryId), getSelf());
	      })
	      .build();
	  }
}
