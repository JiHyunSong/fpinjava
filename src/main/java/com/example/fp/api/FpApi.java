package com.example.fp.api;

import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class FpApi implements Api<FpModel, FpModelQuery> {
    @Override
    public CompletableFuture<Optional<List<FpModel>>> findAll() {
        return null;
    }

    @Override
    public CompletableFuture<Optional<FpModel>> find(FpModelQuery query) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<FpModel>> insert(FpModel value) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<FpModel>> update(FpModel value, FpModelQuery query) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<FpModel>> delete(FpModelQuery query) {
        return null;
    }
}
