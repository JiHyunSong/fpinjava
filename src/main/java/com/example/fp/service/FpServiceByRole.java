package com.example.fp.service;

import com.example.fp.db.FpDB;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(FpServiceByRole.IDENTIFIER)
public final class FpServiceByRole implements FpService {
    public final static String IDENTIFIER = "FpServiceByRole";

    @Getter
    private final FpDB fpDB;

    @Autowired
    public FpServiceByRole(final FpDB fpDB) {
        this.fpDB = fpDB;
    }
}