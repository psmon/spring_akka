package com.psmon.cachedb.actortest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Component;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.pattern.PatternsCS;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;

@Component
public class ActorFault  extends ActorBase  {
	
	public void runAll()  {
		supervisorTest();		
	}
	
	protected void supervisorTest()  {
		try {
			new TestKit(system) {{
				Props superprops = ext.props("Supervisor");
				ActorRef supervisor = system.actorOf(superprops, "supervisor");
				ActorRef probe = getRef();
				
				//나쁜아이를 허용하는 전략으로 부모(감독)를 통해 아이를 생성합니다.
				final CompletableFuture<Object> future = PatternsCS.ask(supervisor, ext.props("BadChild") , 5000).toCompletableFuture();
				ActorRef badChild = (ActorRef)future.get();
				
				//나쁜아이의 상태를 42로 만든다...
				badChild.tell(42, ActorRef.noSender());
				
				//나쁜아이의 상태를 물어보고, probe에 전달하도록한다
				badChild.tell("get", probe);
				
				//prove에 저장된 메시지가 42인지 확인한다.
				expectMsgEquals(42);
				
				// ArithmeticException 예외는 그냥 진행하도록 정의를 하였다.
				badChild.tell( new ArithmeticException(), ActorRef.noSender() );
				
				//아이가 살아있는지 확인...
				badChild.tell("get", probe);
				expectMsgEquals(42);
				
				// NullPointerException 예외는 아이를 다시 초기화하도록 정의하였다.
				badChild.tell( new NullPointerException(), ActorRef.noSender() );
				
				//아이가 초기화 되었는지 확인 ( 초기상태 0)
				badChild.tell("get", probe);
				expectMsgEquals(0);
				
				//액터를 확인하기위한 Util 액터로, 죽은지 여부를 확인할수가 있습니다.
				TestProbe probe2 = new TestProbe(system);
				probe2.watch(badChild);
				
				// IllegalArgumentException 익센셥이 발생하면 아이를 보내기로 결정하였다.
				badChild.tell( new IllegalArgumentException(), ActorRef.noSender()  );
										
				// 아이가 사라진지 체크...
				probe2.expectMsgClass(Terminated.class);
				
			}};
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
