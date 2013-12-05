package com.socklabs.elasticservices.examples.calc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:com/socklabs/elasticservices/examples/calc/webapp.properties"})
public class CalcWebappPropertiesConfig {
}
