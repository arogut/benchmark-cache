package com.devkwondo.benchmark.cache.consumer.service.ignite;

import com.devkwondo.benchmark.cache.consumer.service.CachingService;
import lombok.AllArgsConstructor;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteQueue;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
@Profile("ignite")
public class IgniteCachingService implements CachingService<String, Object> {

    private final IgniteQueue<String> itemIdQueue;
    private final IgniteCache<String, Object> itemCache;

    @Override
    public String poll() {
        List<String> results = new ArrayList<>(1);
        itemIdQueue.drainTo(results, 1);
        return results.isEmpty() ? null : results.get(0);
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
