package com.example.fp.service;

import com.example.fp.model.FpAuthority;

import com.example.fp.monad.monad.ResponseM;
import java.util.List;
import java.util.Optional;

public interface Service<T, U> {
    ResponseM<List<T>> findAll();
    ResponseM<T> upsert(final T value, final U query, final FpAuthority authority);
}