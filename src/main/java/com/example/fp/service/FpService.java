package com.example.fp.service;

import com.example.fp.db.FpDB;
import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface FpService {

    FpDB getFpDB();


    default List<FpModel> findAllModelBySomeCond(final Predicate<FpModel> f) {
        return getFpDB().findAll().stream().filter(f).collect(Collectors.toList());
    }

    default Boolean validate(final FpModel model) {
        return true;
    }

    default FpModel upsert(final FpModel model, final FpModelQuery query, final FpAuthority authority) {
        if (!validate(model)) {
            return null;
        }
        try {
            final FpModel found = getFpDB().find(query);
            if (found == null) {
                getFpDB().insert(model);
            } else {
                getFpDB().update(model, query);
            }
            return model;
        } catch (Exception e) {
            return null;
        }
    }
}