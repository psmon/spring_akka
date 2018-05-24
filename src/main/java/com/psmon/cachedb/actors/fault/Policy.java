package com.psmon.cachedb.actors.fault;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;


//복구정책을 정의합니다.
public class Policy {	
	public static SupervisorStrategy strategy1 = new OneForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES),		//복구시도제한 : 1분이내에 10번만 시도한다
		      DeciderBuilder
		           //다양한 예외에대한 가이드를 서비스코드와 분리할수가 있습니다.
		          .match(ArithmeticException.class, e -> SupervisorStrategy.resume())
		          .match(NullPointerException.class, e -> SupervisorStrategy.restart())
		          .match(IllegalArgumentException.class, e -> SupervisorStrategy.stop())
		          .matchAny(o -> SupervisorStrategy.escalate())
		          .build());
}
