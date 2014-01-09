package com.socklabs.elasticservices.core;

import com.socklabs.elasticservices.core.config.WorkConfig;
import com.socklabs.elasticservices.core.work.AbstractWork;
import com.socklabs.elasticservices.core.work.Work;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by nick.gerakines on 1/9/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class WorkTest {

	@Configuration
	@Import({WorkConfig.class})
	@PropertySource({ "classpath:com/socklabs/elasticservices/core/test.properties" })
	static class ContextConfiguration {

		@Bean
		public CountingWork countingWork() {
			return new CountingWork();
		}

	}

	@Resource
	private CountingWork countingWork;

	@Test
	public void counter() {
		Assert.assertTrue(
				waitUntil(
						new Callable<Work.Phase>(){ @Override public Work.Phase call() throws Exception { return countingWork.getPhase(); } },
						AbstractWork.StandardPhase.STOPPED,
						10));
		countingWork.getPhase();
	}

	private <T> boolean waitUntil(final Callable<T> callable, final T value, final int seconds) {
		final DateTime start = DateTime.now();
		while (DateTime.now().isBefore(start.plusSeconds(seconds))) {
			try {
				if (callable.call().equals(value)) {
					return true;
				}
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
			} catch (final Exception e) {
				return false;
			}
		}
		return false;
	}

}
