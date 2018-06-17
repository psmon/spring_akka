package com.psmon.cachedb.actortest;

import org.springframework.stereotype.Component;

import com.psmon.cachedb.actors.persistence.Cmd;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;

@Component
public class ActorPersistence extends ActorBase {
	
	public void runAll() {
		//persistenceMessage();
		//persistenceEventSrc();		
		persistenceSnapShot();
		
	}
	
	protected void persistenceSnapShot()  {
	    new TestKit(system) {{
	    	ActorRef probe = getRef();	    	
	    	
			Props snapShotActorProp = ext.props("snapShotActor");			
			System.out.println("snapShotActor 액터생성");
			ActorRef snapShotActor = system.actorOf(snapShotActorProp, "snapShotActor");
			
			System.out.println("event 생성");
			snapShotActor.tell("커피", ActorRef.noSender());
			snapShotActor.tell("사탕", ActorRef.noSender());
			snapShotActor.tell("커피", ActorRef.noSender());
			snapShotActor.tell("스테이크", ActorRef.noSender());
			snapShotActor.tell("라면", ActorRef.noSender()); // <-- 복구기대 상태
			snapShotActor.tell("사탕", ActorRef.noSender());  
			snapShotActor.tell("커피", ActorRef.noSender());

			System.out.println("상태확인");
			snapShotActor.tell( "print" , ActorRef.noSender());			
			expectNoMessage(java.time.Duration.ofSeconds(1));
			
			System.out.println("snapShotActor 종료또는 비정상종료");
			snapShotActor.tell( akka.actor.PoisonPill.getInstance() , ActorRef.noSender());			
			expectNoMessage(java.time.Duration.ofSeconds(1));
			
			System.out.println("snapShotActor 마지막 상태 확인");
			ActorRef snapShotActo2 = system.actorOf(snapShotActorProp, "eventActor");
			
			System.out.println("상태 복원확인");
			snapShotActo2.tell( "print" , ActorRef.noSender());
			
			expectNoMessage(java.time.Duration.ofSeconds(1));				       
			
	    }};
	}

	
	protected void persistenceEventSrc()  {
	    new TestKit(system) {{
	    	ActorRef probe = getRef();	    	
	    	
			Props examplePersistentActor = ext.props("examplePersistentActor");
			
			System.out.println("eventActor 액터생성");
			ActorRef eventActor = system.actorOf(examplePersistentActor, "eventActor");
			
			System.out.println("event 생성");
			eventActor.tell(new Cmd("test1"), ActorRef.noSender());
			eventActor.tell(new Cmd("test2"), ActorRef.noSender());
			eventActor.tell(new Cmd("test3"), ActorRef.noSender());
			eventActor.tell(new Cmd("test4"), ActorRef.noSender());
			
			System.out.println("event 재생");
			eventActor.tell( "print" , ActorRef.noSender());			
			expectNoMessage(java.time.Duration.ofSeconds(1));
			
			System.out.println("eventActor 종료또는 비정상종료");
			eventActor.tell( akka.actor.PoisonPill.getInstance() , ActorRef.noSender());			
			expectNoMessage(java.time.Duration.ofSeconds(1));
			
			System.out.println("eventActor 재생성");
			ActorRef eventActor2 = system.actorOf(examplePersistentActor, "eventActor");
			
			System.out.println("event 복원확인");
			eventActor2.tell( "print" , ActorRef.noSender());
			
			expectNoMessage(java.time.Duration.ofSeconds(1));				       
			
	    }};
	}

	protected void persistenceMessage()  {
	    new TestKit(system) {{
	    	ActorRef probe = getRef();
	    	//메시지 목적대상 액터 생성
	    	final ActorRef myDestination = system.actorOf(ext.props("myDestination"),"myDes");	    	
	    	final ActorSelection myDes = system.actorSelection("/user/myDes");
	    	
	    	//재전송가능한 메시지 발송기 생성 ( 옵션은 목적대상 액터선택자 ) 
	        final ActorRef myPersistentActor = system.actorOf( ext.props("myPersistentActor",myDes ), "myPersistentActor"  );
	        
	        for(int i=0;i<10;i++) {
	        	myPersistentActor.tell( "hi " + i ,ActorRef.noSender() );	        	
	        }
	        
	        //장애 시나리오를 가정하고,메시지가 모두 처리되는 시간 25초로 가정
	        expectNoMessage(java.time.Duration.ofSeconds(25));	        
	    }};
	}	

}
