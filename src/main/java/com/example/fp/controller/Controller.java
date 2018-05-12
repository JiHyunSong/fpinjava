package com.example.fp.controller;

import com.example.fp.model.FpAuthority;
import com.example.fp.service.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public interface Controller<T, U> {
    Service<T, U> getService(final FpAuthority authority);

    @RequestMapping("")
    default ResponseEntity<List<T>> getAll(final FpAuthority fpAuthority) {
        return getService(fpAuthority).findAll().toResponseEntity();
    }

    @RequestMapping("")
    default ResponseEntity<T> upsert(
            @RequestBody final T model,
            @RequestParam final U query,
            final FpAuthority fpAuthority) {
        return getService(fpAuthority).upsert(model, query, fpAuthority).toResponseEntity();
    }
}