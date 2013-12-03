package org.socklabs.elasticservices.core.config;

import org.socklabs.elasticservices.core.work.DefaultWorkSupervisor;
import org.socklabs.elasticservices.core.work.WorkBeanPostProcessor;
import org.socklabs.elasticservices.core.work.WorkSupervisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class WorkConfig {

    /*
    NKG: From past experience, having one off threads and executor services
    (thread pools) can make it difficult to figure out what background work
    is happening and where. I use the concept of "work" to help manage all of
    the different places where I need something happening in the background.
    All of the different "work" is managed and run by work supervisors, like
    the default one created here.
     */
    @Bean
    public WorkSupervisor workSupervisor() {
        return new DefaultWorkSupervisor(32);
    }

    @Bean
    public WorkBeanPostProcessor workBeanPostProcessor() {
        return new WorkBeanPostProcessor(workSupervisor());
    }

    @PostConstruct
    public void startWork() {
        workSupervisor().start();
    }

}
