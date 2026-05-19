package com.Meta_learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class MetaLearningApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(MetaLearningApplication.class, args);
	}

	// 외부 Tomcat에서 실행되도록 설정
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MetaLearningApplication.class);
	}

}


