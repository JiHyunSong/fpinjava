package com.example.fp.db;

import java.util.List;

public interface DB<T> {
    List<T> findAll();
    <U> T find(final U query);
    boolean insert(final T value);
    <U> boolean update(final T value, final U query);
    <U> boolean delete(final U query);
}