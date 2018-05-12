package com.example.fp.monad;

import com.example.fp.monad.adapter.Adapter;
import com.example.fp.monad.adapter.ResponseMAdapter;

public interface Witness {
    enum responseM implements WitnessType<responseM> {
        INSTANCE;

        @Override
        public Adapter<responseM> adapter() {
            return new ResponseMAdapter();
        }
    }
}