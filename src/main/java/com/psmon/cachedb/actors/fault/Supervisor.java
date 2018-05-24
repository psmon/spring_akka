package com.psmon.cachedb.actors.fault;

import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import akka.actor.SupervisorStrategy;
import akka.actor.Props;

import akka.event.Logging;
import akka.event.LoggingAdapter;

@Component("Supervisor")
@Scope("prototype")
public class Supervisor extends AbstractActor{
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), "Supervisor");
    
	@Override
    public SupervisorStrategy supervisorStrategy() {
		//자신의 아이가 문제가 생길때 복구전략
      return Policy.strategy1;
    }
	
    @Override
    public Receive createReceive() {
      return receiveBuilder()
    	//부모가 아이를 책임지는구조로, 생성도 부모만 할수가 있습니다.(여기서는 생성의기능만 존재)
        .match(Props.class, props -> {
          getSender().tell(getContext().actorOf(props), getSelf());
        })
        .build();
    }    
}
