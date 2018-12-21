package com.devkwondo.benchmark.cache.consumer.service.hazelcast;

import com.devkwondo.benchmark.cache.consumer.service.CachingService;
import com.hazelcast.cache.ICache;
import com.hazelcast.core.IQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
@Profile("hazelcast")
public class HazelcastCachingService<K, V> implements CachingService<K, V> {

    private final IQueue<K> itemIdQueue;
    private final ICache<K, V> itemCache;

    @Value("${consumer.pollBatchSize:1}")
    private int batchSize;

    @Override
    public List<K> poll() {
        List<K> results = new ArrayList<>(batchSize);
        itemIdQueue.drainTo(results, batchSize);
        return results;
    }

    @Override
    public Collection<V> get(Set<K> ids) {
        if (!ids.isEmpty()) {
            return itemCache.getAll(ids).values();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean remove(K id) {
        if (id != null) {
            return itemCache.remove(id);
        }
        return false;
    }
}
