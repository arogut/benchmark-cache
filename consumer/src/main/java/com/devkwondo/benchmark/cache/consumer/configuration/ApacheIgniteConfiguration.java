package com.devkwondo.benchmark.cache.consumer.configuration;

import com.devkwondo.benchmark.cache.commons.ignite.queue.QueueProxyInvocationHandler;
import com.devkwondo.benchmark.cache.model.domain.Item;
import org.apache.ignite.*;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.processors.datastructures.GridCacheQueueProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.reflect.Proxy;

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
            @Value("${ignite.static.itemIdQueueName}") String queueName,
            CollectionConfiguration itemIdQueueConfiguration
    ) throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        GridCacheQueueProxy gridCacheQueueProxy = (GridCacheQueueProxy) ignite.queue(queueName, 0, itemIdQueueConfiguration);
        return (IgniteQueue<String>) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {IgniteQueue.class}, new QueueProxyInvocationHandler(gridCacheQueueProxy));
    }

    @Bean
    @ConfigurationProperties(prefix = "ignite.queue.item-id")
    CollectionConfiguration itemIdQueueConfiguration() {
        return new CollectionConfiguration();
    }

    @Bean(destroyMethod = "close")
    IgniteCache<String, Item> itemCache(
            Ignite ignite,
            @Value("${ignite.static.itemCacheName}") String cacheName
    ) {
        return ignite.getOrCreateCache(cacheName);
    }

}
