package org.example.packet;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Packet{
    private byte[] bytes;
    private Timestamp receivedAt;
}
