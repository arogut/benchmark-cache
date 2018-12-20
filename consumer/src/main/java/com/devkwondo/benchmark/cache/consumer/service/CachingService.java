package com.devkwondo.benchmark.cache.consumer.service;

public interface CachingService<K,V> {

    K poll();
    V get(K k);
    boolean remove(K k);

}
