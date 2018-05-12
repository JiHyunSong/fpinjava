package com.example.fp.service;

import com.example.fp.db.FpDB;
import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service(FpServiceByRole.IDENTIFIER)
public final class FpServiceByRole implements FpService {
    public final static String IDENTIFIER = "FpServiceByRole";

    @Getter
    private final FpDB fpDB;

    @Autowired
    public FpServiceByRole(final FpDB fpDBByRole) {
        this.fpDB = fpDBByRole;
    }

    private Boolean isValidQuery(final FpModelQuery query, final FpAuthority fpAuthority) {
        return true;
    }

    @Override
    public Optional<FpModel> upsert(final FpModel model,
                                    final FpModelQuery query,
                                    final FpAuthority authority) {
        if (isValidQuery(query, authority)) {
            return FpService.super.upsert(model, query, authority);
        } else {
            log.error("Invalid request by user");
            return Optional.empty();
        }
    }
}