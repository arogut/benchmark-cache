package com.devkwondo.benchmark.cache.producer.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Data
@Profile("ignite")
@Configuration
@ConfigurationProperties("ignite.streamer")
public class IgniteDataStreamerConfigurationProperties {

    private long autoFlushFrequency;
    private int bufferSize;

}
