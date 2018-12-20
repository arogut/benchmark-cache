package com.devkwondo.benchmark.cache.consumer.service.hazelcast;

import com.devkwondo.benchmark.cache.consumer.service.CachingService;
import com.hazelcast.cache.ICache;
import com.hazelcast.core.IQueue;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Profile("hazelcast")
public class HazelcastCachingService<K, V> implements CachingService<K, V> {

    private final IQueue<K> itemIdQueue;
    private final ICache<K, V> itemCache;

    @Override
    public K poll() {
        return itemIdQueue.poll();
    }

    @Override
    public V get(K id) {
        if (id != null) {
            return itemCache.get(id);
        }
        return null;
    }

    @Override
    public boolean remove(K id) {
        if (id != null) {
            return itemCache.remove(id);
        }
        return false;
    }
}
