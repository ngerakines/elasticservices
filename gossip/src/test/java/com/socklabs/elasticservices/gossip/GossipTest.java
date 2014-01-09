package com.socklabs.elasticservices.gossip;

import com.socklabs.elasticservices.core.config.ServiceConfig;
import com.socklabs.elasticservices.core.service.Service;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;

/**
 * Created by nick.gerakines on 1/9/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class GossipTest {

	@Configuration
	@Import({ServiceConfig.class, GossipServiceConfig.class})
	@PropertySource({ "classpath:com/socklabs/elasticservices/core/test.properties" })
	static class ContextConfiguration {

	}

	@Resource
	private Service gossipService;

	@IfProfileValue(name="test-groups", values={"rabbitmq", "integration-tests"})
	@Test
	public void check() {
		Assert.assertNotNull(gossipService);
	}

}
