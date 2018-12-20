package com.devkwondo.benchmark.cache.consumer.runner;

import com.devkwondo.benchmark.cache.consumer.service.ConsumerService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ConsumerRunner implements CommandLineRunner {

    private final ConsumerService consumerService;

    @Override
    public void run(String... args) throws Exception {
        consumerService.startConsuming();
    }
}
