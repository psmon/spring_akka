package com.psmon.cachedb.actors.fsm;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import akka.japi.pf.UnitMatch;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;
import java.time.Duration;

public final class SetTarget  {
  private final ActorRef ref;

  public SetTarget(ActorRef ref) {
    this.ref = ref;
  }

  public ActorRef getRef() {
    return ref;
  }

  @Override
  public String toString() {
    return "SetTarget{" +
      "ref=" + ref +
      '}';
  }
}
