package com.psmon.cachedb;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.psmon.cachedb.extension.SpringExtension;

import akka.NotUsed;
import akka.actor.ActorRef;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.FiniteDuration;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class CachedbApplication {

	public static void main(String[] args) throws InterruptedException, ExecutionException  {
		ApplicationContext context = SpringApplication.run(CachedbApplication.class, args);
		
        ActorSystem system = context.getBean(ActorSystem.class);

        final LoggingAdapter log = Logging.getLogger(system, "Application");

        log.info("Starting up");

        SpringExtension ext = context.getBean(SpringExtension.class);
        
        //액터 생성... 
        ActorRef testActor = system.actorOf(ext.props("testActor")
        		.withDispatcher("my-thread-pool-dispatcher"),
        		"service1");
        
        ActorSelection testActor2 = system.actorSelection("user/service1");
        
        ActorSelection testActorRemote = system.actorSelection("akka.tcp://app@localhost:2552/user/service1");
        
        //참조를 통한 전송 ( 로컬 객체관리에 유리 )
        testActor.tell("ready spring boot",null);
        
        //액터 셀렉션을 통한 전송(
        testActor2.tell("ready spring boot -again", null);
        
        //리모트를 통한 전송
        /*
        testActorRemote.tell("발사후망각-응답필요없음",ActorRef.noSender() ) ;        
        CompletableFuture<Object> future1 =
        		  ask(testActorRemote, "응답하라 1979", 1000).toCompletableFuture();*/

        testActor.tell("발사후망각-응답필요없음",ActorRef.noSender() ) ;        
        CompletableFuture<Object> future1 =
        		  ask(testActor, "응답하라 1979", 1000).toCompletableFuture();

        
        String result = String.valueOf(future1.get());
        
        log.info("msg result:" + result);
        
        
        final Materializer materializer = ActorMaterializer.create(system);
        
        final ActorRef throttler =
          Source.actorRef(1000, OverflowStrategy.dropNew())
          	.throttle(10,  Duration.ofSeconds(1))
            .to(Sink.actorRef(testActor, NotUsed.getInstance() ))
            .run(materializer);
        	        
        for(int i=0;i<100;i++) {
        	throttler.tell(String.format("fasmsg to slow %d", i), ActorRef.noSender());       	
        }
        
        
	}
}
