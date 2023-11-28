package org.example.util;

import org.example.packet.Packet;

import java.io.IOException;
import java.util.Arrays;

import static org.example.metadata.PacketMetadata.*;
import static org.example.util.ByteUtil.byteBlockAsIntLE;

/**
 * Utility class for working with packets
 */
public class PacketUtil {

    /**
     * Gives delay for given packet in seconds. Packet must follow conventions.
     * @param bytes bytes of packet
     * @return delay for resending in seconds
     * @throws IllegalStateException if packet does not have valid id
     */
    public static int getPacketDelayInSec(byte[] bytes) throws IllegalStateException {
        final int packetTypeId = getPacketTypeIdAsInt(bytes);

        if (packetTypeId == DUMMY_PACKET_ID) {
            return byteBlockAsIntLE(
                    Arrays.copyOfRange(bytes,
                            DATA_SEGMENT_BYTES,
                            2 * DATA_SEGMENT_BYTES));
        } else if (packetTypeId == CANCEL_PACKET_ID) {
            return 0;
        } else {
            throw new IllegalStateException(new StringBuilder()
                    .append("Unexpected id: ")
                    .append(packetTypeId)
                    .append(byteBlockAsIntLE(bytes)).toString());
        }
    }

    /**
     * Returns id for given packet (unique packet id) as int.
     * @param bytes bytes of packet
     * @return id (unique packet id, not packet type) as int
     */
    public static int getPacketIdAsInt(byte[] bytes){
        final int id = byteBlockAsIntLE(
          Arrays.copyOfRange(bytes,
                  2 * DATA_SEGMENT_BYTES,
                  3 * DATA_SEGMENT_BYTES)
        );
        return id;
    }

    /**
     * Returns packet length as int.
     * @param bytes bytes of packet
     * @return packet length as int
     */
    public static int getPacketLenAsInt(byte[] bytes){
        final int packetId = byteBlockAsIntLE(bytes);
        if (packetId == DUMMY_PACKET_ID) {
            return DUMMY_PACKET_SIZE_BYTES;
        } else if (packetId == CANCEL_PACKET_ID){
            return CANCEL_PACKET_SIZE_BYTES;
        } else {
            throw new
                    IllegalArgumentException(new StringBuilder()
                    .append("Unexpected id: ")
                    .append(packetId)
                    .append(byteBlockAsIntLE(bytes)).toString());
        }
    }

    /**
     * Returns packet type id (not unique packet id) as int
     * @param bytes bytes of packet
     * @return packet type id (not unique packet id) as int
     */
    public static int getPacketTypeIdAsInt(byte[] bytes){
        final int id = byteBlockAsIntLE(
                Arrays.copyOfRange(bytes,
                        0,
                         DATA_SEGMENT_BYTES)
        );
        return id;
    }

    /**
     * Checks if packet delay for resending has passed
     * @param packet wrapper type instance containing bytes and
     *               timestamp of receipt
     * @return true if stale, false otherwise
     */
    public static boolean isPacketStale(Packet packet) {
        final long packetDelayInSec = getPacketDelayInSec(packet.bytes());
        final long currentTimeMillis = System.currentTimeMillis();
        final long timeStampReceived = packet.receivedAt().getTime();
        final long timePassedMillis = currentTimeMillis - timeStampReceived;
        final long timePassedSeconds = timePassedMillis / 1000;

        return timePassedSeconds >= packetDelayInSec;
    }

    /**
     * Checks if packet data is intact and follows standard.
     * @param packet wrapper type instance containing bytes and
     *               timestamp of receipt
     * @return true if corrupted, false otherwise
     */
    public static boolean isPacketCorrupted(Packet packet) {
        final int packetId = getPacketTypeIdAsInt(packet.bytes());
        return (packetId != CANCEL_PACKET_ID) && (packetId != DUMMY_PACKET_ID);
    }

    /**
     * Legacy version of 'getPacketLenAsInt'. Usage discouraged.
     * @param bytes bytes of packet
     * @return packet length as int
     */
    private static int getPacketLenAsIntOld(byte[] bytes){
        final int packetId = byteBlockAsIntLE(bytes);
        final int len = switch (packetId){
            case 1 -> 16;
            case 2 -> 12;
            default -> throw new
                    IllegalArgumentException("Unexpected id: " +
                    packetId + byteBlockAsIntLE(bytes));
        };
        return len;
    }

    /**
     * Legacy version of 'getPacketDelayInSec'. Usage discouraged.
     * @param bytes bytes of packet
     * @return packet delay for resending in seconds
     */
    private static int getPacketDelayInSecOld(byte[] bytes) {
        final int packetId = getPacketIdAsInt(bytes);
        final int delay = switch (packetId){
            case 1 -> byteBlockAsIntLE(
                    Arrays.copyOfRange(bytes,
                            DATA_SEGMENT_BYTES,
                            2 * DATA_SEGMENT_BYTES));
            case 2 -> 0;
            default -> throw new
                    IllegalStateException("Unexpected id: " +
                    packetId + byteBlockAsIntLE(bytes));
        };
        return delay;
    }
}
