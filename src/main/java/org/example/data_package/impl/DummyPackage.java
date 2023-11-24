package org.example.data_package.impl;

import org.example.data_package.DataPackage;

import java.io.Serializable;

public record DummyPackage(
        Long id,
        Long len,
        Long delayInSec
)implements Serializable {
    public DummyPackage {
        id = 1L;
    }
}
