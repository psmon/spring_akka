package com.psmon.cachedb.dbtest;

import org.springframework.beans.factory.annotation.Autowired;

import com.psmon.cachedb.data.primary.GameItemRepository;
import com.psmon.cachedb.data.primary.ItemBuyLogRepository;
import com.psmon.cachedb.data.primary.UserInfoRepository;

public class JPABase {
	
	@Autowired
	public GameItemRepository gameItemRepository;
	
	@Autowired
	public ItemBuyLogRepository itemBuyLogRepository;
	
	@Autowired
	public UserInfoRepository userInfoRepository;

}
