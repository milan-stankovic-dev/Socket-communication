package org.example.data_package;

import java.io.Serializable;

public record CancelPackage(
        Long id,
        Long len
) implements Serializable { }
