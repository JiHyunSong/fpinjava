package com.example.fp.controller;

import com.example.fp.model.FpAuthority;
import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import com.example.fp.service.FpService;
import com.example.fp.service.FpServiceByRole;
import com.example.fp.service.FpServiceBySuper;
import com.example.fp.service.Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface Controller<T, U> {

    Service<T, U> getService(final FpAuthority fpAuthority);

    @RequestMapping("")
    default ResponseEntity<List<T>> findAll(final FpAuthority fpAuthority) {
        return new ResponseEntity<>(getService(fpAuthority).findAll(), HttpStatus.OK);
    }

    @RequestMapping("")
    default ResponseEntity<T> upsert(
        @RequestBody final T model,
        @RequestParam final U query,
        final FpAuthority fpAuthority) {
        final T upserted = getService(fpAuthority).upsert(model, query, fpAuthority);
        return new ResponseEntity<>(upserted, HttpStatus.OK);
    }
}