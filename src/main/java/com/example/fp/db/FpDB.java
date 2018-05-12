package com.example.fp.db;

import com.example.fp.model.FpModel;
import com.example.fp.model.FpModelQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FpDB extends DB<FpModel, FpModelQuery> {
}