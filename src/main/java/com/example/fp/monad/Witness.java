package com.example.fp.monad;

public interface Witness {
    enum responseM implements WitnessType<responseM> {

    }

    enum completableM implements WitnessType<completableM> {

    }
}