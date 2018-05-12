package com.example.fp.service;

import com.example.fp.db.FpDB;
import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(FpServiceBySuper.IDENTIFIER)
public final class FpServiceBySuper implements FpService {

    public final static String IDENTIFIER = "FpServiceBySuper";

    @Getter
    private final FpDB fpDB;

    @Autowired
    public FpServiceBySuper(final FpDB fpDB) {
        this.fpDB = fpDB;
    }

    private Boolean isValidQuery(final FpModelQuery query, final FpAuthority authority) {
        return true;
    }


    public FpModel upsert(final FpModel model, final FpAuthority authority) {
        final FpModelQuery query = new FpModelQuery();
        if (isValidQuery(query, authority)) {
            return FpService.super.upsert(model);
        } else {
            log.error("Invalid request by user");
            return null;
        }
    }
}
