package com.devkwondo.benchmark.cache.consumer.service;

import com.devkwondo.benchmark.cache.consumer.configuration.properties.ConsumerConfigurationProperties;
import com.devkwondo.benchmark.cache.model.domain.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@AllArgsConstructor
public class ConsumerService {

    private final ConsumerConfigurationProperties consumerConfigurationProperties;
    private final CachingService<String, Item> cachingService;

    public void startConsuming() {
        log.info("Starting consumption using following settings: {}", consumerConfigurationProperties);
        final AtomicInteger itemsToConsume = new AtomicInteger(consumerConfigurationProperties.getItemsToConsume());
        ExecutorService executor = Executors.newFixedThreadPool(consumerConfigurationProperties.getParallelism());
        for (int i = 0; i < consumerConfigurationProperties.getParallelism(); i++) {
            executor.execute(() -> {
                while (itemsToConsume.get() > 0) {
                    try {
                        List<String> itemIds = cachingService.poll();
                        if (itemIds.isEmpty()) {
                            log.debug("Queue is empty. Sleep {}ms", consumerConfigurationProperties.getSleepDuration());
                            TimeUnit.MILLISECONDS.sleep(consumerConfigurationProperties.getSleepDuration());
                        } else {
                            cachingService.get(new HashSet<>(itemIds)).forEach(x -> consumeObject(x, itemsToConsume));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
        } finally {
            int itemsLeft = itemsToConsume.get();
            if (itemsLeft > 0) {
                log.warn("Consumption finished. {} items left.", itemsLeft);
            } else {
                log.info("All items were consumed.");
            }
        }

    }

    private void consumeObject(Item item, AtomicInteger itemsToConsume) {
        log.debug("Polled task with id {}.", item.getId());
        itemsToConsume.decrementAndGet();
        if (item == null) {
            log.warn("Item with id {} was not found in cache.", item.getId());
        }
        if (!cachingService.remove(item.getId())) {
            log.warn("Couldn't remove item with id {} from cache.", item.getId());
        }
        log.debug("Consumed item with id {}.", item.getId());
    }

}
