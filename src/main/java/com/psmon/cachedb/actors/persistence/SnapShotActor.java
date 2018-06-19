package com.psmon.cachedb.actors.persistence;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor.Receive;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.Recovery;
import akka.persistence.SaveSnapshotFailure;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotMetadata;
import akka.persistence.SnapshotOffer;
import akka.persistence.SnapshotSelectionCriteria;

@Component
@Scope("prototype")
public class SnapShotActor extends AbstractPersistentActor {
	
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), "AbstractPersistentActor");	  
		
	private Object state;
	private int snapShotInterval = 5;
	
	private int msgCnt = 0;
	
    @Override
    public String persistenceId() { return "ExamplePersistentActor-id-1"; }
    
    @Override public Receive createReceiveRecover() {
	  return receiveBuilder().
	    match(SnapshotOffer.class, s -> {
	      state = s.snapshot();	//상태복원
	      log.info("상태복원");
	      // ...
	    }).
	    match(String.class, s -> {/* ...*/}).build();
	}
    
    @Override	//복구전략(스냅샷을 무시할수도 있음)
    public Recovery recovery() {
    	return Recovery.create(SnapshotSelectionCriteria.none());
    	/*
      return Recovery.create(
        SnapshotSelectionCriteria
          .create(457L, System.currentTimeMillis()));*/
    }
	 
	// 스냅샷을 지원하는 메시지 정의
	@Override public Receive createReceive() {
	  return receiveBuilder().
	    match(SaveSnapshotSuccess.class, ss -> {
	      SnapshotMetadata metadata = ss.metadata();	      
	      // ...
	    }).
	    match(SaveSnapshotFailure.class, sf -> {
	      SnapshotMetadata metadata = sf.metadata();
	      // ...
	    }).	      
	    match(String.class, cmd -> {
	    	log.info("EventFired:"+cmd);
	    	if(cmd.indexOf("print") ==0 ) {
	    		log.info("상태확인:"+state);	    		
	    	}else {
	    		msgCnt++;	    		
	    		state = cmd + "을 먹은 상태";		//커멘드에따른 상태변화	    		
	    		if (msgCnt % snapShotInterval == 0 ) {
		    		//이벤트가 ?회 발생할때마다,상태를 변경하고 스냅샷을 찍음 ( 순수스냅샷 테스트를 위해 persist기능을 뺏습니다.)	        		        	  		    				    		
		    		log.info("SaveSnapShot:" + state);
		    		saveSnapshot(state);	    		
		    	}	    		
	    	}
	    })	    
	    .build();
	}
}