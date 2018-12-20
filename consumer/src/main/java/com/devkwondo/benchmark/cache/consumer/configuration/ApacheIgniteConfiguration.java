package com.devkwondo.benchmark.cache.consumer.configuration;

import org.apache.ignite.*;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean(destroyMethod = "close")
    IgniteQueue<String> itemIdQueue(
            Ignite ignite,
            @Value("${ignite.static.itemIdQueueName") String queueName,
            CollectionConfiguration itemIdQueueConfiguration
    ) {
        return ignite.queue(queueName, 0, itemIdQueueConfiguration);
    }

    @Bean
    @ConfigurationProperties(prefix = "ignite.queue.item-id")
    CollectionConfiguration itemIdQueueConfiguration() {
        return new CollectionConfiguration();
    }

    @Bean(destroyMethod = "close")
    IgniteCache<String, Object> itemCache(
            Ignite ignite,
            @Value("${ignite.static.itemCacheName}") String cacheName
    ) {
        return ignite.getOrCreateCache(cacheName);
    }

}
