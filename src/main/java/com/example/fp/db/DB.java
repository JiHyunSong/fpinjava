package com.example.fp.db;

import com.example.fp.common.LogHelper;
import java.util.List;
import java.util.Optional;

public interface DB<T, U> {
    List<T> findAll();
    T find(final U query);
    boolean insert(final T value);
    boolean update(final T value, final U query);
    boolean delete(final U query);

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
                LogHelper.logger(DB.class).error("Failed to find data from databases. ", value);
                return Optional.empty();

            }
        } catch (Exception e) {
//            LogHelper.logger(DB.class).info("Failed to execute find ", value, " , ", e);
            return Optional.empty();
        }
    }

    default Optional<Boolean> insertOp(final T value) {
        try {
            return Optional.of(insert(value));
        } catch (Exception e) {
            LogHelper.logger(DB.class).info("Failed to execute insert ", value, " , ", e);
            return Optional.empty();
        }
    }

    default Optional<Boolean> updateOp(final T value, final U query) {
        try {
            return Optional.of(update(value, query));
        } catch (Exception e) {
            LogHelper.logger(DB.class).info("Failed to execute update ", value, " , ", e);
            return Optional.empty();
        }
    }

    default Optional<Boolean> deleteOp(final U query) {
        try {
            return Optional.of(delete(query));
        } catch (Exception e) {
            LogHelper.logger(DB.class).info("Failed to execute delete ", value, " , ", e);
            return Optional.empty();
        }
    }

}