package org.example.data_package.impl;

import java.io.Serializable;

public record CancelPackage(
        Long id,
        Long len
) implements Serializable {
    public CancelPackage {
        id = 2L;
    }
}
