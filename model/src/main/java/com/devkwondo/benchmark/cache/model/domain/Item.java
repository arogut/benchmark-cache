package com.devkwondo.benchmark.cache.model.domain;

import lombok.Value;

import java.io.Serializable;

@Value
public class Item implements Serializable {

    private static final long serialVersionUID = 435348617295952207L;

    private final String id;
    private final byte[] payload;
}
