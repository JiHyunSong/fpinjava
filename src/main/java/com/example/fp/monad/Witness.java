package com.example.fp.monad;

import com.example.fp.monad.adapter.Adapter;
import com.example.fp.monad.adapter.CompletableMAdapter;
import com.example.fp.monad.adapter.ResponseMAdapter;

public interface Witness {
    enum responseM implements WitnessType<responseM> {
        INSTANCE;

        private final Adapter<responseM> adapter = new ResponseMAdapter();

        @Override
        public Adapter<responseM> adapter() {
            return adapter;
        }
    }

    enum completableM implements WitnessType<completableM> {
        INSTANCE;

        private final Adapter<completableM> adapter = new CompletableMAdapter();

        @Override
        public Adapter<completableM> adapter() {
            return adapter;
        }
    }
}