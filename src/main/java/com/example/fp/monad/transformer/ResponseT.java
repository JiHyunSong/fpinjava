package com.example.fp.monad.transformer;

import com.example.fp.FpApplication;
import com.example.fp.monad.Monadic;
import com.example.fp.monad.Witness;
import com.example.fp.monad.WitnessType;
import com.example.fp.monad.monad.CompletableM;
import com.example.fp.monad.monad.ResponseM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

@Slf4j
public class ResponseT<W extends WitnessType<W>, T> {
    private final Monadic<W, ResponseM<T>> run;

    private ResponseT(Monadic<W, ResponseM<T>> run) {
        this.run = run;
    }


    /*
     * Overrides WitnessType methods
     */
    public Monadic<W, ResponseM<T>> unwrap() {
        return run;
    }


    /*
     * Additional methods and interface methods
     */
    // TODO, have to find appropriate way to avoid this kind of casting
    public static <W extends WitnessType<W>, U> ResponseT<W, U> cast(final ResponseT<W, ? extends U> t) {
        return t.map(id -> id);
    }

    public <U, R> ResponseT<W, R> zipWith(final ResponseT<W, U> z1, final BiFunction<T, U, ResponseT<W, R>> f) {
        return flatMap(t -> z1.flatMap(u -> f.apply(t, u)));
    }

    static public <W extends WitnessType<W>, T, U, R> ResponseT<W, R> zip(final ResponseT<W, T> z1, final ResponseT<W, U> z2, final BiFunction<T, U, ResponseT<W, R>> f) {
        return z1.flatMap(t -> z2.flatMap(u -> f.apply(t, u)));
    }


    /*
     * Overrides Monadic interfaces' methods
     */
    public <R> ResponseT<W, R> map(final Function<? super T, ? extends R> f) {
        final Monadic<W, ResponseM<R>> mapped = unwrap().mapM(r -> {
            final Monadic<Witness.responseM, ? extends R> innerMapped = r.mapM(f);
            return ResponseM.narrow(Monadic.cast(innerMapped));
        });
        return of(mapped);
    }

    private <R> ResponseT<W, R> flatMapR(final Function<? super T, ResponseT<W, R>> f) {
        final Function<? super ResponseM<T>, Monadic<W, ResponseM<R>>> helper = r -> {
            if (r instanceof ResponseM.ResponseSuccess) {
                return f.apply(r.get()).unwrap();
            } else if (r instanceof ResponseM.ResponseFailure) {
                return unwrap().adapter().unit(ResponseM.failure((ResponseM.ResponseFailure<T>) r));
            } else {
                log.error("Failed to execute flatMapR, ResponseM isn't ResponseSuccess nor ResponseFailure, {}", FpApplication.getStackTrace());
                return unwrap().adapter().unit(ResponseM.failure(HttpStatus.INTERNAL_SERVER_ERROR, "error.unknown"));
            }
        };
        return of(unwrap().flatMapM(helper));
    }

    public <R> ResponseT<W, R> flatMap(Function<? super T, ResponseT<W, ? extends R>> f) {
        return flatMapR(t -> {
            try {
                return cast(f.apply(t));
            } catch (Throwable th) {
                log.error("Failed to execute mapper, ", th);
                return of(unwrap().adapter().unit(ResponseM.failure(HttpStatus.INTERNAL_SERVER_ERROR, "error.unknown")));
            }
        });
    }

    private boolean equalsM(final ResponseT<W, ? extends T> other) {
        final ResponseT<W, T> casted = cast(other);
        log.debug("try to call equalsM, {}, {}", this, casted);
        if (this.unwrap() == null || casted == null || casted.unwrap() == null) {
            log.error("Failed to compare, unwrapped or other is null, this.unwrap() : {}, other : {}, other.unwrap : {}, {}",
                    this.unwrap(),
                    other,
                    (casted != null ? casted.unwrap() : "N/A"),
                    FpApplication.getStackTrace());
            return false;
        } else {
            return Objects.deepEquals(this.unwrap(), casted.unwrap());
        }
    }


    /*
     * Interop methods
     */
    public CompletableFuture<ResponseEntity<T>> toComplResponseEntity() {
        return unwrap().adapter().toCompletableFuture(this.run).thenApply(r -> r.adapter().toResponseEntity(r));
    }

    /*
     * Java native methods
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ResponseT) {
            try {
                return equalsM((ResponseT<W, T>) o);
            } catch (Throwable th) {
                log.error("Failed to cast specific monad, o : {}, ", o, th);
                return false;
            }
        } else {
            return false;
        }
    }


    /*
     * Aux methods
     */
    public ResponseT<W, T> orElseGet(final Supplier<ResponseT<W, T>> other) {
        final Monadic<W, ResponseM<T>> mapped = run
                .flatMapM(r -> {
                    if (r.isPresent()) {
                        return unwrap().adapter().unit(r);
                    } else {
                        // Lazy execution
                        try {
                            return other.get().unwrap();
                        } catch (Throwable th) {
                            log.error("Failed to call supplier, ", th);
                            return unwrap().adapter().empty();
                        }
                    }
                }).orElseGetM(() -> {
                    // Lazy execution
                    try {
                        return other.get().unwrap();
                    } catch (Throwable th) {
                        log.error("Failed to call supplier, ", th);
                        return unwrap().adapter().empty();
                    }
                });
        return of(mapped);
    }

