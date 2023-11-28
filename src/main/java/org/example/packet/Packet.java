package org.example.packet;

import java.sql.Timestamp;

public record Packet(
        byte[] bytes,
        Timestamp receivedAt
){ }
