package com.example.fp.service;

import com.example.fp.db.FpDB;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(FpServiceBySuper.IDENTIFIER)
public final class FpServiceBySuper implements FpService {
    public final static String IDENTIFIER = "FpServiceBySuper";

    @Getter
    private final FpDB fpDB;

    @Autowired
    public FpServiceBySuper(final FpDB fpDBBySuper) {
        this.fpDB = fpDBBySuper;
    }
}