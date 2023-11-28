package org.example.util;

import org.example.packet.Packet;

import java.io.IOException;
import java.util.Arrays;

import static org.example.metadata.PacketMetadata.*;
import static org.example.util.ByteUtil.byteBlockAsIntLE;

public class PacketUtil {

    public static int getPacketDelayInSec(byte[] bytes) throws IOException, IllegalStateException {
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


    public static int getPacketIdAsInt(byte[] bytes){
        final int id = byteBlockAsIntLE(
          Arrays.copyOfRange(bytes,
                  2 * DATA_SEGMENT_BYTES,
                  3 * DATA_SEGMENT_BYTES)
        );
        return id;
    }

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

    public static int getPacketTypeIdAsInt(byte[] bytes){
        final int id = byteBlockAsIntLE(
                Arrays.copyOfRange(bytes,
                        0,
                         DATA_SEGMENT_BYTES)
        );
        return id;
    }

    public static boolean isPacketStale(Packet packet) throws IOException {
        final long packetDelayInSec = getPacketDelayInSec(packet.bytes());
        final long currentTimeMillis = System.currentTimeMillis();
        final long timeStampReceived = packet.receivedAt().getTime();
        final long timePassedMillis = currentTimeMillis - timeStampReceived;
        final long timePassedSeconds = timePassedMillis / 1000;

        return timePassedSeconds >= packetDelayInSec;
    }


    public static boolean isPacketCorrupted(Packet packet) {
        final int packetId = getPacketTypeIdAsInt(packet.bytes());
        return (packetId != CANCEL_PACKET_ID) && (packetId != DUMMY_PACKET_ID);
    }

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

    private static int getPacketDelayInSecOld(byte[] bytes) throws IOException {
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
