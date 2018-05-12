package com.example.fp.db;

import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;

import java.util.List;

public class FpDBBySuper implements FpDB {
    @Override
    public List<FpModel> findAll() {
        return null;
    }

    @Override
    public FpModel find(FpModelQuery query) {
        return null;
    }

    @Override
    public FpModel insert(FpModel value) {
        return null;
    }

    @Override
    public FpModel update(FpModel value, FpModelQuery query) {
        return null;
    }

    @Override
    public FpModel delete(FpModelQuery query) {
        return null;
    }
}
