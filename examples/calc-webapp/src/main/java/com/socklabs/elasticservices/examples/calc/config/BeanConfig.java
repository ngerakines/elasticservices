package com.socklabs.elasticservices.examples.calc.config;

import com.socklabs.elasticservices.examples.calc.IndexApiController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

	@Bean
	public IndexApiController indexApiController() {
		return new IndexApiController();
	}

}
