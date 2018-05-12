package com.example.fp.api;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Api<T, U> {
    CompletableFuture<Optional<List<T>>> findAll();
    CompletableFuture<Optional<T>> find(final U query);
    CompletableFuture<Optional<T>> insert(final T value);
    CompletableFuture<Optional<T>> update(final T value, final U query);
    CompletableFuture<Optional<T>> delete(final U query);
}