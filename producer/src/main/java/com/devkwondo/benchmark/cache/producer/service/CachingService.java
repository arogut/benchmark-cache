package com.devkwondo.benchmark.cache.producer.service;


public interface CachingService<K,V> {

    void addToCache(K k, V v);

    void addToQueue(K k);

}
