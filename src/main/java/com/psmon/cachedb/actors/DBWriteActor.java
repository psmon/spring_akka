package com.psmon.cachedb.actors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.psmon.cachedb.data.primary.ItemBuyLog;
import com.psmon.cachedb.data.primary.ItemBuyLogRepository;

import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import akka.dispatch.BatchingExecutor.Batch;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import akka.stream.*;
import akka.stream.javadsl.*;

@Component
@Scope("prototype")
public class DBWriteActor extends AbstractActor{
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), "DBWriteActor");
    
	@Autowired
	ItemBuyLogRepository itemBuyLogRepository;
    
    @Override
    public Receive createReceive() {
      return receiveBuilder()
        .match(  Batch.class , s -> {
          log.info("Received ItemBuyLog message: {}", s.size()  );
          
          //Iterator<ItemBuyLog> myIterator = s.iterator();
          List<ItemBuyLog> insertList = new ArrayList<>();
          s.forEach( item-> {
        	  //Todo: 거지같은 이터레이터~        	          	  
          });          
          //itemBuyLogRepository.save( s )          
        })
        .matchAny(o -> log.info("received unknown message"))
        .build();
    }

}
