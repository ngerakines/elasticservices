package com.socklabs.elasticservices.core.config;

import com.socklabs.elasticservices.core.work.DefaultWorkSupervisor;
import com.socklabs.elasticservices.core.work.WorkBeanPostProcessor;
import com.socklabs.elasticservices.core.work.WorkSupervisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class WorkConfig {

	@Bean
	public WorkBeanPostProcessor workBeanPostProcessor() {
		return new WorkBeanPostProcessor(workSupervisor());
	}

	@Bean
	public WorkSupervisor workSupervisor() {
		return new DefaultWorkSupervisor(32);
	}

	@PostConstruct
	public void startWork() {
		workSupervisor().start();
	}

}
