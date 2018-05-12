package com.example.fp.monad;

public interface AnyM<W extends WitnessType<W>, T> extends Monadic<W, T> {
    T get();
    boolean equalsM(final Monadic<W, ? extends T> other);
}