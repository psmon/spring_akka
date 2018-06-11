package com.psmon.cachedb.actortest;

import java.util.LinkedList;

import org.springframework.stereotype.Component;

import com.psmon.cachedb.actors.fsm.Batch;
import com.psmon.cachedb.actors.fsm.Flush;
import com.psmon.cachedb.actors.fsm.Queue;
import com.psmon.cachedb.actors.fsm.SetTarget;
import com.psmon.cachedb.extension.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;

@Component
public class ActorFSM  extends ActorBase  {
	
	public void runAll() {
		
	}
	
	protected void fsmTest(ActorSystem system,SpringExtension ext) {
	    new TestKit(system) {
		{
	        final ActorRef buncher =
	          system.actorOf(ext.props("buncher"));
	        
	        final ActorRef probe = getRef();
	        
	        buncher.tell(new SetTarget(probe), probe);
	        buncher.tell(new Queue(42), probe);
	        buncher.tell(new Queue(43), probe);
	        LinkedList<Object> list1 = new LinkedList<>();
	        list1.add(42);
	        list1.add(43);
	        expectMsgEquals(new Batch(list1));
	        buncher.tell(new Queue(44), probe);

			buncher.tell(Flush.Flush , probe);
	        buncher.tell(new Queue(45), probe);
	        LinkedList<Object> list2 = new LinkedList<>();
	        list2.add(44);
	        expectMsgEquals(new Batch(list2));
	        LinkedList<Object> list3 = new LinkedList<>();
	        list3.add(45);
	        expectMsgEquals(new Batch(list3));
	        system.stop(buncher);
	      }};
	}

}
