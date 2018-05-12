package com.example.fp.monad.monad;

import com.example.fp.monad.AnyM;
import com.example.fp.monad.Monadic;
import com.example.fp.monad.Witness;
import com.example.fp.monad.adapter.Adapter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ResponseM<T> extends AnyM<Witness.responseM, T> {
    @Override
    default Adapter<Witness.responseM> adapter() {
        return Witness.responseM.INSTANCE.adapter();
    }

    default <R> ResponseM<R> map(final Function<? super T, ? extends R> f) {
        return ResponseM.narrow(mapM(f));
    }

    default <R> ResponseM<R> flatMap(final Function<? super T, ? extends ResponseM<? extends R>> f) {
        return ResponseM.narrow(flatMapM(f));
    }

    default ResponseM<T> orElse(final ResponseM<? extends T> other) {
        return ResponseM.narrow(orElseM(ResponseM.wide(other)));
    }

    default ResponseM<T> orElseGet(final Supplier<ResponseM<? extends T>> other) {
        return ResponseM.narrow(orElseGetM(() -> ResponseM.wide(other.get())));
    }

    default ResponseEntity<T> toResponseEntity() {
        if (this instanceof ResponseSuccess) {
            return ((ResponseSuccess<T>) this).toResponseEntity();
        } else if (this instanceof ResponseFailure) {
            return ((ResponseFailure<T>) this).toResponseEntity();
        } else {
            return new ResponseFailure<T>(HttpStatus.INTERNAL_SERVER_ERROR, "").toResponseEntity();
        }
    }

    default CompletableFuture<T> toCompletableFuture() {
        if (this instanceof ResponseM.ResponseSuccess) {
            return CompletableFuture.completedFuture((this).get());
        } else if (this instanceof ResponseM.ResponseFailure) {
            return Adapter.failedCompletableFuture();
        } else {
            return Adapter.failedCompletableFuture();
        }
    }

    default ResponseM<T> peek(Consumer<T> s, BiConsumer<HttpStatus, String> f) {
        return this;
    }

    static <U> ResponseM<U> of(final Optional<U> value) {
        return value.map(ResponseM::of).orElseGet(ResponseM::failure);
    }

    static <U> ResponseM<U> of(final U value) {
        return value != null
                ? new ResponseSuccess<>(value)
                : new ResponseFailure<>(HttpStatus.BAD_REQUEST, "");
    }

    static <U> ResponseM<U> of(final U value, final HttpStatus status, final String message) {
        return value != null
                ? new ResponseSuccess<>(value)
                : new ResponseFailure<>(status, message);
    }

    static <U> ResponseM<U> of(final U value, final ResponseFailure<U> failure) {
        return value != null
                ? new ResponseSuccess<>(value)
                : failure;
    }

    static <U> ResponseM<U> failure(final HttpStatus status, final String message) {
        return new ResponseFailure<>(status, message);
    }

    static <T, U> ResponseM<U> failure(final ResponseFailure<T> failure) {
        return new ResponseFailure<>(failure.status, failure.message);
    }

    static <U> ResponseM<U> failure() {
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, "");
    }

    static <U> ResponseM<U> badRequest(final String message) {
        return failure(HttpStatus.BAD_REQUEST, message);
    }

    static <U> ResponseM<U> unauthorized(final String message) {
        return failure(HttpStatus.UNAUTHORIZED, message);
    }

    static <R> ResponseM<R> narrow(final Monadic<Witness.responseM, R> widen) {
        return (ResponseM<R>) widen;
    }

    static <R> Monadic<Witness.responseM, R> wide(final ResponseM<R> narrowed) {
        return narrowed;
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
        public <R> ResponseM<R> mapM(final Function<? super T, ? extends R> f) {
            try {
                final R mapped = f.apply(data);
                return mapped != null
                        ? new ResponseSuccess<>(mapped)
                        : new ResponseFailure<>(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to execute flatMap function");
            } catch (Exception e) {
                return new ResponseFailure<>(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to execute flatMap function");
            }
        }

        @Override
        public <R> ResponseM<R> flatMapM(final Function<? super T, ? extends Monadic<Witness.responseM, ? extends R>> f) {
            try {
                return narrow(Monadic.cast(f.apply(data)));
            } catch (Exception e) {
                return new ResponseFailure<>(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to execute flatMap function");
            }
        }

        @Override
        public ResponseEntity<T> toResponseEntity() {
            return new ResponseEntity<>(data, HttpStatus.OK);
        }
    }

    @Slf4j
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    final class ResponseFailure<T> implements ResponseM<T> {
        private final HttpStatus status;
        private final String message;

        @Override
        public T get() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean equalsM(Monadic<Witness.responseM, ? extends T> other) {
            return false;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public <R> Monadic<Witness.responseM, R> mapM(Function<? super T, ? extends R> f) {
            return new ResponseFailure<>(status, message);
        }

        @Override
        public <R> ResponseM<R> flatMapM(Function<? super T, ? extends Monadic<Witness.responseM, ? extends R>> f) {
            return new ResponseFailure<>(status, message);
        }

        private final String MESSAGE_HEADER = "FP-Message";
        @Override
        public ResponseEntity<T> toResponseEntity() {
            final MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add(MESSAGE_HEADER, message);
            return new ResponseEntity<>(headers, status);
        }
    }
}