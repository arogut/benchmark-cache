package com.devkwondo.benchmark.cache.consumer.configuration;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("ignite")
@Configuration
public class ApacheIgniteConfiguration {

    @Bean
    Ignite ignite(IgniteConfiguration igniteConfiguration, ApplicationContext appCtx) throws IgniteCheckedException {
        return IgniteSpring.start(igniteConfiguration, appCtx);
    }

    @Bean
    @ConfigurationProperties(prefix = "ignite.config")
    IgniteConfiguration igniteConfiguration() {
        return new IgniteConfiguration();
    }
}
