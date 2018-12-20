package com.devkwondo.benchmark.cache.datastore.configuration;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("hazelcast")
@Configuration
public class HazelcastConfiguration {

    @Bean
    HazelcastInstance hazelcastInstance(Config hazelcastConfig) {
        return Hazelcast.newHazelcastInstance(hazelcastConfig);
    }

    @Bean
    @ConfigurationProperties(prefix = "hazelcast.config")
    Config config() {
        return new Config();
    }
}
