package com.mulodo.fiveneed;

import javax.servlet.http.HttpSessionListener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mulodo.fiveneed.config.SessionListener;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Main
 */
@SpringBootApplication(scanBasePackages = {"com.mulodo.fiveneed"},
		exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@EnableSwagger2
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Bean
	public Docket swaggerSettings() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.any()).paths(PathSelectors.any())
				.build().pathMapping("/");
	}
	
	@Bean
	public HttpSessionListener httpSessionListener(){
	    // MySessionListener should implement javax.servlet.http.HttpSessionListener
	    return new SessionListener(); 
	}
}
