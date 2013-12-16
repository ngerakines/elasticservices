package com.socklabs.elasticservices.core.work;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/*
NKG: This is where some magic is taking place. Here I've created a bean post
processor to be called after every bean is created and initialized. This
specific one will see if the bean implements the "Work" interface and if so,
will add it to the default work supervisor.
 */
public class WorkBeanPostProcessor implements BeanPostProcessor {

	private final WorkSupervisor workSupervisor;

	public WorkBeanPostProcessor(final WorkSupervisor workSupervisor) {
		this.workSupervisor = workSupervisor;
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String name) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String name) throws BeansException {
		if (bean instanceof Work) {
			final Work work = (Work) bean;
			workSupervisor.addWork(work);
		}
		return bean;
	}

}
