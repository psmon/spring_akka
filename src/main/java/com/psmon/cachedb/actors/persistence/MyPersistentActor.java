package com.psmon.cachedb.actors.persistence;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.persistence.AbstractPersistentActorWithAtLeastOnceDelivery;
import akka.persistence.Recovery;

class Msg implements Serializable {
	  private static final long serialVersionUID = 1L;
	  public final long deliveryId;
	  public final String s;

	  public Msg(long deliveryId, String s) {
	    this.deliveryId = deliveryId;
	    this.s = s;
	  }
	}

class Confirm implements Serializable {
  private static final long serialVersionUID = 1L;
  public final long deliveryId;

  public Confirm(long deliveryId) {
    this.deliveryId = deliveryId;
  }
}


class MsgSent implements Serializable {
  private static final long serialVersionUID = 1L;
  public final String s;

  public MsgSent(String s) {
    this.s = s;
  }
}
class MsgConfirmed implements Serializable {
  private static final long serialVersionUID = 1L;
  public final long deliveryId;

  public MsgConfirmed(long deliveryId) {
    this.deliveryId = deliveryId;
  }
}

@Component
@Scope("prototype")
public class MyPersistentActor extends AbstractPersistentActorWithAtLeastOnceDelivery {
	  private final ActorSelection destination;
	  
	  private int	msgCnt = 0;	  

	  public MyPersistentActor(ActorSelection destination) {		  
	      this.destination =destination;
	  }

	  @Override public String persistenceId() {
	    return "persistence-id";		//영속성을 위한 고유 저장소 ID
	  }

	  @Override
	  public Receive createReceive() {
	    return receiveBuilder().
	      match(String.class, s -> {
	    	//메시지가 전송중
	        persist(new MsgSent(s), evt -> updateState(evt));	        	        
	      }).
	      match(Confirm.class, confirm -> {
	    	this.msgCnt++;
	    	  
	    	if(this.msgCnt % 2 == 0) {
		    	//의도적으로 짝수메시지만 확인함...
		        persist(new MsgConfirmed(confirm.deliveryId), evt -> updateState(evt));		        
		        System.out.println("msg ok :" + confirm.deliveryId  );
	    	}else {
	    		//의도적으로 확인을 처리를 드롭...
	    		System.out.println("drop msg for test:" + confirm.deliveryId );
	    	}
	      }).
	      build();
	  }

	  @Override
	  public Receive createReceiveRecover() {
	    return receiveBuilder().
	        match(Object.class, evt -> updateState(evt)).build();
	  }

	  void updateState(Object event) {
	    if (event instanceof MsgSent) {
	      final MsgSent evt = (MsgSent) event;
	      deliver(destination, deliveryId -> new Msg(deliveryId, evt.s));
	    } else if (event instanceof MsgConfirmed) {
	      final MsgConfirmed evt = (MsgConfirmed) event;
	      confirmDelivery(evt.deliveryId);
	    }
	  }
}

