package com.example.fp.controller;

import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import com.example.fp.service.FpService;
import com.example.fp.service.FpServiceByRole;
import com.example.fp.service.FpServiceBySuper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
public class FpController {
    private final FpService fpServiceByRole;
    private final FpService fpServiceBySuper;

    @Autowired
    public FpController(
            @Qualifier(FpServiceByRole.IDENTIFIER) final FpService fpServiceByRole,
            @Qualifier(FpServiceBySuper.IDENTIFIER) final  FpService fpServiceBySuper) {
        this.fpServiceByRole = fpServiceByRole;
        this.fpServiceBySuper = fpServiceBySuper;
    }

    private FpService fpService(final FpAuthority fpAuthority) {
        return fpAuthority.isSuper() ? fpServiceBySuper : fpServiceByRole;
    }

    @RequestMapping("")
    public <T> ResponseEntity<T> getAll(final FpAuthority fpAuthority) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("")
    public ResponseEntity<FpModel> upsert(
            @RequestBody final FpModel model,
            @RequestParam final FpModelQuery query,
            final FpAuthority fpAuthority) {
        final FpModel upserted = fpService(fpAuthority).upsert(model, query, fpAuthority);
        return new ResponseEntity<>(upserted, HttpStatus.OK);
    }
}