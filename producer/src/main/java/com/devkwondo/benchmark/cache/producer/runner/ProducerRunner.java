package com.devkwondo.benchmark.cache.producer.runner;

import com.devkwondo.benchmark.cache.producer.service.ProducerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ProducerRunner implements CommandLineRunner {

    private final ProducerService producerService;

    @Override
    public void run(String... args) throws Exception {
        producerService.populateCache();
    }
}
