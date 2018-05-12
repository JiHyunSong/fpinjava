package com.example.fp.monad.monad;

import com.example.fp.monad.AnyM;
import com.example.fp.monad.Monadic;
import com.example.fp.monad.Witness;
import com.example.fp.monad.Witness.responseM;
import com.example.fp.monad.adapter.Adapter;
import java.util.NoSuchElementException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

public interface ResponseM<T> extends AnyM<Witness.responseM, T> {
    @Override
    default Adapter<responseM> adapter() {
        return responseM.INSTANCE.adapter();
    }

    @Slf4j
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    final class ResponseSuccess<T> implements ResponseM<T> {
        private final T data;

        @Override
        public T get() {
            return data;
        }

        @Override
        public boolean equalsM(Monadic<Witness.responseM, ? extends T> other) {
            return false;
        }


        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public <R> Monadic<Witness.responseM, R> map(Function<? super T, ? extends R> f) {
            return null;
        }

        @Override
        public <R> Monadic<Witness.responseM, R> flatMap(Function<? super T, ? extends Monadic<Witness.responseM, ? extends R>> f) {
            return null;
        }
    }

    @Slf4j
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    final class ResponseFailure<T> implements ResponseM<T> {
        @Override
        public T get() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean equalsM(Monadic<Witness.responseM, ? extends T> other) {
            return false;
        }
]

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public <R> Monadic<Witness.responseM, R> map(Function<? super T, ? extends R> f) {
            return null;
        }

        @Override
        public <R> Monadic<Witness.responseM, R> flatMap(Function<? super T, ? extends Monadic<Witness.responseM, ? extends R>> f) {
            return null;
        }
    }
}