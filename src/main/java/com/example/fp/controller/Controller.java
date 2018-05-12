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

    default <R> ResponseEntity<R> toResponseEntity(
            final Optional<R> op,
            final ResponseEntity<R> failure) {
        return op.map(r -> new ResponseEntity<>(r, HttpStatus.OK)).orElse(failure);
    }

    @RequestMapping("")
    default ResponseEntity<List<T>> getAll(final FpAuthority fpAuthority) {
        return toResponseEntity(
                getService(fpAuthority).findAll(),
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping("")
    default ResponseEntity<T> upsert(
            @RequestBody final T model,
            @RequestParam final U query,
            final FpAuthority fpAuthority) {
        return toResponseEntity(
                getService(fpAuthority).upsert(model, query, fpAuthority),
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}