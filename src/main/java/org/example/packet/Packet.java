package org.example.packet;

import java.sql.Timestamp;

/**
 * Wrapper type for packet byte data and timestamp of
 * receipt
 * @param bytes packet byte data
 * @param receivedAt timestamp of receipt
 */

public record Packet(
        byte[] bytes,
        Timestamp receivedAt
){ }
