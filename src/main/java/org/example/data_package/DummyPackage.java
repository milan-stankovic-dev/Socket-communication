package org.example.data_package;

import java.io.Serializable;

public record DummyPackage(
        Long id,
        Long len,
        Long delayInSec
) implements Serializable { }
