package org.example.data_packet.impl;

import java.io.Serializable;

public record CancelPacket(
        long id,
        long len
) implements Serializable {
    public CancelPacket {
        id = 2L;
    }
}
