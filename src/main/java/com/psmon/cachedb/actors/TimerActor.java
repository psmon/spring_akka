package com.psmon.cachedb.actors;

import java.time.Duration;
import akka.actor.AbstractActorWithTimers;

  public class TimerActor extends AbstractActorWithTimers {
    
    private static Object TICK_KEY = "TickKey";
    private static final class FirstTick {
    }
    private static final class Tick {
    }

    public TimerActor() {
      getTimers().startSingleTimer(TICK_KEY, new FirstTick(), Duration.ofMillis(500));
    }

    @Override
    public Receive createReceive() {
      return receiveBuilder()
        .match(FirstTick.class, message -> {
          // do something useful here
          getTimers().startPeriodicTimer(TICK_KEY, new Tick(), Duration.ofSeconds(1));
        })
        .match(Tick.class, message -> {
            // do something useful here  
        })  
        .build();
    }
  }

