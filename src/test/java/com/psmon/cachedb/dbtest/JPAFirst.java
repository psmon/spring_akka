package com.psmon.cachedb.dbtest;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.psmon.cachedb.actors.fsm.Flush;
import com.psmon.cachedb.actors.fsm.Queue;
import com.psmon.cachedb.actors.fsm.SetTarget;
import com.psmon.cachedb.data.primary.GameItem;
import com.psmon.cachedb.data.primary.ItemBuyLog;
import com.psmon.cachedb.data.primary.UserInfo;
import com.psmon.cachedb.extension.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;

@Component
public class JPAFirst extends JPABase {
	
	public void runAll() {
		dataTest1();		
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
	
	public void fsmDBWriteTest(ActorSystem system,SpringExtension ext) {
	    new TestKit(system) {
		{
	        final ActorRef buncher =
	          system.actorOf(ext.props("buncher"));
	        
	        final ActorRef dbWriter =
	  	          system.actorOf(ext.props("DBWriteActor"));
	        
	        final ActorRef probe = getRef();
	        
	        buncher.tell(new SetTarget(dbWriter), dbWriter);
	        
			for(int i=0;i<200;i++) {				
				String buyTime = String.format("2018-%02d-%02d", i%10+1,i%20+1);			
				UserInfo buyUser = userInfoRepository.findAll().get(i%50);
				GameItem buyItem = gameItemRepository.findAll().get(i%50);			
				ItemBuyLog addBuyLog = new ItemBuyLog(buyTime, buyItem, buyUser);				
				buncher.tell(new Queue(addBuyLog), dbWriter);
			}
			
			buncher.tell(Flush.Flush , dbWriter);
			
			 system.stop(buncher);
	      }};
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
