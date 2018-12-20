package com.devkwondo.benchmark.cache.consumer.service.ignite;

import com.devkwondo.benchmark.cache.consumer.service.CachingService;
import lombok.AllArgsConstructor;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteQueue;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class IgniteCachingService implements CachingService<String, Object> {

    private final IgniteQueue<String> itemIdQueue;
    private final IgniteCache<String, Object> itemCache;

    @Override
    public String poll() {
        return itemIdQueue.poll();
    }

    @Override
    public Object get(String id) {
        if (id != null) {
            return itemCache.get(id);
        }
        return null;
    }

    @Override
    public boolean remove(String id) {
        if (id != null) {
            return itemCache.remove(id);
        }
        return false;
    }
}
