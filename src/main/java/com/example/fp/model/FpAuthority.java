package com.example.fp.model;

import lombok.Data;

@Data
public class FpAuthority {
    public boolean isSuper() {
        return true;
    }
}