package com.psmon.cachedb.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class ApplicationConfig {
	
	   @EventListener(ApplicationReadyEvent.class)
	   public void doSomethingAfterStartup() {
	  }
	   
	   
}
