package org.example.util;

import org.example.metadata.PacketMetadata;

import java.io.IOException;
import java.util.Arrays;


public class PacketUtil {

    public static int getPacketDelayInSec(byte[] bytes) throws IOException {
        final int packetId = ByteUtil.
                byteBlockAsIntLE(bytes);
        final int delay = switch (packetId){
            case 1 -> {
                yield ByteUtil.byteBlockAsIntLE(
                        Arrays.copyOfRange(bytes,
                                PacketMetadata.DATA_SEGMENT_BYTES,
                                2 * PacketMetadata.DATA_SEGMENT_BYTES));
            }
            case 2 -> 0;
            default -> throw new
                    IllegalStateException("Unexpected id: " +
                    packetId + ByteUtil.byteBlockAsIntLE(bytes));
        };
        return delay;
    }

    public static int getPacketIdAsInt(byte[] bytes){
        final int id = ByteUtil.byteBlockAsIntLE(
          Arrays.copyOfRange(bytes,
                  2 * PacketMetadata.DATA_SEGMENT_BYTES,
                  3 * PacketMetadata.DATA_SEGMENT_BYTES)
        );
        return id;
    }

    public static int getPacketLenAsInt(byte[] bytes){
        final int packetId = ByteUtil.
                byteBlockAsIntLE(bytes);
        final int len = switch (packetId){
            case 1 -> 16;
            case 2 -> 12;
            default -> throw new
                    IllegalArgumentException("Unexpected id: " +
                    packetId + ByteUtil.byteBlockAsIntLE(bytes));
        };
        return len;
    }
}
