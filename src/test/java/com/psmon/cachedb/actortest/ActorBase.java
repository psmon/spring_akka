package com.psmon.cachedb.actortest;

import com.psmon.cachedb.extension.SpringExtension;
import akka.actor.ActorSystem;

public class ActorBase {
	
	public void runAll() {
		
	}
	
	protected ActorSystem system;
	protected SpringExtension ext;
	
	public ActorSystem getSystem() {
		return system;
	}
	public void setSystem(ActorSystem system) {
		this.system = system;
	}
	public SpringExtension getExt() {
		return ext;
	}
	public void setExt(SpringExtension ext) {
		this.ext = ext;
	}
	

}
