package com.example.fp.monad.adapter;

import com.example.fp.monad.Monadic;
import com.example.fp.monad.Witness;
import com.example.fp.monad.monad.CompletableM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class CompletableMAdapter implements Adapter<Witness.completableM> {
    @Override
    public <U> Monadic<Witness.completableM, U> empty() {
        return CompletableM.failure();
    }

    @Override
    public <U> Monadic<Witness.completableM, U> unit(final U value) {
        return CompletableM.of(value);
    }

    @Override
    public <U> ResponseEntity<U> toResponseEntity(final Monadic<Witness.completableM, U> m) {
        return CompletableM.narrow(m).toResponseEntity();
    }

    @Override
    public <U>CompletableFuture<U> toCompletableFuture(final Monadic<Witness.completableM, U> m) {
        return CompletableM.narrow(m).toCompletableFuture();
    }
}