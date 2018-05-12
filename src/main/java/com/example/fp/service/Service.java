package com.example.fp.service;

import com.example.fp.model.FpAuthority;

import java.util.List;
import java.util.Optional;

public interface Service<T, U> {
    Optional<List<T>> findAll();
    Optional<T> upsert(final T value, final U query, final FpAuthority authority);
}