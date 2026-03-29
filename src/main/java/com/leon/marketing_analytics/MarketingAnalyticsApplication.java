package com.leon.marketing_analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MarketingAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketingAnalyticsApplication.class, args);
	}

}
