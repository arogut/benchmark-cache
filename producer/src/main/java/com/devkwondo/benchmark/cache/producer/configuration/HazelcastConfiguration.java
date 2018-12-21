package com.devkwondo.benchmark.cache.producer.configuration;

import com.hazelcast.cache.ICache;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("hazelcast")
@Configuration
public class HazelcastConfiguration {

    @Bean
    HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    @Bean
    @ConfigurationProperties(prefix = "hazelcast.config")
    ClientConfig clientConfig() {
        return new ClientConfig();
    }

    @Bean(destroyMethod = "destroy")
    IQueue<String> itemIdQueue(
            HazelcastInstance hz,
            @Value("${hazelcast.static.itemIdQueueName") String queueName
    ) {
        return hz.getQueue(queueName);
    }

    @Bean(destroyMethod = "destroy")
    ICache<String, Object> itemCache(
            HazelcastInstance hz,
            @Value("${hazelcast.static.itemCacheName}") String cacheName
    ) {
        return hz.getCacheManager().getCache(cacheName);
    }
}
