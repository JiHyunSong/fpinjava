package com.example.fp.service;

import com.example.fp.db.FpDB;
import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;

import com.example.fp.monad.Monadic;
import com.example.fp.monad.Witness;
import com.example.fp.monad.Witness.responseM;
import com.example.fp.monad.monad.ResponseM;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

public interface FpService extends Service<FpModel, FpModelQuery> {
    FpDB getFpDB();

    default List<FpModel> findAllModelBySomeCond(final Predicate<FpModel> f) {
        return getFpDB().findAll().stream().filter(f).collect(Collectors.toList());
    }

    default ResponseM<FpModelQuery> validate(final FpModelQuery query) {
//        return ResponseM.of(query);
        return ResponseM.failure(HttpStatus.BAD_REQUEST, "not valid field name xyz");
    }

    @Override
    default ResponseM<List<FpModel>> findAll() {
        return ResponseM.of(getFpDB().findAll());
    }

    @Override
    default ResponseM<FpModel> upsert(final FpModel model,
                                     final FpModelQuery query,
                                     final FpAuthority authority) {

//        final Function<FpModelQuery, Monadic<responseM, FpModel>> aux = _query -> {
//            final ResponseM<FpModel> found = ResponseM.of(getFpDB().findOp(_query));
//            return found.isPresent()
//                    ? found.flatMap(_found -> ResponseM.of(getFpDB().updateOp(_found, _query)))
//                    : ResponseM.of(getFpDB().insertOp(model));
//        };

        final Function <FpModelQuery, ResponseM<FpModel>> aux = _query -> {
            final ResponseM<FpModel> found = ResponseM.of(getFpDB().findOp(_query));
            return found.isPresent()
                ? found.flatMapR(_found -> ResponseM.of(getFpDB().updateOp(_found, _query)))
                : ResponseM.of(getFpDB().insertOp(model));
        };
        return ResponseM.narrow(validate(query).flatMap(aux));
    }
}