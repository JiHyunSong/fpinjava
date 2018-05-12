package com.example.fp.monad.adapter;

import com.example.fp.monad.AnyM;
import com.example.fp.monad.Monadic;
import com.example.fp.monad.Witness;
import com.example.fp.monad.monad.ResponseM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class ResponseMAdapter implements Adapter<Witness.responseM> {
    @Override
    public <U> AnyM<Witness.responseM, U> empty() {
        return ResponseM.failure();
    }

    @Override
    public <U> AnyM<Witness.responseM, U> unit(final U value) {
        return ResponseM.of(value);
    }

    @Override
    public <U> ResponseEntity<U> toResponseEntity(final Monadic<Witness.responseM, U> m) {
        return ResponseM.narrow(m).toResponseEntity();
    }

    @Override
    public <U>CompletableFuture<U> toCompletableFuture(final Monadic<Witness.responseM, U> m) {
        return ResponseM.narrow(m).toCompletableFuture();
    }
}