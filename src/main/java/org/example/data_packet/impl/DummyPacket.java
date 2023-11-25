package org.example.data_packet.impl;

import java.io.Serializable;

public record DummyPacket(
        long id,
        long len,
        long delayInSec
)implements Serializable {
    public DummyPacket {
        id = 1L;
        len = 16;
    }
}
