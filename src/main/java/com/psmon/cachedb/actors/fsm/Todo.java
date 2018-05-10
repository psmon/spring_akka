package com.psmon.cachedb.actors.fsm;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import akka.japi.pf.UnitMatch;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.time.Duration;

//states
enum State {
	Idle, Active
}

//state data
interface Data {
}

enum Uninitialized implements Data {
	Uninitialized
}

final class Todo implements Data {
	private final ActorRef target;
	private final List<Object> queue;
	
	public Todo(ActorRef target, List<Object> queue) {
	 this.target = target;
	 this.queue = queue;
	}
	
	public ActorRef getTarget() {
	 return target;
	}
	
	public List<Object> getQueue() {
	 return queue;
	}
	
	@Override
	public String toString() {
	 return "Todo{" +
	   "target=" + target +
	   ", queue=" + queue +
	   '}';
	}
	
	public Todo addElement(Object element) {
	 List<Object> nQueue = new LinkedList<>(queue);
	 nQueue.add(element);
	 return new Todo(this.target, nQueue);
	}
	
	public Todo copy(List<Object> queue) {
	 return new Todo(this.target, queue);
	}
	
	public Todo copy(ActorRef target) {
	 return new Todo(target, this.queue);
	}
}