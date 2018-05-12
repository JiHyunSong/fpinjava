package com.example.fp.service;

import com.example.fp.model.FpAuthority;
import com.example.fp.monad.Witness;
import com.example.fp.monad.monad.ResponseM;
import com.example.fp.monad.transformer.ResponseT;

import java.util.List;

public interface Service<T, U> {
    ResponseM<List<T>> findAll();
    ResponseM<T> upsert(final T value, final U query, final FpAuthority authority);
    ResponseT<Witness.completableM, List<T>> findAllByApi();
    ResponseT<Witness.completableM, T> upsertByApi(final T value, final U query, final FpAuthority authority);
}