package com.example.fp.service;

import com.example.fp.db.FpDB;
import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface FpService extends Service<FpModel, FpModelQuery> {
    FpDB getFpDB();

    default List<FpModel> findAllModelBySomeCond(final Predicate<FpModel> f) {
        return getFpDB().findAll().stream().filter(f).collect(Collectors.toList());
    }

    default Boolean validate(final FpModel model) {
        return true;
    }

    @Override
    default Optional<List<FpModel>> findAll() {
        return Optional.of(getFpDB().findAll());
    }

    @Override
    default Optional<FpModel> upsert(final FpModel model,
                                     final FpModelQuery query,
                                     final FpAuthority authority) {
        if (!validate(model)) {
            return Optional.empty();
        }

        Optional<FpModel> op = getFpDB().findOp(query);
        op
                .flatMap(found -> getFpDB().updateOp(found, query).map(result -> found))
                .orElseGet(() -> { getFpDB().insert(model); return model; });

        // 있다면
        // getFpDB().update(model, query);
        // 없다면
        // getFpDB().insert(model);
        try {
            final FpModel found = getFpDB().find(query);
            if (found == null ) {
                getFpDB().insert(model);
            } else {
                getFpDB().update(model, query);
            }
            return Optional.of(model);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}