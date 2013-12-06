package com.socklabs.elasticservices.examples.calc.config;

import com.socklabs.elasticservices.core.config.PropertiesConfig;
import com.socklabs.elasticservices.core.config.RabbitMqConfig;
import com.socklabs.elasticservices.core.config.ServiceConfig;
import com.socklabs.elasticservices.core.config.WorkConfig;
import com.socklabs.elasticservices.examples.calc.CalcPropertiesConfig;
import com.socklabs.elasticservices.gossip.GossipServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@Import({
				PropertiesConfig.class,
				CalcPropertiesConfig.class,
				CalcWebappPropertiesConfig.class,
				RabbitMqConfig.class,
				WorkConfig.class,
				ServiceConfig.class,
				GossipServiceConfig.class,
				EdgeConfig.class,
				BeanConfig.class})
public class AppConfig {

}