package com.example.fp.service;

import com.example.fp.model.FpAuthority;
import java.util.List;

public interface Service<T, U> {
    List<T> findAll();
    T upsert(final T value, final U query, final FpAuthority authority);
}
