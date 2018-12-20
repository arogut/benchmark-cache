package com.devkwondo.benchmark.cache.datastore.configuration;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Profile("hazelcast")
@Configuration
public class HazelcastConfiguration {

    @Bean
    HazelcastInstance hazelcastInstance(Config hazelcastConfig) {
        return Hazelcast.newHazelcastInstance(hazelcastConfig);
    }

    @Bean
    Config config(List<CacheSimpleConfig> cacheConfigs) {
        Config config = new Config();
        cacheConfigs.forEach(config::addCacheConfig);
        return config;
    }

    @Bean
    @ConfigurationProperties(prefix = "hazelcast.config")
    List<CacheSimpleConfig> cacheConfigs() {
        return new ArrayList<>();
    }

}
