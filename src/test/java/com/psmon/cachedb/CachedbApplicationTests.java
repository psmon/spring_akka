package com.psmon.cachedb;

//import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import com.psmon.cachedb.dbtest.JPAFirst;
import com.psmon.cachedb.extension.SpringExtension;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.psmon.cachedb.actortest.ActorFSM;
import com.psmon.cachedb.actortest.ActorFault;
import com.psmon.cachedb.actortest.ActorFirst;
import com.psmon.cachedb.actortest.ActorPersistence;
import com.psmon.cachedb.actortest.ActorRouter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CachedbApplicationTests {
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	ActorPersistence actorPersitence;
	
	@Autowired
	JPAFirst		jpaFirst;
	
	@Autowired
	ActorFault	actorFault;
	
	@Autowired
	ActorFirst		actorFirst;
	
	@Autowired
	ActorFSM		actorFSM;
	
	@Autowired
	ActorRouter	actorRouter;
	
	
	@Test
	public void contextLoads() throws Exception {		
		ActorSystem system = context.getBean(ActorSystem.class);
		SpringExtension ext = context.getBean(SpringExtension.class);
		
		//Init
		actorFirst.setSystem(system);
		actorFirst.setExt(ext);
		actorFSM.setSystem(system);
		actorFSM.setExt(ext);
		actorRouter.setSystem(system);
		actorRouter.setExt(ext);
		actorPersitence.setSystem(system);
		actorPersitence.setExt(ext);
		actorFault.setSystem(system);
		actorFault.setExt(ext);
		
		//필요한 테스트만 선택하여 테스트하기....		
						
		// JPA(DB) TEST
		//jpaFirst.runAll();
		//jpaFirst.fsmDBWriteTest(system, ext);		
		
		// ACTOR 입문
		//actorFirst.runAll();
		
		// ACTOR 상태머신
		//actorFSM.runAll();
		
		// ACTOR 라우터
		//actorRouter.runAll();		
		
		// ACTOR 영속성				
		actorPersitence.runAll();
				
		// 장애처리 대응
		//actorFault.runAll();
		
		TestKit.shutdownActorSystem(system , scala.concurrent.duration.Duration.apply(5, TimeUnit.SECONDS ) ,true );		
	}
}
