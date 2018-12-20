package com.devkwondo.benchmark.cache.producer.service.hazelcast;

import com.devkwondo.benchmark.cache.producer.service.CachingService;
import com.hazelcast.cache.ICache;
import com.hazelcast.core.IQueue;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Profile("hazelcast")
public class HazelcastCachingService<K, V> implements CachingService<K, V> {

    private final ICache<K, V> cache;
    private final IQueue<K> queue;

    @Override
    public void addToCache(K k, V v) {
        cache.put(k, v);
    }

    @Override
    public void addToQueue(K k) {
        queue.add(k);
    }
}