    public ResponseT<W, T> orElse(final ResponseT<W, T> other) {
        final Monadic<W, ResponseM<T>> mapped = run
                .flatMapM(r -> {
                    if (r.isPresent()) {
                        return unwrap().adapter().unit(r);
                    } else {
                        return other.unwrap();
                    }
                }).orElseGetM(other::unwrap);
        return of(mapped);
    }

    public ResponseT<W, T> peek(final Consumer<T> succeeded, final BiConsumer<HttpStatus, String> failed) {
        final Monadic<W, ResponseM<T>> peeked = unwrap().mapM(r -> r.peek(succeeded, failed));
        return of(peeked);
    }

    public ResponseT<W, T> logging(final Consumer<T> succeeded, final BiConsumer<HttpStatus, String> failed) {
        return peek(succeeded, failed);
    }

    public ResponseT<W, T> logging() {
        return logging(
                (v) -> log.info("{} : ResponseT succeeded, value : {}", FpApplication.getCaller("NON SALT CONSOLE METHOD"), v),
                (s, m) -> log.info("{} : ResponseT failed, status : {}, message : {}", FpApplication.getCaller(), s, m));
    }

    public ResponseT<W, T> loggingSuccess() {
        return logging(
                (v) -> log.info("{} : ResponseT succeeded, value : {}", FpApplication.getCaller("NON SALT CONSOLE METHOD"), v),
                (s, m) -> {});
    }

    public ResponseT<W, T> loggingFailure() {
        return logging(
                (v) -> {},
                (s, m) -> log.info("{} : ResponseT failed, status : {}, message : {}", FpApplication.getCaller("NON SALT CONSOLE METHOD"), s, m));
    }

    public static <W extends WitnessType<W>, U> ResponseT<W, U> of(final Monadic<W, ResponseM<U>> run) {
        return new ResponseT<>(run);
    }

    public static <W extends WitnessType<W>, U> ResponseT<W, U> failure(final WitnessType<W> witness, final HttpStatus status, final String message) {
        final Monadic<W, ResponseM<U>> failed = witness.adapter().unit(ResponseM.failure(status, message));
        return of(failed);
    }

    public static <W extends WitnessType<W>, U> ResponseT<W, U> internalServerError(final WitnessType<W> witness) {
        final Monadic<W, ResponseM<U>> failed = witness.adapter().unit(ResponseM.failure(HttpStatus.INTERNAL_SERVER_ERROR, "error.unknown"));
        return of(failed);
    }

    public static <W extends WitnessType<W>, U> ResponseT<W, U> unauthorized(final WitnessType<W> witness) {
        final Monadic<W, ResponseM<U>> failed = witness.adapter().unit(ResponseM.failure(HttpStatus.UNAUTHORIZED, "error.unauthorized"));
        return of(failed);
    }

    public static <W extends WitnessType<W>, U> ResponseT<W, U> ok(final WitnessType<W> witness) {
        final Monadic<W, ResponseM<U>> failed = witness.adapter().unit(ResponseM.failure(HttpStatus.OK, ""));
        return of(failed);
    }

    public static <U> ResponseT<Witness.completableM, U> of(final Supplier<U> value) {
        try {
            final U v = value.get();
            return of(v);
        } catch (Throwable th) {
            log.error("Failed to get data from supplier, ", th);
            return internalServerError(Witness.completableM.INSTANCE);
        }
    }

    public static <U> ResponseT<Witness.completableM, U> of(final U value) {
        final Monadic<Witness.completableM, ResponseM<U>> lifted = CompletableM.narrow(
                CompletableM.of(
                        CompletableFuture.completedFuture(
                                ResponseM.of(value))));
        return of(lifted);
    }

    public static <U> ResponseT<Witness.completableM, U> of(final Optional<U> value) {
        final Monadic<Witness.completableM, ResponseM<U>> lifted = CompletableM.narrow(
                CompletableM.of(
                        CompletableFuture.completedFuture(
                                ResponseM.of(value))));
        return of(lifted);
    }

    public static <U> ResponseT<Witness.completableM, U> of(final CompletableFuture<U> value) {
        final Monadic<Witness.completableM, ResponseM<U>> lifted = CompletableM.narrow(
                CompletableM.of(
                        value.thenApply(ResponseM::of).exceptionally((th) -> {
                            log.error("Failed to execute, ", th);
                            return ResponseM.failure();
                        })));
        return of(lifted);
    }

    public static <U> ResponseT<Witness.completableM, U> ofO(final CompletableFuture<Optional<U>> value) {
        final Monadic<Witness.completableM, ResponseM<U>> lifted = CompletableM.narrow(
                CompletableM.of(
                        value.thenApply(ResponseM::of).exceptionally((th) -> {
                            log.error("Failed to execute, ", th);
                            return ResponseM.failure();
                        })));
        return of(lifted);
    }
}
