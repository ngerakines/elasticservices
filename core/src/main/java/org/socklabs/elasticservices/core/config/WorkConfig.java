package org.socklabs.elasticservices.core.config;

import org.socklabs.elasticservices.core.work.DefaultWorkSupervisor;
import org.socklabs.elasticservices.core.work.WorkBeanPostProcessor;
import org.socklabs.elasticservices.core.work.WorkSupervisor;
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
