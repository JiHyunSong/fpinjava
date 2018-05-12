package com.example.fp.monad.adapter;

import com.example.fp.common.LogHelper;
import com.example.fp.monad.Monadic;
import com.example.fp.monad.WitnessType;

import java.util.Objects;
import java.util.Optional;

public interface Adapter<W extends WitnessType<W>> {
    <U> Monadic<W, U> empty();
    <U> Monadic<W, U> unit(final U value);
}