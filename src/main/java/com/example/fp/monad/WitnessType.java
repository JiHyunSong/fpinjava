package com.example.fp.monad;

import com.example.fp.monad.adapter.Adapter;

public interface WitnessType<W extends WitnessType<W>> {
    Adapter<W> adapter();
}