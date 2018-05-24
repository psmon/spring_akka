package com.psmon.cachedb.actors.fault;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

@Component("BadChild")
@Scope("prototype")
public class BadChild extends AbstractActor {
	//우리가 생성한 클래스는 항상 착하지 않다.
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), "BadChild");	
	int state = 0;
  
	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(Exception.class, exception -> {
				log.error(exception.toString());
				/*
				전통적인 익센셥처리의 문제는, 익센셥은 스택구조이기때문에
				자기자신에게 발생한 익센셥을 해결하지못하는데 있고
				서비스코드에 처리방법이 썩임으로 예외처리코드가 서비스코드를 덮는데 있다.
				Akka의 예외처리는 예외가발생하도록 놔두고, 예외처리정책을 우아하게 분리할수가 있다.
				이것이 Let it Crash 전략이다.
				*/
				throw exception;
			})
			.match(Integer.class, i -> state = i)
			.matchEquals("get", s -> getSender().tell(state, getSelf()))
			.build();
	}
}
