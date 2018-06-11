package com.psmon.cachedb.actortest;

import org.springframework.stereotype.Component;

import com.psmon.cachedb.extension.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;

@Component
public class ActorRouter extends ActorBase {
	
	public void runAll() {
		routerTest();			
	}
	
	protected void routerTest() {
	    new TestKit(system) {{
	    	final ActorRef workers = system.actorOf( ext.props("workers"),"workers");	    		    
	    	workers.tell( "hi1", getRef() );
		      // await the correct response
		    expectMsg(java.time.Duration.ofSeconds(1), "hi too");
		    
	    	workers.tell( "hi1", getRef() );
		      // await the correct response
		    expectMsg(java.time.Duration.ofSeconds(1), "hi too");
		    
		    workers.tell( "hi1", getRef() );
		    workers.tell( "hi1", getRef() );
		    workers.tell( "hi1", getRef() );
		    workers.tell( "hi1", getRef() );
		    workers.tell( "hi1", getRef() );
		    
		    for(int i=0;i<10;i++) workers.tell( "hi2", getRef() );		    		   
	    }};
	}

}
