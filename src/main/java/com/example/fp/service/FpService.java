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

    default Optional<FpModelQuery> validate(final FpModelQuery query) {
        return Optional.of(query);
    }

    @Override
    default Optional<List<FpModel>> findAll() {
        return Optional.of(getFpDB().findAll());
    }

    @Override
    default Optional<FpModel> upsert(final FpModel model,
                                     final FpModelQuery query,
                                     final FpAuthority authority) {

        Optional<FpModel> op = validate(query)
            .flatMap(_query -> getFpDB().findOp(_query))
            .flatMap(found -> getFpDB().updateOp(found, query));
        return Optional.of(op.orElse(getFpDB().insert(model)));

    }
}