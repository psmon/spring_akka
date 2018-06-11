package com.psmon.cachedb.actors.persistence;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;

import java.io.Serializable;
import java.util.ArrayList;

class Evt implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String data;

    public Evt(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}

class ExampleState implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<String> events;

    public ExampleState() {
        this(new ArrayList<>());
    }

    public ExampleState(ArrayList<String> events) {
        this.events = events;
    }

    public ExampleState copy() {
        return new ExampleState(new ArrayList<>(events));
    }

    public void update(Evt evt) {
        events.add(evt.getData());
    }

    public int size() {
        return events.size();
    }

    @Override
    public String toString() {
        return events.toString();
    }
}

@Component
@Scope("prototype")
public class ExamplePersistentActor extends AbstractPersistentActor {
	  
    private ExampleState state = new ExampleState();
    
    private int snapShotInterval = 1000;

    public int getNumEvents() {
        return state.size();
    }

    @Override
    public String persistenceId() { return "ExamplePersistentActor-id-1"; }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
            .match(Evt.class, state::update)
            .match(SnapshotOffer.class, ss -> state = (ExampleState) ss.snapshot())
            .build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Cmd.class, c -> {
              final String data = c.getData();
              final Evt evt = new Evt(data + "-" + getNumEvents());
              persist(evt, (Evt e) -> {
                  state.update(e);
                  getContext().getSystem().eventStream().publish(e);
                  if (lastSequenceNr() % snapShotInterval == 0 && lastSequenceNr() != 0)
                      // IMPORTANT: create a copy of snapshot because ExampleState is mutable
                      saveSnapshot(state.copy());
              });
            })
            .matchEquals("print", s -> System.out.println(state) )	//이벤트를 재생
            .build();
    }	
}
