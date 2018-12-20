package com.devkwondo.benchmark.cache.consumer.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "consumer")
public class ConsumerConfigurationProperties {

    private int itemsToConsume;
    private int parallelism;
    private long sleepDuration;
}
