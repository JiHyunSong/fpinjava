package com.example.fp.monad.adapter;

import com.example.fp.monad.Monadic;
import com.example.fp.monad.WitnessType;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

public interface Adapter<W extends WitnessType<W>> {
    <U> Monadic<W, U> empty();
    <U> Monadic<W, U> unit(final U value);
    <U> CompletableFuture<U> toCompletableFuture(final Monadic<W, U> m);
    <U> ResponseEntity<U> toResponseEntity(final Monadic<W, U> m);

    static <U> CompletableFuture<U> failedCompletableFuture() {
        return failedCompletableFuture(new Exception("CompletableM Failure"));
    }

    static <U> CompletableFuture<U> failedCompletableFuture(final Throwable th) {
        final CompletableFuture<U> failed = new CompletableFuture<>();
        failed.completeExceptionally(th);
        return failed;
    }
}