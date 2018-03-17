package com.psmon.cachedb;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.psmon.cachedb.data.primary.GameItem;
import com.psmon.cachedb.data.primary.GameItemRepository;
import com.psmon.cachedb.data.primary.ItemBuyLog;
import com.psmon.cachedb.data.primary.ItemBuyLogRepository;
import com.psmon.cachedb.data.primary.UserInfo;
import com.psmon.cachedb.data.primary.UserInfoRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CachedbApplicationTests {

	@Autowired
	GameItemRepository gameItemRepository;
	
	@Autowired
	ItemBuyLogRepository itemBuyLogRepository;
	
	@Autowired
	UserInfoRepository userInfoRepository;
	
	@Test
	public void contextLoads() {
		initData();
		
	     int[] score = {Integer.MIN_VALUE};
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
