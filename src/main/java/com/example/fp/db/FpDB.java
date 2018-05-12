package com.example.fp.db;

import com.example.fp.model.FpModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class FpDB implements DB<FpModel> {
    @Override
    public List<FpModel> findAll() {
        return null;
    }

    @Override
    public <U> FpModel find(U query) {
        return null;
    }

    @Override
    public boolean insert(FpModel value) {
        return false;
    }

    @Override
    public <U> boolean update(FpModel value, U query) {
        return false;
    }

    @Override
    public <U> boolean delete(U query) {
        return false;
    }
}