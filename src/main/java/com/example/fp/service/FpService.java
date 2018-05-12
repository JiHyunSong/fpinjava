package com.example.fp.service;

import com.example.fp.api.FpApi;
import com.example.fp.db.FpDB;
import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import com.example.fp.monad.Witness;
import com.example.fp.monad.monad.CompletableM;
import com.example.fp.monad.monad.ResponseM;
import com.example.fp.monad.transformer.ResponseT;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface FpService extends Service<FpModel, FpModelQuery> {
    FpDB getFpDB();
    FpApi getFpApi();

    default List<FpModel> findAllModelBySomeCond(final Predicate<FpModel> f) {
        return getFpDB().findAll().stream().filter(f).collect(Collectors.toList());
    }

    default ResponseM<FpModelQuery> validate(final FpModelQuery query) {
        // return ResponseM.of(query);
        return ResponseM.failure(HttpStatus.BAD_REQUEST, "not valid field name xyz");
    }

    default ResponseT<Witness.completableM, FpModelQuery> validateT(final FpModelQuery query) {
        return ResponseT.of(CompletableM.of(validate(query)));
    }

    @Override
    default ResponseT<Witness.completableM, List<FpModel>> findAllByApi() {
        return ResponseT.ofO(getFpApi().findAll());
    }

    @Override
    default ResponseT<Witness.completableM, FpModel> upsertByApi(final FpModel model,
                                                                 final FpModelQuery query,
                                                                 final FpAuthority authority) {
        final ResponseT<Witness.completableM, FpModel> found = validateT(query).flatMap(_nohting -> ResponseT.ofO(getFpApi().find(query)));
        return found
                .flatMap(_found -> ResponseT.ofO(getFpApi().update(_found, query)))
                .orElseGet(() -> found.orElse(ResponseT.ofO(getFpApi().insert(model))));
    }

    @Override
    default ResponseM<List<FpModel>> findAll() {
        return ResponseM.of(getFpDB().findAll());
    }

    @Override
    default ResponseM<FpModel> upsert(final FpModel model,
                                      final FpModelQuery query,
                                      final FpAuthority authority) {
        final ResponseM<FpModel> found = validate(query).flatMap(_nothing -> ResponseM.of(getFpDB().findOp(query)));
        return found
                .flatMap(_found -> ResponseM.of(getFpDB().updateOp(_found, query)))
                .orElseGet(() -> found.orElse(ResponseM.of(getFpDB().insertOp(model))));
    }
}