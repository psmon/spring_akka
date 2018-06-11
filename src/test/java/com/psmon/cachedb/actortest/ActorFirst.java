package com.psmon.cachedb.actortest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.psmon.cachedb.actors.TestActor;
import com.psmon.cachedb.extension.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import akka.testkit.javadsl.TestKit;

@Component
public class ActorFirst extends ActorBase {
	
	public void runAll() {
		actorTest1();
		actorTest2();
		actorTest3();
		actorTest4();
		
	}
	
	protected void actorTest1() {
		ActorRef testActor = system.actorOf(ext.props("testActor")
				.withDispatcher("blocking-io-dispatcher"),
				"service1");
		
		for(int i=0;i<10;i++)
			testActor.tell("test message wiht dispatcher",null);
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected void actorTest2() {			
	    new TestKit(system) {{
	    	final ActorRef testActor = system.actorOf( ext.props("testActor"),"test1");	    		    
			testActor.tell( "hi", getRef() );			
		      // await the correct response
		    expectMsg(java.time.Duration.ofSeconds(1), "hi too");
		    
	    }};
	    
	}
	
	protected void actorTest3() {
	    new TestKit(system) {{
	    	final ActorRef testActor = system.actorOf( ext.props("testActor"),"test2");	    	
	    	final TestKit probe = new TestKit(system);	//추가 테스트 객체생성..	    	
		    within(java.time.Duration.ofSeconds(3), () -> {
		    	testActor.tell( "hi", probe.getRef() );
		    	//메시지가 오기를 기다림
		    	awaitCond(probe::msgAvailable);
		    	//메시지검사
		    	probe.expectMsg(java.time.Duration.ofSeconds(0), "hi too");
		    	return null;
		    });
	    }};		
	}
	
	protected void actorTest4() {
	    final TestActorRef<TestActor> testB = TestActorRef.create(system, ext.props("testActor") , "test3");
	    final CompletableFuture<Object> future = PatternsCS.ask(testB, "hi", 3000).toCompletableFuture();
	    assertTrue(future.isDone());
	    try {
			assertEquals("hi too", future.get() );
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	


}
