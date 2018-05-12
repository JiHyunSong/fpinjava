package com.example.fp.common;

import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModelQuery;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AuthorityHelper {
    public static <U> CompletableFuture<Optional<U>> checkAuthorityByApi(final FpModelQuery query, final FpAuthority authority) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public static <T> CompletableFuture<Optional<Integer>> someOtherApi(final T value) {
        return CompletableFuture.completedFuture(Optional.of(123));
    }
}