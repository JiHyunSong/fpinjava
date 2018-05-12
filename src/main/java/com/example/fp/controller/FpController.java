package com.example.fp.controller;

import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import com.example.fp.service.FpService;
import com.example.fp.service.FpServiceByRole;
import com.example.fp.service.FpServiceBySuper;
import com.example.fp.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fp")
public class FpController implements Controller<FpModel, FpModelQuery> {
    private final FpService fpServiceByRole;
    private final FpService fpServiceBySuper;

    @Autowired
    public FpController(
            @Qualifier(FpServiceByRole.IDENTIFIER) final FpService fpServiceByRole,
            @Qualifier(FpServiceBySuper.IDENTIFIER) final  FpService fpServiceBySuper) {
        this.fpServiceByRole = fpServiceByRole;
        this.fpServiceBySuper = fpServiceBySuper;
    }

    @Override
    public FpService getService(final FpAuthority fpAuthority) {
        return fpAuthority.isSuper() ? fpServiceBySuper : fpServiceByRole;
    }

}