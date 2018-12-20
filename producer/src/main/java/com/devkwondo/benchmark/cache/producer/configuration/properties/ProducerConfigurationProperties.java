package com.devkwondo.benchmark.cache.producer.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "producer")
public class ProducerConfigurationProperties {

    private int itemsToProduce;
    private int parallelism;
    private int minPayloadSize;
    private int maxPayloadSize;
}
