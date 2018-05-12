package com.example.fp.controller;

import com.example.fp.model.FpAuthority;
import com.example.fp.service.Service;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public interface Controller<T, U> {
    // 여길 optional로 하면 null pointer exception을 안뱉는다.
    Service<T, U> getService(final FpAuthority authority);

    default <R> ResponseEntity<R> toResponseEntity(
        final Optional<R> op,
        final ResponseEntity<R> failure
    ) {
        return op.map(r -> new ResponseEntity<>(r, HttpStatus.OK))
            .orElse(failure);
    }

    @RequestMapping("")
    default ResponseEntity<List<T>> getAll(final FpAuthority fpAuthority) {
        return getService(fpAuthority).findAll()
            .map(found -> new ResponseEntity<>(found, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping("")
    default ResponseEntity<T> upsert(
            @RequestBody final T model,
            @RequestParam final U query,
            final FpAuthority fpAuthority) {
        return getService(fpAuthority)
            .upsert(model, query, fpAuthority)
            .map(_model -> new ResponseEntity<>(_model, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}