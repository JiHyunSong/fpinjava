package com.example.fp.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
public class ControllerHelper {
    public <T> Supplier<T> conditional(final boolean valid, final Supplier<T> succeeded, final Supplier<T> failed) {
        return valid ? succeeded : failed;
    }

    public <T> ResponseEntity<T> badRequestIfNotValidResponseEntity(final boolean valid, final Supplier<ResponseEntity<T>> succeeded) {
        return conditional(valid, succeeded, () -> new ResponseEntity<>(HttpStatus.BAD_REQUEST)).get();
    }

    public <T> CompletableFuture<ResponseEntity<T>> badRequestIfNotValidCompletableFuture(final boolean valid, final Supplier<CompletableFuture<ResponseEntity<T>>> succeeded) {
        return conditional(valid, succeeded, () -> CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.BAD_REQUEST))).get();
    }

    public <T> CompletableFuture<ResponseEntity<T>> unauthorizedIfNotValidCompletableFuture(final boolean valid, final Supplier<CompletableFuture<ResponseEntity<T>>> succeeded) {
        return conditional(valid, succeeded, () -> CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.UNAUTHORIZED))).get();
    }
}