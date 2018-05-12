package com.example.fp.monad.monad;

import com.example.fp.monad.AnyM;
import com.example.fp.monad.Monadic;
import com.example.fp.monad.Witness;
import com.example.fp.monad.adapter.Adapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class CompletableM<T> implements AnyM<Witness.completableM, T> {
    private final CompletableFuture<T> boxed;

    private CompletableM(final CompletableFuture<T> c) {
        this.boxed = c;
    }

    public <R> CompletableM<R> map(final Function<? super T, ? extends R> f) {
        return mapM(f);
    }

    public <R> CompletableM<R> flatMap(final Function<? super T, ? extends CompletableM<? extends R>> f) {
        return flatMapM(f);
    }

    public CompletableM<T> orElse(final CompletableM<? extends T> other) {
        return CompletableM.narrow(orElseM(other));
    }

    public CompletableM<T> orElseGet(final Supplier<CompletableM<? extends T>> other) {
        return CompletableM.narrow(orElseGetM(() -> CompletableM.wide(other.get())));
    }

    public ResponseEntity<T> toResponseEntity() {
        throw new NoSuchElementException();
    }

    public CompletableFuture<T> toCompletableFuture() {
        return unwrap();
    }

    /*
     * Overrides WitnessType methods
     */
    @Override
    public Adapter<Witness.completableM> adapter() {
        return Witness.completableM.INSTANCE.adapter();
    }


    /*
     * Additional methods
     */
    public CompletableFuture<T> unwrap() {
        return boxed;
    }

    public static <U> CompletableM<U> narrow(final Monadic<Witness.completableM, U> widen) {
        return (CompletableM<U>) widen;
    }

    public static <U> Monadic<Witness.completableM, U> wide(final CompletableM<U> narrowed) {
        return narrowed;
    }


    /*
     * Overrides AnyM methods
     */
    @Override
    public T get() {
        if (unwrap().isDone()) {
            try {
                return unwrap().get();
            } catch (Throwable th) {
                log.error("Failed to get from boxed even the result of isDone is true, ", th);
                throw new NoSuchElementException("Failed to call CompletableFuture.get().");
            }
        } else {
            throw new NoSuchElementException("CompletableFuture not has been finished.");
        }
    }

    @Override
    public boolean equalsM(final Monadic<Witness.completableM, ? extends T> other) {
        final CompletableM<T> narrowed = narrow(Monadic.cast(other));
        if (unwrap() == null || narrowed.unwrap() == null) {
            log.error("Failed to compare, unwrapped value is null, this : {}, other : {}", unwrap(), narrowed.unwrap());
            return false;
        } else if ((unwrap().isCancelled() && narrowed.unwrap().isCancelled())
                || (unwrap().isCompletedExceptionally() && narrowed.unwrap().isCompletedExceptionally())) {
            log.warn("'this' and 'other' are both failed, it will return true but those might have different exception.");
            return true;
        } else if (unwrap().isDone() && narrowed.unwrap().isDone()) {
            try {
                return Objects.deepEquals(unwrap().get(), narrowed.unwrap().get());
            } catch (Throwable th) {
                log.error("Failed to compare, it might be caused by failure of CompletableFuture.get, ", th);
                return false;
            }
        } else {
            return false;
        }
    }


    /*
     * Overrides Monadic interfaces' methods
     */
    @Override
    public <R> CompletableM<R> mapM(final Function<? super T, ? extends R> f) {
        final CompletableFuture<R> mapped = unwrap().thenCompose(t -> {
            try {
                return CompletableFuture.completedFuture(f.apply(t));
            } catch (Throwable th) {
                log.error("Failed to execute mapper, ", th);
                return Adapter.failedCompletableFuture();
            }
        });
        return of(mapped);
    }

    @Override
    public <R> CompletableM<R> flatMapM(final Function<? super T, ? extends Monadic<Witness.completableM, ? extends R>> f) {
        final CompletableFuture<R> mapped = unwrap().thenCompose(t -> {
            try {
                final CompletableM<R> r = narrow(Monadic.cast(f.apply(t)));
                return r.unwrap();
            } catch (Throwable th) {
                log.error("Failed to execute mapper, ", th);
                return Adapter.failedCompletableFuture();
            }
        });
        return of(mapped);
    }

    @Override
    public boolean isPresent() {
        return !(unwrap().isCompletedExceptionally() || unwrap().isCancelled());
    }


    /*
     * Java native methods
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CompletableM) {
            try {
                return equalsM((CompletableM<T>) o);
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
    public static <U> CompletableM<U> failure(final Throwable th) {
        return CompletableM.of(Adapter.failedCompletableFuture(th));
    }

    public static <U> CompletableM<U> failure() {
        return CompletableM.of(Adapter.failedCompletableFuture());
    }

    public static <U> CompletableM<U> of(final U value) {
        if (Objects.isNull(value)) {
            return CompletableM.of(Adapter.failedCompletableFuture(new NullPointerException("value is null")));
        } else {
            return new CompletableM<>(CompletableFuture.completedFuture(value));
        }
    }

    public static <U> CompletableM<U> of(final CompletableFuture<U> value) {
        return new CompletableM<>(value);
    }
}
