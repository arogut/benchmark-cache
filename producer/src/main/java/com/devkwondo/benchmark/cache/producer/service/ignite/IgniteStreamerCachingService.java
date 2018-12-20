package com.devkwondo.benchmark.cache.producer.service.ignite;

import com.devkwondo.benchmark.cache.producer.service.CachingService;
import lombok.AllArgsConstructor;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteQueue;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@AllArgsConstructor
@Service
public class IgniteStreamerCachingService<K,V> implements CachingService<K,V> {

    private final IgniteDataStreamer<K,V> igniteDataStreamer;
    private final IgniteQueue<K> igniteQueue;

    @PostConstruct
    private void init() {

    }

    @Override
    public void addToCache(K k, V v) {
        igniteDataStreamer.addData(k, v);
    }

    @Override
    public void addToQueue(K k) {
        igniteQueue.add(k);
    }
}
