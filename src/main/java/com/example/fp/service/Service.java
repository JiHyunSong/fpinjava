package com.example.fp.service;

import com.example.fp.model.FpAuthority;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Service<T, U> {
    CompletableFuture<Optional<List<T>>> findAll();
    CompletableFuture<Optional<T>> upsert(final T value, final U query, final FpAuthority authority);
}