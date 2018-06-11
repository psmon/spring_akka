package com.psmon.cachedb.data.primary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ItemBuyLog {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
	long logid;
	
	String buytime;
	
	@ManyToOne
    @JoinColumn(name = "item_id" )  
	GameItem	gameitem;

	
	@ManyToOne
    @JoinColumn(name = "user_id" ) 
	UserInfo	userinfo;
	
	public ItemBuyLog() {
		// TODO Auto-generated constructor stub
	}

	public ItemBuyLog(String buytime, GameItem gameitem, UserInfo userinfo) {
		this.buytime = buytime;
		this.gameitem = gameitem;
		this.userinfo = userinfo;
	}

	public long getLogid() {
		return logid;
	}

	public void setLogid(long logid) {
		this.logid = logid;
	}


	public String getBuytime() {
		return buytime;
	}


	public void setBuytime(String buytime) {
		this.buytime = buytime;
	}


	public GameItem getBuyitem() {
		return gameitem;
	}


	public void setBuyitem(GameItem gameitem) {
		this.gameitem = gameitem;
	}


	public UserInfo getBuyuser() {
		return userinfo;
	}


	public void setBuyuser(UserInfo userinfo) {
		this.userinfo = userinfo;
	}
	

}
