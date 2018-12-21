package com.devkwondo.benchmark.cache.consumer.service.ignite;

import com.devkwondo.benchmark.cache.consumer.service.CachingService;
import com.devkwondo.benchmark.cache.model.domain.Item;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
@Profile("ignite")
public class IgniteCachingService implements CachingService<String, Item> {

    private final IgniteQueue<String> itemIdQueue;
    private final IgniteCache<String, Item> itemCache;

    @Value("${consumer.pollBatchSize:1}")
    private int batchSize;

    @Override
    public List<String> poll() {
        List<String> results = new ArrayList<>(batchSize);
        itemIdQueue.drainTo(results, batchSize);
        return results;
    }

    @Override
    public Collection<Item> get(Set<String> ids) {
        if (!ids.isEmpty()) {
            return itemCache.getAll(ids).values();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean remove(String id) {
        if (id != null) {
            return itemCache.remove(id);
        }
        return false;
    }
}
