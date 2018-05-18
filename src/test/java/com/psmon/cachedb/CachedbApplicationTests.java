package com.psmon.cachedb;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.psmon.cachedb.actors.TestActor;
import com.psmon.cachedb.data.primary.GameItem;
import com.psmon.cachedb.data.primary.GameItemRepository;
import com.psmon.cachedb.data.primary.ItemBuyLog;
import com.psmon.cachedb.data.primary.ItemBuyLogRepository;
import com.psmon.cachedb.data.primary.UserInfo;
import com.psmon.cachedb.data.primary.UserInfoRepository;
import com.psmon.cachedb.extension.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import akka.testkit.javadsl.TestKit;
import com.psmon.cachedb.actors.fsm.*;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import akka.japi.pf.UnitMatch;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CachedbApplicationTests {


	@Autowired
	GameItemRepository gameItemRepository;
	
	@Autowired
	ItemBuyLogRepository itemBuyLogRepository;
	
	@Autowired
	UserInfoRepository userInfoRepository;
	
	@Autowired
	ApplicationContext context;
	
	@Test
	public void contextLoads() {
		ActorSystem system = context.getBean(ActorSystem.class);
		SpringExtension ext = context.getBean(SpringExtension.class);		
		//actorTest2(system,ext);		
		//actorTest3(system,ext);
		//actorTest4(system,ext);		
		//fsmTest(system,ext);
		routerTest(system,ext);
		TestKit.shutdownActorSystem(system , scala.concurrent.duration.Duration.apply(5, TimeUnit.SECONDS ) ,true );		
	}
	
	protected void routerTest(ActorSystem system,SpringExtension ext) {
	    new TestKit(system) {{
	    	final ActorRef workers = system.actorOf( ext.props("workers"),"workers");	    		    
	    	workers.tell( "hi1", getRef() );
		      // await the correct response
		    expectMsg(java.time.Duration.ofSeconds(1), "hi too");
		    
	    	workers.tell( "hi1", getRef() );
		      // await the correct response
		    expectMsg(java.time.Duration.ofSeconds(1), "hi too");					    
	    }};
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
	
		
	protected void actorTest2(ActorSystem system,SpringExtension ext) {			
	    new TestKit(system) {{
	    	final ActorRef testActor = system.actorOf( ext.props("testActor"),"test1");	    		    
			testActor.tell( "hi", getRef() );			
		      // await the correct response
		    expectMsg(java.time.Duration.ofSeconds(1), "너의 메시지에 응답을함");
		    
	    }};
	    
	}
	
	protected void actorTest3(ActorSystem system,SpringExtension ext) {
	    new TestKit(system) {{
	    	final ActorRef testActor = system.actorOf( ext.props("testActor"),"test2");	    	
	    	final TestKit probe = new TestKit(system);	//추가 테스트 객체생성..	    	
		    within(java.time.Duration.ofSeconds(3), () -> {
		    	testActor.tell( "hi", probe.getRef() );
		    	//메시지가 오기를 기다림
		    	awaitCond(probe::msgAvailable);
		    	//메시지검사
		    	probe.expectMsg(java.time.Duration.ofSeconds(0), "너의 메시지에 응답을함");
		    	return null;
		    });
	    }};		
	}
	
	protected void actorTest4(ActorSystem system,SpringExtension ext) {
	    final TestActorRef<TestActor> testB = TestActorRef.create(system, ext.props("testActor") , "test3");
	    final CompletableFuture<Object> future = PatternsCS.ask(testB, "hi", 3000).toCompletableFuture();
	    assertTrue(future.isDone());
	    try {
			assertEquals("너의 메시지에 응답을함", future.get() );
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	protected void actorTest1(ActorSystem system,SpringExtension ext) {
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
	
	
	public void dataTest1() {
		initData();
		
	     int[] score = {Integer.MAX_VALUE};
	     int[] no = {0};
	     int[] rank = {0}; 
		itemBuyLogRepository.findByUserinfoAgeBetween(10, 20).stream()
       .sorted((a,b) -> a.getBuyitem().getItemprice() - b.getBuyitem().getItemprice() )
       .map(p -> {
            ++no[0];
            if (score[0] != p.getBuyitem().getItemprice()) rank[0] = no[0];             
            String itemRank = String.format("item:%s price:%d rank:%d", 
           		 p.getBuyitem().getItemname(),
           		 p.getBuyitem().getItemprice(),
           		 rank[0]);
            System.out.println(itemRank);
            return itemRank;                              
       })
       .collect(Collectors.toList());
		
	}
	
	
	public void initData() {
		
		for(int i=0;i<100;i++) {
			UserInfo adduser = new UserInfo("auto"+i,i%50,i%2 );
			userInfoRepository.save(adduser);
		}
		userInfoRepository.flush();
		
		for(int i=0;i<100;i++) {
			GameItem addItem = new GameItem("item"+i, "t"+i%3, i+(i*500) );		
			gameItemRepository.save(addItem);
		}
		gameItemRepository.flush();
		
		for(int i=0;i<100;i++) {
			String buyTime = String.format("2018-%02d-%02d", i%10+1,i%20+1);			
			UserInfo buyUser = userInfoRepository.findAll().get(i%50);
			GameItem buyItem = gameItemRepository.findAll().get(i%50);			
			ItemBuyLog addBuyLog = new ItemBuyLog(buyTime, buyItem, buyUser);
			itemBuyLogRepository.save(addBuyLog);			
		}
		itemBuyLogRepository.flush();

	}

}
