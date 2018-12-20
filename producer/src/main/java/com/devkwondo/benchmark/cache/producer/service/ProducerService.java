package com.devkwondo.benchmark.cache.producer.service;

import com.devkwondo.benchmark.cache.producer.configuration.properties.ProducerConfigurationProperties;
import com.devkwondo.benchmark.cache.model.domain.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@Service
public class ProducerService {

    private final CachingService cachingService;
    private final ProducerConfigurationProperties producerConfigurationProperties;
    private final Random random = new Random();

    public void populateCache() {
        log.info("Populating cache using following settings: {}", producerConfigurationProperties);
        Executor executor = Executors.newFixedThreadPool(producerConfigurationProperties.getParallelism());
        for (int i = 0; i < producerConfigurationProperties.getItemsToProduce(); i++) {
            executor.execute(() -> {
                try {
                    Item item = createNewItem();
                    log.debug("Adding new item {}", item.getId());
                    cachingService.addToCache(item.getId(), item);
                    cachingService.addToQueue(item.getId());
                    log.debug("New item was successfully added {}.", item.getId());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            });
        }
        try {
            ((ExecutorService) executor).shutdown();
            ((ExecutorService) executor).awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
        } finally {
            log.info("Cache population finished.");
        }
    }

    private Item createNewItem() {
        return new Item(UUID.randomUUID().toString(), randomPayload());
    }

    private byte[] randomPayload() {
        byte[] payload = new byte[calculatePayloadSize()];
        random.nextBytes(payload);
        return payload;
    }

    private int calculatePayloadSize() {
        if (producerConfigurationProperties.getMinPayloadSize() == producerConfigurationProperties.getMaxPayloadSize()) {
            return producerConfigurationProperties.getMaxPayloadSize();
        } else {
            return random.nextInt(producerConfigurationProperties.getMaxPayloadSize() - producerConfigurationProperties.getMinPayloadSize() + 1) + producerConfigurationProperties.getMinPayloadSize();
        }
    }

}
