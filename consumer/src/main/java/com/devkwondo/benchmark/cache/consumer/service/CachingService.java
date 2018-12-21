package com.devkwondo.benchmark.cache.consumer.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CachingService<K, V> {

    List<K> poll();
    Collection<V> get(Set<K> k);
    boolean remove(K k);
}
