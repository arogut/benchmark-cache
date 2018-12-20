package com.devkwondo.benchmark.cache.producer.service;

import com.devkwondo.benchmark.cache.producer.configuration.properties.ProducerConfigurationProperties;
import com.devkwondo.benchmark.cache.producer.model.Item;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@AllArgsConstructor
@Service
public class ProducerService {

    private final CachingService cachingService;
    private final ProducerConfigurationProperties producerConfigurationProperties;
    private final Random random = new Random();

    public void populateCache() {
        Executor executor = Executors.newFixedThreadPool(producerConfigurationProperties.getParallelism());
        for (int i = 0; i < producerConfigurationProperties.getItemsToProduce(); i++) {
            executor.execute(() -> {
                Item item = createNewItem();
                cachingService.addToCache(item.getId(), item);
                cachingService.addToQueue(item.getId());
            });
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
