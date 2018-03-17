package com.psmon.cachedb.data.primary;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class GameItem {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
	long itemid;	
	
	String itemname;	
	
	String itemtype;
	
	int itemprice;
	
	@OneToMany( mappedBy="gameitem",cascade = {CascadeType.ALL},fetch=FetchType.LAZY )
	Set<ItemBuyLog>	itembuylog;

	public GameItem() {
		// TODO Auto-generated constructor stub
	}

	public GameItem(String itemname, String itemtype, int itemprice) {
		this.itemname = itemname;
		this.itemtype = itemtype;
		this.itemprice = itemprice;
	}

	public long getItemid() {
		return itemid;
	}

	public void setItemid(long itemid) {
		this.itemid = itemid;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public int getItemprice() {
		return itemprice;
	}

	public void setItemprice(int itemprice) {
		this.itemprice = itemprice;
	}

	public Set<ItemBuyLog> getBuylogs() {
		return itembuylog;
	}

	public void setBuylogs(Set<ItemBuyLog> itembuylog) {
		this.itembuylog = itembuylog;
	}
	
		
}
