package com.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.zuul.filter.ErrorFilter;
import com.zuul.filter.PostFilter;
import com.zuul.filter.PreFilter;

@EnableHystrixDashboard
@EnableHystrix
@EnableCircuitBreaker
@EnableZuulProxy
@SpringBootApplication  
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	@Bean
    public PreFilter preFilter() {
        return new PreFilter();
    }
    
	@Bean
    public PostFilter postFilter() {
        return new PostFilter();
    }
	
	@Bean
    public ErrorFilter errorFilter() {
        return new ErrorFilter();
    }
	
}