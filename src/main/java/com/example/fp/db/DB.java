package com.example.fp.db;

import com.example.fp.common.LogHelper;

import java.util.List;
import java.util.Optional;

public interface DB<T, U> {
    List<T> findAll();
    T find(final U query);
    T insert(final T value);
    T update(final T value, final U query);
    T delete(final U query);

    default Optional<List<T>> findAllOp() {
        try {
            return Optional.of(findAll());
        } catch (Exception e) {
            LogHelper.logger(DB.class).info("Failed to execute find all, ", e);
            return Optional.empty();
        }
    }

    default Optional<T> findOp(final U query) {
        try {
            final T value = find(query);
            if (value != null) {
                return Optional.of(value);
            } else {
                LogHelper.logger(DB.class).error("Failed to find data from database.");
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    default Optional<T> insertOp(final T value) {
        try {
            final T result = insert(value);
            return result != null ? Optional.of(result) : Optional.empty();
        } catch (Exception e) {
            LogHelper.logger(DB.class).info("Failed to execute find all, ", e);
            return Optional.empty();
        }
    }

    default Optional<T> updateOp(final T value, final U query) {
        try {
            final T result = update(value, query);
            return result != null ? Optional.of(result) : Optional.empty();
        } catch (Exception e) {
            LogHelper.logger(DB.class).info("Failed to execute find all, ", e);
            return Optional.empty();
        }
    }

    default Optional<T> deleteOp(final U query) {
        try {
            final T result = delete(query);
            return result != null ? Optional.of(result) : Optional.empty();
        } catch (Exception e) {
            LogHelper.logger(DB.class).info("Failed to execute find all, ", e);
            return Optional.empty();
        }
    }
}