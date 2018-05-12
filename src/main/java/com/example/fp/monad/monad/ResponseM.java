package com.example.fp.monad.monad;

import com.example.fp.monad.AnyM;
import com.example.fp.monad.Monadic;
import com.example.fp.monad.Witness;
import com.example.fp.monad.Witness.responseM;
import com.example.fp.monad.adapter.Adapter;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public interface ResponseM<T> extends AnyM<Witness.responseM, T> {

    @Override
    default Adapter<responseM> adapter() {
        return responseM.INSTANCE.adapter();
    }

    default ResponseEntity<T> toResponseEntity() {
        if (this instanceof ResponseSuccess) {
            return ((ResponseSuccess<T>) this).toResponsibleEntity();
        } else if (this instanceof ResponseFailure) {
            return ((ResponseFailure<T>) this).toResponsibleEntity();
        } else {
            // unreachable code
            return new ResponseFailure<T>(HttpStatus.INTERNAL_SERVER_ERROR, "").toResponsibleEntity();
        }
    }

    static <U> ResponseM<U> of(final U value) {
        return value != null
            ? new ResponseSuccess<>(value)
            : new ResponseFailure<>(HttpStatus.BAD_REQUEST, "");
    }

    static <U> ResponseM<U> of(final Optional<U> value) {
        return value
            .map(ResponseM::of)
            .orElse(failure());
    }

    static <U> ResponseM<U> of(final U value, final HttpStatus status, final String message) {
        return value != null
            ? new ResponseSuccess<>(value)
            : new ResponseFailure<>(status, message);
    }

    static <U> ResponseM<U> failure() {
        return new ResponseFailure<>(HttpStatus.BAD_REQUEST, "");
    }

    static <U> ResponseM<U> failure(final HttpStatus status, final String message) {
        return new ResponseFailure<>(status, message);
    }

    static <R> ResponseM<R> narrow ( final Monadic<Witness.responseM, R> m) {
        return (ResponseM<R>) m;
    }

    default <R> ResponseM<R> flatMapR(Function <? super T, ? extends ResponseM<? extends R>> f) {
        return ResponseM.narrow(this.flatMap(f));
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
            try {
                final R mapped = f.apply(data);
                return mapped != null
                    ? new ResponseSuccess<>(mapped)
                    : new ResponseFailure<>(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to execute flatMap function");
            } catch (Exception e) {
                return new ResponseFailure<>(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to execute flatMap function");
            }
        }

//        private static <R> ResponseM<R> narrow(final Monadic<Witness.responseM, R> m) {
//            return (ResponseM<R>) m;
//        }

        @Override
        public <R> ResponseM<R> flatMap(
            Function<? super T, ? extends Monadic<Witness.responseM, ? extends R>> f) {
            try {
                return narrow(Monadic.cast(f.apply(data)));
            } catch (Exception e) {
                return new ResponseFailure<>(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to execute flatMap function");
            }
        }

        public ResponseEntity<T> toResponsibleEntity () {
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
        public <R> ResponseM<R> map(Function<? super T, ? extends R> f) {
            return new ResponseFailure<>(status, message);
        }

        @Override
        public <R> ResponseM<R> flatMap(
            Function<? super T, ? extends Monadic<Witness.responseM, ? extends R>> f) {
            return new ResponseFailure<>(status, message);
        }

        private final String MESSAGE_HEADER = "FP-Message";
        public ResponseEntity<T> toResponsibleEntity () {
            final MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add(MESSAGE_HEADER, message);
            return new ResponseEntity<>(headers, HttpStatus.OK);
        }
    }
}