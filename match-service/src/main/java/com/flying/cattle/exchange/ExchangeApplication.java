package com.flying.cattle.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

import com.flying.cattle.exchange.plugins.mq.MatchSink;

@SpringBootApplication
@EnableBinding({ MatchSink.class }) 
public class ExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeApplication.class, args);
	}

}
