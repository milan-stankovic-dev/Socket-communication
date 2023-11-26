package org.example.communication;

import lombok.Getter;
import org.example.metadata.PacketMetadata;
import org.example.util.ByteUtil;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

public class PacketReader {
    private final DataInputStream inputStream;
    @Getter
    private final DataOutputStream outputStream;
    private static final Logger logger = Logger.getLogger(PacketReader.class.getName());
    public PacketReader() throws IOException{
            final Socket socket = MyConnectionFactory.getInstance()
                    .establishConnection();
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public int readPacketId() throws IOException {
        final byte[] idBytes = new byte[PacketMetadata.DATA_SEGMENT_BYTES];
        int bytesRead = this.inputStream.read(idBytes);

        if (bytesRead == -1) {
            throw new IOException("End of stream reached while reading idBytes");
        } else if (bytesRead != PacketMetadata.DATA_SEGMENT_BYTES) {
            throw new IOException("Unexpected number of bytes read for idBytes");
        }

        return ByteUtil.byteBlockAsIntLE(idBytes);
    }

    public byte[] readPacketFully() throws IOException {
        final int packetId = readPacketId();
        final int dataLength = (packetId == PacketMetadata.DUMMY_PACKET_ID) ?
                (PacketMetadata.DUMMY_PACKET_SIZE_BYTES - PacketMetadata.DATA_SEGMENT_BYTES):
                ((packetId == PacketMetadata.CANCEL_PACKET_ID) ?
                        PacketMetadata.CANCEL_PACKET_SIZE_BYTES - PacketMetadata.DATA_SEGMENT_BYTES : -1);

        if (dataLength == -1) {
            logger.warning("UNKNOWN PACKET TYPE (id =" + packetId + ")");
            throw new IOException("Could not read packet.");
        }

        System.out.println(packetId);

        final byte[] packetBytes = new byte[dataLength];
        this.inputStream.readFully(packetBytes);


        logger.finest("PACKET ID: " + packetId);
        logger.finest("PACKET DATA: ");
        ByteUtil.printDataBits(packetBytes);

        if (packetId == 1) {
            logger.finest("PACKET TYPE: Dummy");
        } else {
            logger.finest("PACKET TYPE: Cancel");
        }

        return ByteUtil.concatenateByteArrays(
                ByteUtil.intAsByteBlockLE(packetId),
                packetBytes
        );

    }

}
