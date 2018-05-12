package com.example.fp.db;

import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;

import java.util.List;

public class FpDBByRole implements FpDB {
    @Override
    public List<FpModel> findAll() {
        return null;
    }

    @Override
    public FpModel find(FpModelQuery query) {
        return null;
    }

    @Override
    public boolean insert(FpModel value) {
        return false;
    }

    @Override
    public boolean update(FpModel value, FpModelQuery query) {
        return false;
    }

    @Override
    public boolean delete(FpModelQuery query) {
        return false;
    }
}
