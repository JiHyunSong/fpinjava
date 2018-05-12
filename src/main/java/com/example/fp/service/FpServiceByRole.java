package com.example.fp.service;

import com.example.fp.api.FpApi;
import com.example.fp.common.AuthorityHelper;
import com.example.fp.db.FpDB;
import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import com.example.fp.monad.Witness;
import com.example.fp.monad.monad.ResponseM;
import com.example.fp.monad.transformer.ResponseT;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service(FpServiceByRole.IDENTIFIER)
public final class FpServiceByRole implements FpService {
    public final static String IDENTIFIER = "FpServiceByRole";

    @Getter
    private final FpDB fpDB;

    @Getter
    private final FpApi fpApi;

    @Autowired
    public FpServiceByRole(final FpDB fpDBByRole, final FpApi fpApi) {
        this.fpDB = fpDBByRole;
        this.fpApi = fpApi;
    }

    private Boolean isAuthorized(final FpModelQuery query, final FpAuthority fpAuthority) {
        return true;
    }

    @Override
    public ResponseT<Witness.completableM, FpModel> upsertByApi(final FpModel model,
                                                                 final FpModelQuery query,
                                                                 final FpAuthority authority) {
        return ResponseT
                .ofO(AuthorityHelper.checkAuthorityByApi(query, authority))
                .orElse(ResponseT.failure(Witness.completableM.INSTANCE, HttpStatus.UNAUTHORIZED, "unauthorized"))
                .flatMap(_nothing -> ResponseT.ofO(AuthorityHelper.someOtherApi(123)))
                .flatMap(_nothing -> FpService.super.upsertByApi(model, query, authority))
                .loggingFailure();
    }

    @Override
    public ResponseM<FpModel> upsert(final FpModel model,
                                     final FpModelQuery query,
                                     final FpAuthority authority) {
        if (isAuthorized(query, authority)) {
            return FpService.super.upsert(model, query, authority);
        } else {
            log.error("Invalid request by user");
            return ResponseM.failure(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
    }
}