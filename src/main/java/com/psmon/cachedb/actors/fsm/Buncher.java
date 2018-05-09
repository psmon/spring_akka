package com.psmon.cachedb.actors.fsm;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import akka.japi.pf.UnitMatch;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Scope("prototype")
public class Buncher extends AbstractFSM<State, Data> {	
	final static  com.psmon.cachedb.actors.fsm.State Idle = com.psmon.cachedb.actors.fsm.State.Idle;
	final static  com.psmon.cachedb.actors.fsm.Uninitialized Uninitialized = com.psmon.cachedb.actors.fsm.Uninitialized.Uninitialized;	
	final static public com.psmon.cachedb.actors.fsm.State Active = com.psmon.cachedb.actors.fsm.State.Active;

	{
	    startWith(Idle, Uninitialized);
	    when(Idle,
	      matchEvent(SetTarget.class, Uninitialized.class,
	        (setTarget, uninitialized) ->
	          stay().using(new Todo(setTarget.getRef(), new LinkedList<>()))));
	
	    onTransition(
	      matchState(Active, Idle, () -> {
	        // reuse this matcher
	        final UnitMatch<Data> m = UnitMatch.create(
	          matchData(Todo.class,
	            todo -> todo.getTarget().tell(new Batch(todo.getQueue()), getSelf())));
	        m.match(stateData());
	      }).
	      state(Idle, Active, () -> {/* Do something here */}));
	
	    when(Active, Duration.ofSeconds(1L),
	      matchEvent(Arrays.asList(Flush.class, StateTimeout()), Todo.class,
	        (event, todo) -> goTo(Idle).using(todo.copy(new LinkedList<>()))));
	
	    whenUnhandled(
	      matchEvent(Queue.class, Todo.class,
	        (queue, todo) -> goTo(Active).using(todo.addElement(queue.getObj()))).
	        anyEvent((event, state) -> {
	          log().warning("received unhandled request {} in state {}/{}",
	            event, stateName(), state);
	          return stay();
	        }));
	
	    initialize();
	  }
}